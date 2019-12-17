package propra.imageconverter.image.compression.rle;

import propra.imageconverter.binary.LittleEndianInputStream;
import propra.imageconverter.image.compression.CompressionReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Implementiert einen CompressionReader für
 * RLE-komprimierte Daten.
 */
public class RLECompressionReader implements CompressionReader {
    private final LittleEndianInputStream inputStream;
    // Speichert im Fall, dass sich der Reader
    // im Wiederholungsmodus befindet, den wiederholten
    // Bildpunkt.
    private final byte[] repeatedPixel = new byte[3];
    // Speichert die verbleibenden unkomprimierten Bildpunkte bis zum
    // nächsten Steuerbyte; falls > 0 befindet sich der Reader im Datenmodus
    private int remainingNonRepeatedPixels = 0;
    // Speichert die verbleibenden Wiederholungen des gespeicherten
    // Bildpunkts; falls > 0 befindet sich der Reader im Wiederholungsmodus
    private int remainingRepetitions = 0;


    public RLECompressionReader(InputStream inputStream) {
        this.inputStream = new LittleEndianInputStream(inputStream);
    }

    @Override
    public byte[] readNextPixel() throws IOException {
        // Reader befindet sich im Wiederholungsmodus
        if (remainingRepetitions > 0) {
            this.remainingRepetitions--;

            // Wir machen mal wieder Defensive Programmierung
            // und geben nur eine Kopie des Arrays zurück, damit
            // der Aufrufer unser Array nicht überschreibt.
            return Arrays.copyOf(this.repeatedPixel, 3);
        }

        // Reader befindet sich im Datenmodus
        if (remainingNonRepeatedPixels > 0) {
            byte[] pixel = new byte[3];

            inputStream.readFully(pixel);

            this.remainingNonRepeatedPixels--;

            // Hier keine Kopie, da wir unser Byte-Array
            // weiter nutzen.
            return pixel;
        }

        // Aktuelles Byte ist ein Kontrollbyte,
        // da aktuell kein Datenzähler oder Wiederholung
        // aktiv ist.
        int controlByte = inputStream.readUByte();

        // Datenzähler oder Wiederholungszähler
        // wird durch das 8. Bit bestimmt
        // 0 -> Datenzähler
        // 1 -> Wiederholungszähler
        boolean isRepetitionCounter = (controlByte & 0b1000_0000) > 0;

        // Behandlung des Wiederholungszählers
        if (isRepetitionCounter) {
            // Der Bildpunkt wird nach dem Zähler in den unteren 7 Bits
            // plus 1 wiederholt.
            // Wir speichern den Wert und emittieren nun für die nächsten
            // this.remainingRepetitions den gleichen Bildpunkt.
            this.remainingRepetitions = (controlByte & 0b0111_1111) + 1;

            // Wiederholten Bildpunkt abspeichern und emittieren
            // bis alle Wiederholungen "abgearbeitet" wurden.
            inputStream.readFully(this.repeatedPixel);

            // Rekursiver Aufruf, damit rufen wir garantiert
            // aber nur den nächsten Bildpunkt ab (da this.remainingRepetitions nun größer 0 ist).
            // Eine Endlosschleife ist zumindest nach der Programmlogik nicht möglich.
            return readNextPixel();
        }

        // Ansonsten: Behandlung des Datenzählers

        // Die Anzahl der Bildpunkte ohne Wiederholung; wird
        // aus den unteren 7 Bits + 1 gelesen; das 8. Bit ist
        // 0, da es sich um den Datenzähler handelt.
        this.remainingNonRepeatedPixels = controlByte + 1;

        // Rekursiver Aufruf, damit rufen wir garantiert
        // aber nur den nächsten Bildpunkt ab (da this.remainingNonRepeatedPixels nun größer 0 ist).
        // Eine Endlosschleife ist zumindest nach der Programmlogik nicht möglich.
        return readNextPixel();
    }
}

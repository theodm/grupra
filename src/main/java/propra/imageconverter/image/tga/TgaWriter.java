package propra.imageconverter.image.tga;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Ermöglicht das pixelweise Schreiben einer TGA-Datei.
 * <p>
 * Der Benutzer ist angehalten, die Instanz nach dem Schreiben
 * wieder zu schließen.
 */
public final class TgaWriter implements ImageWriter {
    private final ReadWriteFile readWriteFile;
    private final LittleEndianOutputStream outputStream;

    private TgaWriter(
            ReadWriteFile readWriteFile,
            LittleEndianOutputStream outputStream
    ) {
        this.readWriteFile = readWriteFile;
        this.outputStream = outputStream;
    }

    /**
     * Erstellt einen TgaWriter mit den gegebenen Dimensionen.
     */
    public static TgaWriter create(
            ReadWriteFile readWriteFile,
            int width,
            int height
    ) throws IOException {
        LittleEndianOutputStream outputStream = readWriteFile.outputStream(0);

        outputStream.writeUByte(0); // Länge der Bild-ID
        outputStream.writeUByte(0); // Palettentyp
        outputStream.writeUByte(2); // Bildtyp, nur unterstützt 2 = RGB(24 oder 32 Bit) unkomprimiert
        outputStream.writeFully(new byte[] { 0, 0, 0, 0, 0 }); // Palletenbeginn, Palettenlänge und Palettengröße immer 0, da keine Palette vorhanden
        outputStream.writeUShort(0); // X-Koordinate für Nullpunkt, siehe parse()
        outputStream.writeUShort(height); // Y-Koordinate für Nullpunkt
        outputStream.writeUShort(width); // Breite des Bilds
        outputStream.writeUShort(height); // Länge des Bilds
        outputStream.writeUByte(24); // Bits pro Bildpunkt, nach Vorgabe immer 24
        outputStream.writeUByte(0b00100000); // Attribut-Byte, nach Vorgabe, siehe parse()

        return new TgaWriter(readWriteFile, outputStream);
    }

    @Override
    public void writeNextPixel(byte[] rgbPixel) throws IOException {
        // Wir kopieren hier den übertragenen Pixel,
        // damit wir das Byte-Array des Aufrufers nicht Ausversehen
        // überscheiben (Defensive Programmierung)
        byte[] pixelForWrite = Arrays.copyOf(rgbPixel, 3);

        ArrayUtils.swap(pixelForWrite, 0, 2);

        outputStream.writeFully(pixelForWrite);
    }

    @Override
    public void close() throws Exception {
        // ToDo: An dieser Stelle wäre es villeicht noch schön zu
        // ToDo: überprüfen, ob wirklich alle Daten für
        // ToDo: übergebene Breite und Höhe geschrieben wurde.
        // ToDo: Zunächst aber erstmal nicht, wegen YAGNI.
        readWriteFile.releaseOutputStream();
        readWriteFile.close();
    }
}

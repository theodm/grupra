package propra.imageconverter.image.tga;

import propra.imageconverter.binary.BinaryReadWriter;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Ermöglicht das pixelweise Schreiben einer TGA-Datei.
 * <p>
 * Der Benutzer ist angehalten, die Instanz nach dem Schreiben
 * wieder zu schließen.
 */
public class TgaWriter implements ImageWriter {
    private final BinaryReadWriter binaryOutput;
    private final BigInteger lengthOfContent;

    private TgaWriter(BinaryReadWriter binaryOutput, BigInteger lengthOfContent) {
        this.binaryOutput = binaryOutput;
        this.lengthOfContent = lengthOfContent;
    }

    /**
     * Erstellt einen TgaWriter mit den gegebenen Dimensionen.
     */
    public static TgaWriter create(
            BinaryReadWriter binaryOutput,
            int width,
            int height
    ) throws IOException {
        binaryOutput.writeUByte(0); // Länge der Bild-ID
        binaryOutput.writeUByte(0); // Palettentyp
        binaryOutput.writeUByte(2); // Bildtyp, nur unterstützt 2 = RGB(24 oder 32 Bit) unkomprimiert
        binaryOutput.writeN(new byte[]{0, 0, 0, 0, 0}); // Palletenbeginn, Palettenlänge und Palettengröße immer 0, da keine Palette vorhanden
        binaryOutput.writeUShort(0); // X-Koordinate für Nullpunkt, siehe parse()
        binaryOutput.writeUShort(height); // Y-Koordinate für Nullpunkt
        binaryOutput.writeUShort(width); // Breite des Bilds
        binaryOutput.writeUShort(height); // Länge des Bilds
        binaryOutput.writeUByte(24); // Bits pro Bildpunkt, nach Vorgabe immer 24
        binaryOutput.writeUByte(0b00100000); // Attribut-Byte, nach Vorgabe, siehe parse()

        BigInteger lengthOfContent = BigInteger.ONE
                .multiply(BigInteger.valueOf(width))
                .multiply(BigInteger.valueOf(height))
                .multiply(BigInteger.valueOf(3));

        return new TgaWriter(binaryOutput, lengthOfContent);
    }

    public void writeNextPixel(byte[] rgbPixel) throws IOException {
        // Wir kopieren hier den übertragenen Pixel,
        // damit wir das Byte-Array des Aufrufers nicht Ausversehen
        // überscheiben (Defensive Programmierung)
        byte[] pixelForWrite = Arrays.copyOf(rgbPixel, 3);

        ArrayUtils.swap(pixelForWrite, 0, 2);

        binaryOutput.writeN(pixelForWrite);
    }

    @Override
    public void close() throws Exception {
        // ToDo: An dieser Stelle wäre es villeicht noch schön zu
        // ToDo: überprüfen, ob wirklich alle Daten für
        // ToDo: übergebene Breite und Höhe geschrieben wurde.
        // ToDo: Zunächst aber erstmal nicht, wegen YAGNI.
        binaryOutput.close();
    }
}

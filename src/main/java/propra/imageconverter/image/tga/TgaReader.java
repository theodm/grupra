package propra.imageconverter.image.tga;

import propra.PropraException;
import propra.imageconverter.binary.BinaryReader;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.util.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Ermöglicht das pixelweise Einlesen einer TGA-Datei.
 * <p>
 * Der Benutzer ist angehalten die Instanz der Klasse wieder zu schließen.
 */
public final class TgaReader implements ImageReader {
    private final BinaryReader binaryInput;
    private final BufferedInputStream bufferedInputStream;
    private final int width;
    private final int height;
    private final BigInteger lengthOfContent;

    /**
     * Aktuelle Leseposition des Datensegments in Bytes.
     */
    private BigInteger currentPosInContent = BigInteger.ZERO;

    private TgaReader(BinaryReader binaryInput, int width, int height, BigInteger lengthOfContent) throws IOException {
        this.binaryInput = binaryInput;
        this.width = width;
        this.height = height;
        this.lengthOfContent = lengthOfContent;
        bufferedInputStream = binaryInput.bufferedInputStream();
    }

    public static TgaReader create(
            BinaryReader binaryInput
    ) throws IOException {
        // Länge der Bild-ID, per Aufgabenstellung 0
        int lengthOfPictureID = binaryInput.readUByte();
        require(lengthOfPictureID == 0, "Die Bild-ID des TGA-Formats wird vom Konverter nicht unterstützt.");

        // Farbpalettentyp
        // 0 = keine Farbpalette
        // 1 = Farbpalette vorhanden
        int paletteType = binaryInput.readUByte();
        require(paletteType == 0, "Eine Palette wird im TGA-Format nicht unterstützt.");

        // Bildtyp
        // nur unterstützt: 2 = RGB (24 oder 32 Bit) unkomprimiert
        int pictureType = binaryInput.readUByte();
        require(pictureType == 2, "Es wird im TGA-Format nur der Bildtyp 2 unterstützt. Angegeben wurde Bildtyp " + pictureType + ".");

        // Palettenbeginn, Palettenlänge und Palettengröße überspringen
        binaryInput.skip(5);

        // X-Koordinate für Nullpunkt
        // Y-Koordinate für Nullpunkt
        int xZero = binaryInput.readUShort();
        int yZero = binaryInput.readUShort();

        // Breite und Länge des Bilds
        int width = binaryInput.readUShort();
        int height = binaryInput.readUShort();

        // Wir ignorieren die Werte von xZero und yZero,
        // schreiben sie aber einheitlich.

        // Höhe und Breite dürfen nicht 0 sein.
        require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
        require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

        // Bits pro Bildpunkt, nach Vorgabe immer 24
        int bitsPerPoint = binaryInput.readUByte();
        require(bitsPerPoint == 24, "Es werden pro Bildpunkt im TGA-Format nur exakt 24 bits unterstützt. Angegeben wurde " + bitsPerPoint + ".");

        // Nach Vorgabe binaryInputt die Lage des Nullpunkts oben links
        // und die Anzahl der Attributbits pro Punkt 3 (für RBG bzw. GBR)
        int pictureAttributeByte = binaryInput.readUByte();
        require(pictureAttributeByte == 0b00100000, "Ein Bild im TGA-Format wird nur unterstützt, falls das Bildattributsbyte 0b00100000 ist. Das bedeutet die Lage des Nullpunkts ist oben links.");

        // Nach Vorgabe besteht keine Bild-ID und Farbpalette

        BigInteger lengthOfContent = BigInteger.ONE
                .multiply(BigInteger.valueOf(width))
                .multiply(BigInteger.valueOf(height))
                .multiply(BigInteger.valueOf(3));

        return new TgaReader(binaryInput, width, height, lengthOfContent);
    }

    /**
     * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
     */
    private static void require(boolean condition, String message) {
        if (!condition)
            throw new PropraException(message);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public byte[] readNextPixel() throws IOException {
        byte[] nextPixel = new byte[3];

        int readBytes = bufferedInputStream.read(nextPixel);
        require(readBytes == 3, "readNextPixel wurde über das Dateiende hinaus aufgerufen.");

        // Wir lesen im Format BGR, wollen aber
        // dem Aufrufer das Format RGB liefern.
        ArrayUtils.swap(nextPixel, 0, 2);

        currentPosInContent = currentPosInContent.add(BigInteger.valueOf(3));

        return nextPixel;
    }

    public boolean hasNextPixel() {
        return currentPosInContent.compareTo(lengthOfContent) < 0;
    }

    @Override
    public void close() throws Exception {
        binaryInput.close();
    }
}

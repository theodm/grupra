package propra.imageconverter.image.tga;

import propra.imageconverter.binary.LittleEndianInputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.compression.reader.CompressionReader;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;

import static propra.imageconverter.util.RequireUtils.require;

/**
 * Ermöglicht das pixelweise Einlesen einer TGA-Datei.
 * <p>
 * Der Benutzer ist angehalten die Instanz der Klasse wieder zu schließen.
 */
public final class TgaReader implements ImageReader {
    private final ReadWriteFile readWriteFile;
    private final int width;
    private final int height;
    private final long numberOfPixels;
    private final int pictureType;

    private CompressionReader compression;

    /**
     * Aktuelle Leseposition des Datensegments in Bytes.
     */
    private long currentPosInContent = 0;

    private TgaReader(
            ReadWriteFile readWriteFile,
            int width,
            int height,
            long numberOfPixels,
            int pictureType) throws IOException {
        this.readWriteFile = readWriteFile;
        this.width = width;
        this.height = height;
        this.numberOfPixels = numberOfPixels;
        this.pictureType = pictureType;

        reset();
    }

    public static TgaReader create(
            ReadWriteFile readWriteFile
    ) throws IOException {
        LittleEndianInputStream inputStream = new LittleEndianInputStream(readWriteFile.inputStream(0));

        // Länge der Bild-ID, per Aufgabenstellung 0
        int lengthOfPictureID = inputStream.readUByte();
        require(lengthOfPictureID == 0, "Die Bild-ID des TGA-Formats wird vom Konverter nicht unterstützt.");

        // Farbpalettentyp
        // 0 = keine Farbpalette
        // 1 = Farbpalette vorhanden
        int paletteType = inputStream.readUByte();
        require(paletteType == 0, "Eine Palette wird im TGA-Format nicht unterstützt.");

        // Bildtyp
        // nur unterstützt: 2 = RGB (24 oder 32 Bit) unkomprimiert
        //                 10 = RGB (24 oder 32 Bit) RLE-komprimiert
        int pictureType = inputStream.readUByte();
        require(pictureType == 2 || pictureType == 10, "Es wird im TGA-Format nur der Bildtyp 2 oder 10 unterstützt. Angegeben wurde Bildtyp " + pictureType + ".");

        // Palettenbeginn, Palettenlänge und Palettengröße überspringen
        inputStream.skip(5);

        // X-Koordinate für Nullpunkt
        // Y-Koordinate für Nullpunkt
        int xZero = inputStream.readUShort();
        int yZero = inputStream.readUShort();

        // Breite und Länge des Bilds
        int width = inputStream.readUShort();
        int height = inputStream.readUShort();

        // Wir ignorieren die Werte von xZero und yZero,
        // schreiben sie aber einheitlich.

        // Höhe und Breite dürfen nicht 0 sein.
        require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
        require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

        // Bits pro Bildpunkt, nach Vorgabe immer 24
        int bitsPerPoint = inputStream.readUByte();
        require(bitsPerPoint == 24, "Es werden pro Bildpunkt im TGA-Format nur exakt 24 bits unterstützt. Angegeben wurde " + bitsPerPoint + ".");

        // Nach Vorgabe ist die Lage des Nullpunkts oben links
        // und die Anzahl der Attributbits pro Punkt 3 (für RBG bzw. GBR)
        int pictureAttributeByte = inputStream.readUByte();
        require(pictureAttributeByte == 0b00100000, "Ein Bild im TGA-Format wird nur unterstützt, falls das Bildattributsbyte 0b00100000 ist. Das bedeutet die Lage des Nullpunkts ist oben links.");

        // Nach Vorgabe besteht keine Bild-ID und Farbpalette
        long numberOfPixels = ((long) width) * ((long) height) * 3L;

        readWriteFile.releaseInputStream();

        return new TgaReader(readWriteFile, width, height, numberOfPixels, pictureType);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public byte[] readNextPixel() throws IOException {
        byte[] nextPixel = compression.readNextPixel();

        // Wir lesen im Format BGR, wollen aber
        // dem Aufrufer das Format RGB liefern.
        ArrayUtils.swap(nextPixel, 0, 2);

        // Aktuelle Position (in Bildpunkten) in der Datei
        // aktualisieren
        currentPosInContent += 3;

        return nextPixel;
    }

    @Override
    public boolean hasNextPixel() {
        return currentPosInContent < numberOfPixels;
    }

    @Override
    public void reset() throws IOException {
        // An Stelle des Datensegments "starten" wir einen neuen
        // Eingabestream.
        readWriteFile.releaseInputStream();
        InputStream inputStreamData = readWriteFile.inputStream(TGAFileFormat.OFFSET_DATA);

        compression =
                TGAFileFormat.compressionReaderForPictureType(inputStreamData, pictureType);
        currentPosInContent = 0;
    }

    @Override
    public void close() throws Exception {
        readWriteFile.releaseInputStream();
        readWriteFile.close();
    }
}

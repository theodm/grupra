package propra.imageconverter.image.propra;

import propra.PropraException;
import propra.imageconverter.binary.LittleEndianInputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.compression.reader.CompressionReader;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static propra.imageconverter.image.propra.PropraFileFormat.MAGIC_HEADER;

/**
 * Der PropraReader ermöglicht das pixelweise Einlesen einer
 * Propra-Datei.
 * <p>
 * Der Benutzer ist angehalten die Instanz der Klasse nach
 * der Nutzung wieder zu schließen.
 */
public final class PropraReader implements ImageReader {
    private final ReadWriteFile readWriteFile;
    private final int width;
    private final int height;
    private final long numberOfPixels;
    private final int compressionType;

    private CompressionReader compression;

    /**
     * Die aktuelle Position des Pixel-Zeigers.
     */
    private long currentPosInContent = 0;

    private PropraReader(
            ReadWriteFile readWriteFile,
            int width,
            int height,
            long numberOfPixels,
            int compressionType) throws IOException {
        this.readWriteFile = readWriteFile;
        this.width = width;
        this.height = height;
        this.numberOfPixels = numberOfPixels;
        this.compressionType = compressionType;

        reset();
    }

    /**
     * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
     */
    private static void require(boolean condition, String message) {
        if (!condition)
            throw new PropraException(message);
    }

    /**
     * Hiermit erstellen wir eine Instanz des PropraReaders. Danach können wir
     * die Bilddaten pixelweise ablesen.
     */
    public static PropraReader create(
            ReadWriteFile readWriteFile
    ) throws IOException {
        LittleEndianInputStream inputStream = new LittleEndianInputStream(readWriteFile.inputStream(0));

        // Formatkennung
        byte[] magicHeader = new byte[MAGIC_HEADER.length];
        inputStream.readFully(magicHeader);
        require(Arrays.equals(magicHeader, MAGIC_HEADER), "Der Header der Propra-Datei ist nicht wohlgeformt. Der Beginn muss " + new String(MAGIC_HEADER) + " sein.");

        // Breite und Höhe
        int width = inputStream.readUShort();
        int height = inputStream.readUShort();

        // Höhe und Breite dürfen nicht 0 sein.
        require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
        require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

        // Bits pro Bildpunkt
        int bitsPerPoint = inputStream.readUByte();
        require(bitsPerPoint == 24, "Es werden nur 24 bits pro Pixel für Propa-Dateien unterstützt. Angegeben wurden " + bitsPerPoint + " bit.");

        // Kompressionstyp
        int compressionType = inputStream.readUByte();
        require(compressionType == 0 || compressionType == 1 || compressionType == 2, "Es wird für Propra-Dateien nur der Kompressionstyp 0, 1 und 2 unterstützt. Angeben wurde der Kompressionstyp " + compressionType + ".");

        // Länge des Bilddatensegments
        long lengthOfContent = inputStream.readULong().longValueExact();

        // Prüfsumme
        long checksum = inputStream.readUInt();

        // Hier lesen wir die kompletten Daten ein, um die Prüfsumme
        // berechnen zu können. Danach (siehe unten) setzen wir den Datei-Cursor
        // zurück, um für den Benutzer der Klasse, die Daten erneut lesbar zu machen.
        //
        // Das ist zwar für die Geschwindigkeit ineffizient, aber hier ist die
        // Verständlichkeit des Codes und die Arbeitsspeichereffizienz wichtiger als die
        // Geschwindigkeit des Codes.
        long calculatedChecksum = Checksum.calcStreamingChecksum(lengthOfContent, inputStream::readUByte);
        require(checksum == calculatedChecksum, "Die berechnete Prüfsumme der Daten stimmt nicht mit der in der Datei gespeicherten Prüfsumme überein.");

        // Erstes Byte nach dem Datensegment sollte EOF sein.
        int firstByteAfterDataSegment = inputStream.read();
        require(firstByteAfterDataSegment == -1, "Die tatsächlichen Bilddaten sind länger, als im Header angegeben.");

        // Wie oben beschrieben setzen wir den File-Cursor
        // auf das Datensegment um dem Benutzer der Klasse
        // das Lesen der Bilddaten zu ermöglichen
        readWriteFile.releaseInputStream();

        // Anzahl der Bildpunkte
        long numberOfPixels = ((long) width) * ((long) height);

        return new PropraReader(readWriteFile, width, height, numberOfPixels, compressionType);
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

        // Das Propra-Format speichert die Pixel im GBR-Format
        // wir wollen unseren Benutzern aber die Daten komfortabel
        // im RGB-Format zurückgeben.
        ArrayUtils.swap(nextPixel, 0, 2);
        ArrayUtils.swap(nextPixel, 1, 2);

        currentPosInContent += 1;

        return nextPixel;
    }

    @Override
    public boolean hasNextPixel() {
        return currentPosInContent < numberOfPixels;
    }

    @Override
    public void reset() throws IOException {
        // Wir setzen den aktuellen Cursor zum Anfang des Datensegments zurück.
        readWriteFile.releaseInputStream();
        InputStream inputStreamData = readWriteFile.inputStream(PropraFileFormat.OFFSET_DATA);

        compression = PropraFileFormat.compressionReaderForCompressionType(inputStreamData, compressionType);
        currentPosInContent = 0;
    }

    @Override
    public void close() throws Exception {
        readWriteFile.releaseInputStream();
        readWriteFile.close();
    }
}

package propra.imageconverter.image.propra;

import propra.imageconverter.binary.LittleEndianInputStream;
import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static propra.imageconverter.image.propra.PropraFileFormat.MAGIC_HEADER;

/**
 * Der PropraReader ermöglicht das pixelweise Schreibenb einer
 * Propra-Datei.
 * <p>
 * Der Benutzer ist angehalten die Instanz der Klasse nach
 * der Nutzung wieder zu schließen. Wird die Instanz nicht
 * geschlossen ist das Ergebnis undefiniert und die generierte Datei
 * möglicherweise fehlerhaft.
 */
public final class PropraWriter implements ImageWriter {
    private final ReadWriteFile readWriteFile;
    private final LittleEndianOutputStream outputStream;
    private final BigInteger lengthOfContent;

    private PropraWriter(ReadWriteFile readWriteFile, LittleEndianOutputStream outputStream, BigInteger lengthOfContent) {
        this.readWriteFile = readWriteFile;
        this.outputStream = outputStream;
        this.lengthOfContent = lengthOfContent;
    }

    /**
     * Erstellt den PropraWriter mit den vorgegebenen Dimensionen.
     */
    public static PropraWriter create(
            ReadWriteFile readWriteFile,
            int width,
            int height
    ) throws IOException {
        LittleEndianOutputStream outputStream = readWriteFile.outputStream(0);

        outputStream.writeFully(MAGIC_HEADER); // Formatkennung
        outputStream.writeUShort(width); // Bildbreite
        outputStream.writeUShort(height); // Bildhöhe
        outputStream.writeUByte(24); // Bits pro Bildpunkt (=24)
        outputStream.writeUByte(0); // Kompressionstyp (0=unkomprimiert)

        BigInteger lengthOfContent = BigInteger.ONE
                .multiply(BigInteger.valueOf(width))
                .multiply(BigInteger.valueOf(height))
                .multiply(BigInteger.valueOf(3));

        outputStream.writeULong(lengthOfContent); // Länge des Datensegments in Bytes (vorzeichenlos)

        // Wir wissen die Prüfsumme zu diesem Zeitpunkt noch nicht,
        // wir schreiben zuerst alle Daten und berechnen die Prüfsumme dann.
        // Hier wird erst mal eine 0 geschrieben
        outputStream.writeUInt(0); // Prüfsumme über die Bytes des Datensegments (vorzeichenlos)

        // Der FilePointer des darunterliegenden Ausgabestreams
        // befindet sich nun am Beginn des Datensegments
        return new PropraWriter(readWriteFile, outputStream, lengthOfContent);
    }

    @Override
    public void writeNextPixel(byte[] rgbPixel) throws IOException {
        // Wir kopieren hier den übertragenen Pixel,
        // damit wir das Byte-Array des Aufrufers nicht Ausversehen
        // überscheiben (Defensive Programmierung)
        byte[] pixelForWrite = Arrays.copyOf(rgbPixel, 3);

        // Konvertierung von RGB in GBR
        ArrayUtils.swap(pixelForWrite, 1, 2);
        ArrayUtils.swap(pixelForWrite, 0, 2);

        outputStream.writeFully(pixelForWrite);
    }

    @Override
    public void close() throws Exception {
        readWriteFile.releaseOutputStream();

        // Alle Daten wurden geschrieben, daher müssen wir nun nochmal die
        // Prüfsumme berechnen und diese auch schreiben.

        // Wir setzenden FilePointer des darunterliegenden Ausgabestreams
        // an den Anfang des Datensegments
        // Und dann berechnen wir die Prüfsumme der
        // geschriebenen Daten
        LittleEndianInputStream inputStream = readWriteFile.inputStream(PropraFileFormat.OFFSET_DATA);
        long checksum = Checksum.calcStreamingChecksum(lengthOfContent, inputStream::read);
        readWriteFile.releaseInputStream();

        // Wir setzen den Cursor des darunterliegenden Ausgabestreams
        // an die Position der Prüfsumme
        // Wir schreiben die Prüfsumme und schließen den Ausgabestream
        LittleEndianOutputStream outputStream = readWriteFile.outputStream(PropraFileFormat.OFFSET_CHECKSUM);
        outputStream.writeUInt(checksum);
        readWriteFile.releaseOutputStream();

        readWriteFile.close();
    }
}

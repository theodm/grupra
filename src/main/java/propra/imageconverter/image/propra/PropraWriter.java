package propra.imageconverter.image.propra;

import propra.imageconverter.binary.BinaryReadWriter;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.util.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    private final BinaryReadWriter binaryOutput;
    private final BufferedOutputStream bufferedOutputStream;
    private final BigInteger lengthOfContent;

    private PropraWriter(BinaryReadWriter binaryOutput, BigInteger lengthOfContent) throws IOException {
        this.binaryOutput = binaryOutput;
        this.lengthOfContent = lengthOfContent;
        this.bufferedOutputStream = binaryOutput.bufferedOutputStream();
    }

    /**
     * Erstellt den PropraWriter mit den vorgegebenen Dimensionen.
     */
    public static PropraWriter create(
            BinaryReadWriter binaryOutput,
            int width,
            int height
    ) throws IOException {
        binaryOutput.writeN(MAGIC_HEADER); // Formatkennung
        binaryOutput.writeUShort(width); // Bildbreite
        binaryOutput.writeUShort(height); // Bildhöhe
        binaryOutput.writeUByte(24); // Bits pro Bildpunkt (=24)
        binaryOutput.writeUByte(0); // Kompressionstyp (0=unkomprimiert)

        BigInteger lengthOfContent = BigInteger.ONE
                .multiply(BigInteger.valueOf(width))
                .multiply(BigInteger.valueOf(height))
                .multiply(BigInteger.valueOf(3));

        binaryOutput.writeULong(lengthOfContent); // Länge des Datensegments in Bytes (vorzeichenlos)

        // Wir wissen die Prüfsumme zu diesem Zeitpunkt noch nicht,
        // wir schreiben zuerst alle Daten und berechnen die Prüfsumme dann.
        // Hier wird erst mal eine 0 geschrieben
        binaryOutput.writeUInt(0); // Prüfsumme über die Bytes des Datensegments (vorzeichenlos)

        // Der FilePointer des darunterliegenden Ausgabestreams
        // befindet sich nun am Beginn des Datensegments
        return new PropraWriter(binaryOutput, lengthOfContent);
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

        bufferedOutputStream.write(pixelForWrite);
    }

    @Override
    public void close() throws Exception {
        binaryOutput.releaseBufferedOutputStream();

        // Alle Daten wurden geschrieben, daher müssen wir nun nochmal die
        // Prüfsumme berechnen und diese auch schreiben.

        // Wir setzenden FilePointer des darunterliegenden Ausgabestreams
        // an den Anfang des Datensegments
        binaryOutput.seek(PropraFileFormat.OFFSET_DATA);

        // Und dann berechnen wir die Prüfsumme der
        // geschriebenen Daten
        BufferedInputStream bufferedInputStream = binaryOutput.bufferedInputStream();
        long checksum = Checksum.calcStreamingChecksum(lengthOfContent, bufferedInputStream::read);
        binaryOutput.releaseInputStream();

        // Wir setzenden Cursor des darunterliegenden Ausgabestreams
        // an die Position der Prüfsumme
        binaryOutput.seek(PropraFileFormat.OFFSET_CHECKSUM);

        // Wir schreiben die Prüfsumme und schließen den Ausgabestream
        binaryOutput.writeUInt(checksum);
        binaryOutput.close();
    }
}

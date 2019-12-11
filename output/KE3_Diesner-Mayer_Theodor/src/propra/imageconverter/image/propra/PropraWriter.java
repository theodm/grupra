package propra.imageconverter.image.propra;

import propra.imageconverter.binary.LittleEndianInputStream;
import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.image.compression.writer.CompressionWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import static propra.imageconverter.image.propra.PropraFileFormat.MAGIC_HEADER;
import static propra.imageconverter.util.RequireUtils.require;

/**
 * Der PropraReader ermöglicht das Schreiben einer Propra-Datei
 */
public final class PropraWriter implements ImageWriter {
    private final CompressionType compressionType;

    private PropraWriter(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    /**
     * Erstellt einen PropraWriter mit dem angegebenen Kompressionstyp.
     */
    public static PropraWriter create(
            CompressionType compressionType
    ) {
        return new PropraWriter(compressionType);
    }

    @Override public void write(
            ImageReader imageReader,
            ReadWriteFile outputFile
    ) throws IOException {
        require(compressionType == CompressionType.NO_COMPRESSION
                || compressionType == CompressionType.RLE
                || compressionType == CompressionType.HUFFMAN, "Die gewählte Kompressionsart wird für das ProPra-Format nicht unterstützt.");

        CompressionWriter compression
                = compressionType.getCompressionWriter();
        {
            LittleEndianOutputStream outputStream = new LittleEndianOutputStream(outputFile.outputStream(0));

            outputStream.writeFully(MAGIC_HEADER); // Formatkennung
            outputStream.writeUShort(imageReader.getWidth()); // Bildbreite
            outputStream.writeUShort(imageReader.getHeight()); // Bildhöhe
            outputStream.writeUByte(24); // Bits pro Bildpunkt (=24)
            outputStream.writeUByte(PropraFileFormat.compressionTypeToPropraCompressionType(compressionType)); // Kompressionstyp (0=unkomprimiert)

            // Wir kennen die Datenlänge noch nicht,
            // da die Kompression aktiv sein könnte,
            // daher setzen wir sie zuerst auf 0
            outputStream.writeULong(BigInteger.ZERO); // Länge des Datensegments in Bytes (vorzeichenlos)

            // Wir wissen die Prüfsumme zu diesem Zeitpunkt noch nicht,
            // wir schreiben zuerst alle Daten und berechnen die Prüfsumme dann.
            // Hier wird erst mal eine 0 geschrieben
            outputStream.writeUInt(0); // Prüfsumme über die Bytes des Datensegments (vorzeichenlos)

            outputFile.releaseOutputStream();
        }
        long lengthOfContent;
        {
            BufferedOutputStream outputStreamData
                    = outputFile.outputStream(PropraFileFormat.OFFSET_DATA);

            PixelIterator pixelIterator =
                    PropraPixelIterator.forImageReader(imageReader);

            lengthOfContent = compression.write(pixelIterator, outputStreamData);

            outputFile.releaseOutputStream();
        }

        {
            // Alle Daten wurden geschrieben, daher müssen wir nun nochmal die
            // Prüfsumme berechnen.
            // Wir setzenden FilePointer des darunterliegenden Ausgabestreams
            // an den Anfang des Datensegments
            // Und dann berechnen wir die Prüfsumme der
            // geschriebenen Daten
            LittleEndianInputStream inputStream = new LittleEndianInputStream(outputFile.inputStream(PropraFileFormat.OFFSET_DATA));
            long checksum = Checksum.calcStreamingChecksum(lengthOfContent, inputStream::readUByte);
            outputFile.releaseInputStream();

            // Wir setzen den Cursor des darunterliegenden Ausgabestreams
            // an die Position der Datensegmentlänge.
            // Wir schreiben die Datensegmentlänge und die Prüfsumme und schließen den Ausgabestream
            LittleEndianOutputStream outputStream = new LittleEndianOutputStream(outputFile.outputStream(PropraFileFormat.OFFSET_DATA_SEGMENT_LENGTH));
            outputStream.writeULong(BigInteger.valueOf(lengthOfContent));
            outputStream.writeUInt(checksum);
            outputFile.releaseOutputStream();

        }
    }
}

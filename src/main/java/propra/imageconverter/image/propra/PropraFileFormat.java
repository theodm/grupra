package propra.imageconverter.image.propra;

import propra.PropraException;
import propra.imageconverter.image.compression.CompressionReader;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.huffman.HuffmanCompressionReader;
import propra.imageconverter.image.compression.rle.RLECompressionReader;
import propra.imageconverter.image.compression.uncompressed.NoCompressionReader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Die Klasse enthält Informationen, die sowohl
 * vom PropraReader, als auch vom PropraWriter verwendet und benötigt werden.
 * <p>
 * Das Propra-Format ist unter https://moodle-wrm.fernuni-hagen.de/mod/page/view.php?id=40779
 * dokumentiert.
 */
final class PropraFileFormat {
    /**
     * Der statische Beginn einer Propa-Datei.
     */
    final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();

    /**
     * Der Offset vom Beginn einer Propra-Datei
     * bis zum Beginn der Länge des Datensegments.
     */
    final static long OFFSET_DATA_SEGMENT_LENGTH = MAGIC_HEADER.length + 2 + 2 + 1 + 1;

    /**
     * Der Offset vom Beginn einer Propra-Datei
     * bis zum Beginn der Prüfsumme
     */
    final static long OFFSET_CHECKSUM = MAGIC_HEADER.length + 2 + 2 + 1 + 1 + 8;

    /**
     * Der Offset vom Beginn einer Propra-Datei
     * bis zu dem Beginn des Datensegments
     */
    final static long OFFSET_DATA = MAGIC_HEADER.length + 2 + 2 + 1 + 1 + 8 + 4;

    final static List<CompressionType> supportedCompressionTypes = Arrays.asList(
            CompressionType.HUFFMAN,
            CompressionType.RLE,
            CompressionType.NO_COMPRESSION,
            CompressionType.AUTO
    );

    private PropraFileFormat() {

    }

    /**
     * Gibt für das Attribut {@param compressionType} einer Propra-Datei den
     * entsprechenden Reader für diesen Kompressionstyp zurück.
     */
    static CompressionReader compressionReaderForCompressionType(InputStream inputStream, int compressionType) {
        switch (compressionType) {
            case 0:
                return new NoCompressionReader(inputStream);
            case 1:
                return new RLECompressionReader(inputStream);
            case 2:
                return new HuffmanCompressionReader(inputStream);
        }

        throw new PropraException("Der ausgewählte Compression-Type (Propra) " + compressionType + " wird nicht unterstützt.");
    }

    /**
     * Gibt für einem Aufzählungswert des Enums CompressionType, die
     * interne Repräsentation (=Kompressionstyp) im Propra-Format zurück.
     */
    static int compressionTypeToPropraCompressionType(CompressionType compressionType) {
        switch (compressionType) {
            case NO_COMPRESSION:
                return 0;
            case RLE:
                return 1;
            case HUFFMAN:
                return 2;
        }

        // Kann nicht vorkommen!
        throw new PropraException("Der Kompressionstyp " + compressionType + " (Propra) wird nicht unterstützt.");
    }
}

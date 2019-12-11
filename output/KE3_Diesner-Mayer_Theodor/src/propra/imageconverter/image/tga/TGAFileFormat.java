package propra.imageconverter.image.tga;

import propra.PropraException;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.reader.CompressionReader;
import propra.imageconverter.image.compression.reader.NoCompressionReader;
import propra.imageconverter.image.compression.reader.RLECompressionReader;

import java.io.InputStream;

final class TGAFileFormat {
    /**
     * Der Offset vom Beginn einer TGA-Datei
     * bis zu dem Beginn des Datensegments
     */
    final static long OFFSET_DATA = 18;

    private TGAFileFormat() {

    }

    /**
     * Gibt für das Attribut {@param pictureType} einer TGA-Datei den
     * entsprechenden Reader für diesen Kompressionstyp zurück.
     */
    static CompressionReader compressionReaderForPictureType(InputStream inputStream, int pictureType) {
        switch (pictureType) {
            case 2:
                return new NoCompressionReader(inputStream);
            case 10:
                return new RLECompressionReader(inputStream);
        }

        throw new PropraException("Der ausgewählte Picture-Type (TGA) " + pictureType + " wird nicht unterstützt.");
    }

    /**
     * Gibt für den aktuellen Aufzählungswert, die
     * interne Repräsentation (=Kompressionstyp) im TGA-Format zurück.
     */
    static int compressionTypeToTGACompressionType(CompressionType compressionType) {
        switch (compressionType) {
            case NO_COMPRESSION:
                return 2;
            case RLE:
                return 10;
        }

        // Kann nicht vorkommen!
        throw new PropraException("Der Kompressionstyp " + compressionType + " (TGA) wird nicht unterstützt.");
    }
}

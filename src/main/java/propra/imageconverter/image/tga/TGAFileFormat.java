package propra.imageconverter.image.tga;

import propra.PropraException;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.reader.CompressionReader;
import propra.imageconverter.image.compression.reader.NoCompressionReader;
import propra.imageconverter.image.compression.reader.RLECompressionReader;

final class TGAFileFormat {
    private TGAFileFormat() {

    }

    /**
     * Gibt für das Attribut {@param pictureType} einer TGA-Datei den
     * entsprechenden Reader für diesen Kompressionstyp zurück.
     */
    static CompressionReader compressionReaderForPictureType(int pictureType) {
        switch (pictureType) {
            case 2:
                return new NoCompressionReader();
            case 10:
                return new RLECompressionReader();
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

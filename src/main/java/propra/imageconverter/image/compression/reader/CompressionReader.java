package propra.imageconverter.image.compression.reader;

import propra.PropraException;
import propra.imageconverter.binary.LittleEndianInputStream;

import java.io.IOException;

/**
 * Ermöglicht das bildpunktweise Einlesen
 * einer Datenquelle unter Beachtung einer Kompressionsmethode.
 */
public interface CompressionReader {

    /**
     * Gibt für das Attribut {@param pictureType} einer TGA-Datei den
     * entsprechenden Reader für diesen Kompressionstyp zurück.
     */
    static CompressionReader fromTGAPictureType(int pictureType) {
        switch (pictureType) {
            case 2:
                return new NoCompressionReader();
            case 10:
                return new RLECompressionReader();
        }

        throw new PropraException("Der ausgewählte Picture-Type (TGA) " + pictureType + " wird nicht unterstützt.");
    }

    /**
     * Gibt für das Attribut {@param pictureType} einer Propra-Datei den
     * entsprechenden Reader für diesen Kompressionstyp zurück.
     */
    static CompressionReader fromPropraCompressionType(int pictureType) {
        switch (pictureType) {
            case 0:
                return new NoCompressionReader();
            case 1:
                return new RLECompressionReader();
        }

        throw new PropraException("Der ausgewählte Compression-Type (Propra) " + pictureType + " wird nicht unterstützt.");
    }

    /**
     * Lese das nächste Pixel unter Beachtung des Kompressions-
     * algorithmus aus dem übergebenen {@param inputStream} aus.
     */
    byte[] readNextPixel(
            LittleEndianInputStream inputStream
    ) throws IOException;
}

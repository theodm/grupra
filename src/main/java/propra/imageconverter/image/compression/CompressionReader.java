package propra.imageconverter.image.compression;

import java.io.IOException;

/**
 * Ermöglicht das bildpunktweise Einlesen
 * einer Datenquelle unter Beachtung einer Kompressionsmethode.
 */
public interface CompressionReader {
    /**
     * Lese das nächste Pixel unter Beachtung des Kompressions-
     * algorithmus aus dem übergebenen {@param inputStream} aus.
     */
    byte[] readNextPixel() throws IOException;
}

package propra.imageconverter.image.compression.writer;

import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Ermöglicht das Schreiben von Bilddaten mit
 * verschiedenen Kompressionstypen
 */
public interface CompressionWriter {
    /**
     * Schreibt die Bilddaten, die aus dem Iterator {@param pixelData} kommen
     * mit der entsprechenden Komprimierung in den übergebenen {@param outputStream}.
     * Gibt die Anzahl an Bytes zurück, die geschrieben wurden.
     */
    long write(
            PixelIterator pixelData,
            BufferedOutputStream outputStream
    ) throws IOException;
}

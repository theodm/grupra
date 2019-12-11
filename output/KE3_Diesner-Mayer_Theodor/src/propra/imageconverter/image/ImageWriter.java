package propra.imageconverter.image;

import propra.imageconverter.binary.ReadWriteFile;

import java.io.IOException;

/**
 * Ermöglicht das Schreiben einer
 * Bilddatei in verschiedene Formate.
 */
public interface ImageWriter {
    /**
     * Schreibt die Bilddaten aus dem übergebenen {@param imageReader} in
     * die übergebene Datei {@param outputFile}. Die übergebene Datei muss durch
     * den Aufrufer wieder geschlossen werden.
     */
    void write(
            ImageReader imageReader,
            ReadWriteFile outputFile
    ) throws IOException;
}

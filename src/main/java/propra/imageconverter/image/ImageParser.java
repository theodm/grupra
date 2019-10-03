package propra.imageconverter.image;

import java.io.IOException;

/**
 * Interface, das für ein Bildformat einen Parser und Writer anbietet.
 */
public interface ImageParser {
    /**
     * Liest aus dem übergebenen BinaryReader ein Bild in das interne Datenformat ein.
     * Der Aufrufer muss den Stream selbst schließen.
     */
    Picture parse(BinaryReader is) throws IOException;

    /**
     * Schreibt in den übergebenen Ausgabestream ein Bild aus dem internen Datenformat.
     * Der Aufrufer muss den Stream selbst schließen.
     */
    void write(Picture picture, BinaryWriter os) throws IOException;
}

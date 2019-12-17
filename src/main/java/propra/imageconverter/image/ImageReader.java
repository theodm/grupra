package propra.imageconverter.image;

import java.io.IOException;

/**
 * Ermöglicht das pixelweise Einlesen einer Bilddatei.
 * <p>
 * Die Instanz muss durch den Benutzer geschlossen werden, damit der
 * darunterliegende übergebene Eingabestream geschlossen wird.
 */
public interface ImageReader extends AutoCloseable {
    /**
     * Breite des eingelesenen Bilds.
     */
    int getWidth();

    /**
     * Höhe des eingelesenen Bilds.
     */
    int getHeight();

    /**
     * Liest den nächsten Pixel im RGB-Format und erhöht den
     * internen Cursor zum nächsten Pixel.
     */
    byte[] readNextPixel() throws IOException;

    /**
     * Gibt an, ob es einen nächsten Pixel gibt
     * oder das Ende der Bilddaten erreicht wurde.
     */
    boolean hasNextPixel();

    /**
     * Setzt den Bilddatenstrom auf den Anfang der
     * Bilddaten zurück.
     */
    void reset() throws IOException;
}

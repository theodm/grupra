package propra.imageconverter.image;

import java.io.IOException;

/**
 * Ermöglicht das pixelweise Schreiben
 * einer Bilddatei.
 * <p>
 * Die Instanz muss durch den Benutzer geschlossen werden,
 * um den darunterliegenden Ausgabestream zu schließen.
 */
public interface ImageWriter extends AutoCloseable {
    /**
     * Schreibt den nächsten Pixel im RGB-Format. Der Benutzer
     * ist dafür verantwortlich, dass nicht mehr Pixel geschrieben
     * werden, als die zuvor übergebene Breite und Höhe zulassen.
     */
    void writeNextPixel(byte[] rgbPixel) throws IOException;
}

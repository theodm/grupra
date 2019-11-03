package propra.imageconverter.image.compression.iterator;

import java.io.IOException;

/**
 * Das Interface PixelIterator ermöglicht das bildpunktweise Auslesen eines
 * Bildes. Es ist sehr ähnlich zum ImageReader, wird aber konzeptuell auf der Ebene zwischen
 * dem Schreiben des Bildes und dem Kompressionsalgorithmus verwendet. Insofern gibt das Interface
 * die Daten nicht im RGB-Format sondern im Format des Dateityps zurück; außerdem erlaubt es das
 * Vorausschauen auf den nächsten Bildpunkt (Peek).
 */
public interface PixelIterator {
	/**
	 * Gibt den nächsten Bildpunkt zurück und bewegt den internen
	 * Cursor nach vorne.
	 * <p>
	 * Gibt null zurück, falls das Ende des Bildes erreicht wurde.
	 */
	byte[] readNextPixel() throws IOException;

	/**
	 * Gibt den nächsten Bildpunkt zurück, ohne den internen Cursor
	 * vorzubewegen.
	 * <p>
	 * Gibt null zurück, falls das Ende des Bildes erreicht wurde.
	 */
	byte[] peekPixel();

	/**
	 * Gibt an ob es im zugrundeliegenden Pixelstrom,
	 * noch einen weiteren Pixel gibt.
	 */
	boolean hasNextPixel();

	/**
	 * Gibt die Breite des zugrundeliegenden
	 * Bildes in Pixeln zurück.
	 */
	int getWidth();

	/**
	 * Gibt die Höhe des zugrundeliegenden
	 * Bildes in Pixeln zurück.
	 */
	int getHeight();
}

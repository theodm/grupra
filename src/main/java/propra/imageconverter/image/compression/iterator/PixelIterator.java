package propra.imageconverter.image.compression.iterator;

import java.io.IOException;

public interface PixelIterator {
	byte[] readNextPixel() throws IOException;

	byte[] peekPixel() throws IOException;

	boolean hasNextPixel();

	// ToDo: Höhe breite?
}

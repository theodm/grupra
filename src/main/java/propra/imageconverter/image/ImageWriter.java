package propra.imageconverter.image;

import propra.imageconverter.binary.ReadWriteFile;

import java.io.IOException;

/**
 * Ermöglicht das pixelweise Schreiben
 * einer Bilddatei.
 * <p> TODO
 */
public interface ImageWriter {
	void write(
			ImageReader imageReader,
			ReadWriteFile outputFile
	) throws IOException;
}

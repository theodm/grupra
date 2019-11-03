package propra.imageconverter.image.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.IOException;

/**
 * Implementiert einen CompressionWriter ohne
 * Komprimierung.
 */
public class NoCompressionWriter implements CompressionWriter {
	@Override
	public long write(
			PixelIterator pixelData,
			LittleEndianOutputStream outputStream
	) throws IOException {
		long numberOfDataWritten = 0;

		while (pixelData.hasNextPixel()) {
			outputStream.writeFully(pixelData.readNextPixel());
			numberOfDataWritten += 3;
		}

		return numberOfDataWritten;
	}
}

package propra.imageconverter.image.compression.uncompressed;

import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.CompressionWriter;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Implementiert einen CompressionWriter ohne
 * Komprimierung.
 */
public class NoCompressionWriter implements CompressionWriter {
	@Override
	public long write(
			PixelIterator pixelData,
			BufferedOutputStream outputStream
	) throws IOException {
		long numberOfDataWritten = 0;

		while (pixelData.hasNextPixel()) {
			outputStream.write(pixelData.readNextPixel());
			numberOfDataWritten += 3;
		}

		return numberOfDataWritten;
	}

	@Override
	public CompressionType getCompressionType() {
		return CompressionType.NO_COMPRESSION;
	}
}

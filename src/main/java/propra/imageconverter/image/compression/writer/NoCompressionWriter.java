package propra.imageconverter.image.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.IOException;

public class NoCompressionWriter implements CompressionWriter {
	@Override public void write(
			PixelIterator pixelData,
			LittleEndianOutputStream outputStream
	) throws IOException {
		while (pixelData.hasNextPixel()) {
			outputStream.writeFully(pixelData.readNextPixel());
		}
	}
}

package propra.imageconverter.image.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.IOException;

public interface CompressionWriter {
	public void write(
			PixelIterator pixelData,
			LittleEndianOutputStream outputStream
	) throws IOException;
}

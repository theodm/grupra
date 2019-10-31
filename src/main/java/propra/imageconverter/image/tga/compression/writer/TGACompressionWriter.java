package propra.imageconverter.image.tga.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;

import java.io.IOException;

public interface TGACompressionWriter {
	void writeNextPixel(
			LittleEndianOutputStream outputStream,
			byte[] rgbPixel
	) throws IOException;

	void flush(LittleEndianOutputStream outputStream) throws IOException;
}

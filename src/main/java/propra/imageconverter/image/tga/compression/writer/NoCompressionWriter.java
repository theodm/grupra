package propra.imageconverter.image.tga.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;

import java.io.IOException;

public class NoCompressionWriter implements TGACompressionWriter {

	@Override
	public void writeNextPixel(
			LittleEndianOutputStream outputStream,
			byte[] rgbPixel
	) throws IOException {
		// Nur ausschreiben :)
		outputStream.writeFully(rgbPixel);
	}

	@Override
	public void flush(LittleEndianOutputStream outputStream) {

	}
}

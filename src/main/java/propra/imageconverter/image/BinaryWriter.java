package propra.imageconverter.image;

import java.io.IOException;
import java.io.OutputStream;

public class BinaryWriter implements AutoCloseable {
	private final OutputStream outputStream;

	public BinaryWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeByte(int value) throws IOException {
		// Ggf. warnen bei zu hohem Wert
		outputStream.write(value);
	}

	public void writeWord(int value) throws IOException {
		// Ggf. warnen

		outputStream.write(value % 256);
		outputStream.write((value >> 8) % 256);
	}

	public void writeN(byte[] sourceArray) throws IOException {
		outputStream.write(sourceArray);
	}

	@Override public void close() throws IOException {
		outputStream.close();
	}
}

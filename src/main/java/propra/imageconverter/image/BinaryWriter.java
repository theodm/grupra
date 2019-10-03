package propra.imageconverter.image;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class BinaryWriter implements AutoCloseable {
	private final OutputStream outputStream;

	public BinaryWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeByte(int value) throws IOException {
		// Ggf. warnen bei zu hohem Wert
		outputStream.write(value);
	}

	// Kommentar über QWord
	public void writeQWord(BigInteger value) throws IOException {
		outputStream.write(value.mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(8).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(16).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(24).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(32).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(40).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(48).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(56).mod(BigInteger.valueOf(256)).intValue());
	}

	public void writeDWord(long value) throws IOException {
		outputStream.write((int) (value % 256));
		outputStream.write((int) (value >> 8) % 256);
		outputStream.write((int) (value >> 16) % 256);
		outputStream.write((int) (value >> 24) % 256);
	}

	public void writeWord(int value) throws IOException {
		// Ggf. warnen

		outputStream.write(value % 256);
		outputStream.write((value >>> 8) % 256);
	}

	public void writeN(byte[] sourceArray) throws IOException {
		outputStream.write(sourceArray);
	}

	@Override public void close() throws IOException {
		outputStream.close();
	}
}

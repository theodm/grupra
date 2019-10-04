package propra.imageconverter.image;

import propra.PropraException;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Die Klasse wrappt einen OutputStream und bietet
 * einfache Methoden zum Schreiben von Daten im Little-Endian-Format
 * an.
 */
public final class BinaryWriter implements AutoCloseable {
	private final OutputStream outputStream;

	public BinaryWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * Wirf eine OutOfBounds-Exception zum Anzeigen, dass ein üngültiger Wert übergebeben wurde.
	 */
	private void throwOutOfBounds() {
		throw new PropraException("Die übergebene Zahl ist nicht im gültigen Wertebereich, kann daher nicht geschrieben werden.");
	}

	/**
	 * Schreibt ein vorzeichenloses Byte in den Datenstream.
	 */
	public void writeByte(int value) throws IOException {
		if (value < 0 || value >= (2 << 7))
			throwOutOfBounds();

		outputStream.write(value);
	}

	/**
	 * Schreibt ein vorzeichenloses QWord (vorzeichenlose 8 Byte Zahl) in den Datenstream.
	 */
	public void writeQWord(BigInteger value) throws IOException {
		if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(BigInteger.valueOf(2).pow(64)) >= 0)
			throwOutOfBounds();

		outputStream.write(value.mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(8).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(16).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(24).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(32).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(40).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(48).mod(BigInteger.valueOf(256)).intValue());
		outputStream.write(value.shiftRight(56).mod(BigInteger.valueOf(256)).intValue());
	}

	/**
	 * Schreibt ein vorzeichenloses DWord (vorzeichenlose 4 Byte Zahl) in den Datenstream.
	 */
	public void writeDWord(long value) throws IOException {
		if (value < 0L || value > (2L << 31L))
			throwOutOfBounds();

		outputStream.write((int) (value % 256));
		outputStream.write((int) (value >> 8) % 256);
		outputStream.write((int) (value >> 16) % 256);
		outputStream.write((int) (value >> 24) % 256);
	}

	/**
	 * Schreibt ein vorzeichenloses Word (vorzeichenlose 2 Byte Zahl) in den Datenstream.
	 */
	public void writeWord(int value) throws IOException {
		if (value < 0 || value >= (2 << 15))
			throwOutOfBounds();

		outputStream.write(value % 256);
		outputStream.write((value >>> 8) % 256);
	}

	/**
	 * Schreibt ein Byte-Array in den Datenstream.
	 */
	public void writeN(byte[] sourceArray) throws IOException {
		outputStream.write(sourceArray);
	}

	public void mark() {
		return outputStream.
	}

	@Override public void close() throws IOException {
		outputStream.close();
	}
}

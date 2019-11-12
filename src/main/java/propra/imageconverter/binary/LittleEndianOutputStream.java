package propra.imageconverter.binary;

import propra.PropraException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Die Klasse wrappt einen (gebufferten) Ausgabestream und bietet
 * einfache Methoden zum Schreiben und Lesen von Daten im Little-Endian-Format
 * an.
 */
public final class LittleEndianOutputStream implements AutoCloseable {
	private final BufferedOutputStream outputStream;

	public LittleEndianOutputStream(BufferedOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * Wirf eine OutOfBounds-Exception zum Anzeigen, dass ein ungültiger Wert übergebeben wurde.
	 */
	private void throwOutOfBounds() {
		throw new PropraException("Die übergebene Zahl ist nicht im gültigen Wertebereich, kann daher nicht geschrieben werden.");
	}

	/**
	 * Schreibt ein vorzeichenloses Byte in den Datenstream.
	 */
	public void writeUByte(int value) throws IOException {
        if (value < 0 || value > 0b1111_1111)
			throwOutOfBounds();

		outputStream.write(value);
	}

	/**
	 * Schreibt ein vorzeichenloses Short (2 Byte) in den Datenstream.
	 */
	public void writeUShort(int value) throws IOException {
        if (value < 0 || value > 0b1111_1111_1111_1111)
			throwOutOfBounds();

		outputStream.write(value % 256);
		outputStream.write((value >>> 8) % 256);
	}

	/**
	 * Schreibt ein vorzeichenloses Int (4 Byte) in den Datenstream.
	 */
	public void writeUInt(long value) throws IOException {
        if (value < 0L || value > 0b1111_1111_1111_1111_1111_1111_1111_1111L)
			throwOutOfBounds();

		outputStream.write((int) (value % 256));
		outputStream.write((int) (value >> 8) % 256);
		outputStream.write((int) (value >> 16) % 256);
		outputStream.write((int) (value >> 24) % 256);
	}

	/**
	 * Schreibt ein vorzeichenloses Long (8 Byte) in den Datenstream.
	 */
	public void writeULong(BigInteger value) throws IOException {
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
	 * Schreibt ein Byte-Array in den Datenstream.
	 */
	public void writeFully(byte[] sourceArray) throws IOException {
		outputStream.write(sourceArray);
	}

	/**
	 * Schließt den darunterliegenden Ausgabestream.
	 */
	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	/**
	 * Schreibt alle noch nicht abgespeicherten Daten
	 * des zugrundeliegenden gebufferten Ausgabestreams.
	 */
	void flush() throws IOException {
		outputStream.flush();
	}
}

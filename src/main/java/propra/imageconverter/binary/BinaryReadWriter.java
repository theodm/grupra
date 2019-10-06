package propra.imageconverter.binary;

import propra.PropraException;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Die Klasse wrappt einen Eingabe- und Ausgabestream und bietet
 * einfache Methoden zum Schreiben und Lesen von Daten im Little-Endian-Format
 * an.
 */
public final class BinaryReadWriter extends BinaryReader implements AutoCloseable {
    private final BufferedRandomAccessFile dataInputOutput;

    public BinaryReadWriter(BufferedRandomAccessFile dataInputOutput) {
        super(dataInputOutput);
        this.dataInputOutput = dataInputOutput;
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
        if (value < 0 || value >= (2 << 7))
            throwOutOfBounds();

        dataInputOutput.write(value);
    }

    /**
     * Schreibt ein vorzeichenloses Short (2 Byte) in den Datenstream.
     */
    public void writeUShort(int value) throws IOException {
        if (value < 0 || value >= (2 << 15))
            throwOutOfBounds();

        dataInputOutput.write(value % 256);
        dataInputOutput.write((value >>> 8) % 256);
    }

    /**
     * Schreibt ein vorzeichenloses Int (4 Byte) in den Datenstream.
     */
    public void writeUInt(long value) throws IOException {
        if (value < 0L || value > (2L << 31L))
            throwOutOfBounds();

        dataInputOutput.write((int) (value % 256));
        dataInputOutput.write((int) (value >> 8) % 256);
        dataInputOutput.write((int) (value >> 16) % 256);
        dataInputOutput.write((int) (value >> 24) % 256);
    }

    /**
     * Schreibt ein vorzeichenloses Long (8 Byte) in den Datenstream.
     */
    public void writeULong(BigInteger value) throws IOException {
        if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(BigInteger.valueOf(2).pow(64)) >= 0)
            throwOutOfBounds();

        dataInputOutput.write(value.mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(8).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(16).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(24).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(32).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(40).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(48).mod(BigInteger.valueOf(256)).intValue());
        dataInputOutput.write(value.shiftRight(56).mod(BigInteger.valueOf(256)).intValue());
    }

    /**
     * Schreibt ein Byte-Array in den Datenstream.
     */
    public void writeN(byte[] sourceArray) throws IOException {
        dataInputOutput.write(sourceArray);
    }

    @Override
    public void close() throws IOException {
        dataInputOutput.close();
    }
}

package propra.imageconverter.image;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class BinaryReader implements AutoCloseable {
    private final InputStream inputStream;

    public BinaryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    // TODO dokumentieren warum int und  nicht byte und short
    public int readByte() throws IOException {
        return inputStream.read();
    }

    public BigInteger readQWord() throws IOException {
        BigInteger byte1 = BigInteger.valueOf(inputStream.read()).shiftLeft(0);
        BigInteger byte2 = BigInteger.valueOf(inputStream.read()).shiftLeft(8);
        BigInteger byte3 = BigInteger.valueOf(inputStream.read()).shiftLeft(16);
        BigInteger byte4 = BigInteger.valueOf(inputStream.read()).shiftLeft(24);
        BigInteger byte5 = BigInteger.valueOf(inputStream.read()).shiftLeft(32);
        BigInteger byte6 = BigInteger.valueOf(inputStream.read()).shiftLeft(40);
        BigInteger byte7 = BigInteger.valueOf(inputStream.read()).shiftLeft(48);
        BigInteger byte8 = BigInteger.valueOf(inputStream.read()).shiftLeft(56);

        return BigInteger.ZERO
                .add(byte1)
                .add(byte2)
                .add(byte3)
                .add(byte4)
                .add(byte5)
                .add(byte6)
                .add(byte7)
                .add(byte8);
    }

    public int readWord() throws IOException {
        int byte1 = inputStream.read();
        int byte2 = inputStream.read() << 8;

        return byte1 + byte2;
    }

    public void skip(long n) throws IOException {
        long numberOfSkippedBytes = inputStream.skip(n);

        if (!(numberOfSkippedBytes == n)) {
            throw new RuntimeException("Das Dateiende wurde vorzeitig erreicht. Erwartet waren " + n + " Bytes. Gelesen wurden jedoch nur " + numberOfSkippedBytes + ".");
        }
        ;
    }

    public void readN(byte[] targetArray, int n) throws IOException {
        long numberOfReadBytes = inputStream.read(targetArray, 0, n);

        if (!(numberOfReadBytes == n)) {
            throw new RuntimeException("Das Dateiende wurde vorzeitig erreicht. Erwartet waren " + n + " Bytes. Gelesen wurden jedoch nur " + numberOfReadBytes + ".");
        }
        ;
    }


    @Override public void close() throws IOException {
        inputStream.close();
    }

    public long readDword() throws IOException {
        long byte1 = inputStream.read();
        long byte2 = ((long) inputStream.read()) << 8L;
        long byte3 = ((long) inputStream.read()) << 16L;
        long byte4 = ((long) inputStream.read()) << 24L;

        return byte1 + byte2 + byte3 + byte4;
    }
}
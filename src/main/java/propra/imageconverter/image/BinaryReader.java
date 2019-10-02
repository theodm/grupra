package propra.imageconverter.image;

import java.io.IOException;
import java.io.InputStream;

public class BinaryReader implements AutoCloseable {
    private final InputStream inputStream;

    public BinaryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    // TODO dokumentieren warum int und  nicht byte und short
    public int readByte() throws IOException {
        return inputStream.read();
    }

    public int readWord() throws IOException {
        int firstByte = inputStream.read();
        int secondByte = inputStream.read();

        return firstByte + secondByte * 256;
    }

    public void skip(long n) throws IOException {
        long numberOfSkippedBytes = inputStream.skip(n);

        assert numberOfSkippedBytes == n;
    }

    public void readN(byte[] targetArray, int n) throws IOException {
        long numberOfReadBytes = inputStream.read(targetArray, 0, n);

        assert numberOfReadBytes == n;
    }

    @Override public void close() throws IOException {
        inputStream.close();
    }
}
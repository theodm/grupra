package propra.imageconverter.image;

import propra.PropraException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Hilfsklasse um Daten eines InputStream zu lesen. Bietet
 * geeignete Methoden um Binärdaten in Little-Endian zu lesen.
 */
public final class BinaryReader implements AutoCloseable {
    private final InputStream inputStream;

    public BinaryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Helferlein, das ein Exception wirft, falls EOF im gelesenen Byte zurückgegeben wurde.
     */
    private int throwIfEOF(int value) {
        if (value == -1) {
            throw new PropraException("Die Datei wurde unerwarteter Weise über das Ende hinaus gelesen. Villeicht ist die eingelesene Datei nicht in Ordnung.");
        }

        return value;
    }

    /**
     * Liest ein Byte als vorzeichenlose Zahl (1 Byte) aus dem darunterliegenden Inputstream aus. Der Wert wird im
     * Wertebereich int zurückgegeben, damit das vorzeichenlose Byte auch richtig interpretiert werden kann.
     * (Java kennt nur vorzeichenbehaftete Typen; in int passt aber auch ein vorzeichenloses Byte herein)
     *
     * @return -1, falls der Stream am Ende ist, sonst das gelesene Byte
     */
    public int readByte() throws IOException {
        return inputStream.read();
    }

    /**
     * Liest ein QWord (8 Byte vorzeichenlos) aus dem darunterliegenden InputStream im Little-Endian-Format aus.
     * Es wird ein Exception ausgegeben, falls beim Einlesen auf das Ende des Streams zugegriffen wird.
     */
    public BigInteger readQWord() throws IOException {
        BigInteger byte1 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(0);
        BigInteger byte2 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(8);
        BigInteger byte3 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(16);
        BigInteger byte4 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(24);
        BigInteger byte5 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(32);
        BigInteger byte6 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(40);
        BigInteger byte7 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(48);
        BigInteger byte8 = BigInteger.valueOf(throwIfEOF(inputStream.read())).shiftLeft(56);

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

    /**
     * Liest ein Word (2 Byte vorzeichenlos) aus dem darunterliegenden InputStream im Little-Endian-Format aus.
     * Es wird ein Exception ausgegeben, falls beim Einlesen auf das Ende des Streams zugegriffen wird.
     */
    public int readWord() throws IOException {
        int byte1 = throwIfEOF(inputStream.read());
        int byte2 = throwIfEOF(inputStream.read()) << 8;

        return byte1 + byte2;
    }

    /**
     * Überspringt die nächsten [n] Bytes des darunterliegenden InputStream.
     * Es wird ein Exception ausgegeben, falls beim Einlesen auf das Ende des Streams zugegriffen wird.
     */
    public void skip(long n) throws IOException {
        long numberOfSkippedBytes = inputStream.skip(n);

        if (!(numberOfSkippedBytes == n)) {
            throw new PropraException("Das Dateiende wurde vorzeitig erreicht. Erwartet waren " + n + " Bytes. Gelesen wurden jedoch nur " + numberOfSkippedBytes + ".");
        }
    }

    /**
     * Liest die nächsten [n] Bytes des darunterliegenden InputStream in das [targetArray].
     * Es wird ein Exception ausgegeben, falls beim Einlesen auf das Ende des Streams zugegriffen wird.
     */
    public void readN(byte[] targetArray, int n) throws IOException {
        long numberOfReadBytes = inputStream.read(targetArray, 0, n);

        if (!(numberOfReadBytes == n)) {
            throw new PropraException("Das Dateiende wurde vorzeitig erreicht. Erwartet waren " + n + " Bytes. Gelesen wurden jedoch nur " + numberOfReadBytes + ".");
        }
    }

    @Override public void close() throws IOException {
        inputStream.close();
    }

    /**
     * Liest ein DWord (4 Byte vorzeichenlos) aus dem darunterliegenden InputStream im Little-Endian-Format aus.
     * Es wird ein Exception ausgegeben, falls beim Einlesen auf das Ende des Streams zugegriffen wird.
     */
    public long readDword() throws IOException {
        long byte1 = throwIfEOF(inputStream.read());
        long byte2 = ((long) throwIfEOF(inputStream.read())) << 8L;
        long byte3 = ((long) throwIfEOF(inputStream.read())) << 16L;
        long byte4 = ((long) throwIfEOF(inputStream.read())) << 24L;

        return byte1 + byte2 + byte3 + byte4;
    }
}
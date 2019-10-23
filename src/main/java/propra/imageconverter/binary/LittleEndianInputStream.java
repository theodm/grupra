package propra.imageconverter.binary;

import propra.PropraException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Hilfsklasse um Daten eines darunterliegenden Eingabestreams zu lesen. Bietet
 * geeignete Methoden um Binärdaten in Little-Endian zu lesen.
 * <p>
 * Bei den read***-Methoden wird jeweils der nächstgrößer numerische
 * Datentyp zurückgegeben, um das Handling von vorzeichenlosen Zahlen zu
 * vereinfachen. Java unterstützt nativ keine vorzeichenlosen Zahlen.
 * <p>
 */
public class LittleEndianInputStream implements AutoCloseable {
    // Die Klasse könnte auch von InputStream ableiten,
    // das war aber bisher entbehrlich. (YAGNI)
    private final InputStream inputStream;

    public LittleEndianInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int read() throws IOException {
        return inputStream.read();
    }

    /**
     * Liest ein vorzeichenloses Byte aus dem darunterliegenden Eingabestream aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public int readUByte() throws IOException {
        int byteRead = inputStream.read();

        if (byteRead == -1) {
            throw new PropraException("Das Dateiende wurde vorzeitig erreicht.");
        }

        return byteRead;
    }

    /**
     * Liest ein vorzeichenloses Short (2 Byte) aus dem darunterliegenden Eingabestream im Little-Endian-Format aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public int readUShort() throws IOException {
        int byte1 = readUByte();
        int byte2 = readUByte() << 8;

        return byte1 + byte2;
    }

    /**
     * Liest ein vorzeichenloses Int (4 Byte) aus dem darunterliegenden Eingabestream im Little-Endian-Format aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public long readUInt() throws IOException {
        long byte1 = readUByte();
        long byte2 = readUByte() << 8L;
        long byte3 = readUByte() << 16L;
        long byte4 = ((long) readUByte()) << 24L;

        return byte1 + byte2 + byte3 + byte4;
    }

    /**
     * Liest ein vorzeichenloses Long (8 Byte) aus dem darunterliegenden Eingabestream im Little-Endian-Format aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public BigInteger readULong() throws IOException {
        BigInteger byte1 = BigInteger.valueOf(readUByte()).shiftLeft(0);
        BigInteger byte2 = BigInteger.valueOf(readUByte()).shiftLeft(8);
        BigInteger byte3 = BigInteger.valueOf(readUByte()).shiftLeft(16);
        BigInteger byte4 = BigInteger.valueOf(readUByte()).shiftLeft(24);
        BigInteger byte5 = BigInteger.valueOf(readUByte()).shiftLeft(32);
        BigInteger byte6 = BigInteger.valueOf(readUByte()).shiftLeft(40);
        BigInteger byte7 = BigInteger.valueOf(readUByte()).shiftLeft(48);
        BigInteger byte8 = BigInteger.valueOf(readUByte()).shiftLeft(56);

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
     * Überspringt die nächsten [n] Bytes des darunterliegenden Eingabestream.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public void skip(int n) throws IOException {
        // Wir nutzen readFully statt Skip, da
        // die Funktion bei Erreichen des EOF
        // eine Exception auswirft.
        readFully(new byte[n]);
    }

    /**
     * Liest die nächsten [n] Bytes des darunterliegenden Eingabestreams in das [targetArray].
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public void readFully(byte[] targetArray) throws IOException {
        int bytesRead = inputStream.read(targetArray);

        if (bytesRead != targetArray.length) {
			throw new PropraException("Das Dateiende wurde vorzeitig erreicht.");
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
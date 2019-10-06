package propra.imageconverter.binary;

import propra.PropraException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.channels.Channels;

/**
 * Hilfsklasse um Daten eines darunterliegenden Eingabestreams zu lesen. Bietet
 * geeignete Methoden um Binärdaten in Little-Endian zu lesen.
 * <p>
 * Bei den read***-Methoden wird jeweils der nächstgrößer numerische
 * Datentyp zurückgegeben, um das Handling von vorzeichenlosen Zahlen zu
 * vereinfachen. Java unterstützt nativ keine vorzeichenlosen Zahlen.
 * <p>
 * Der BinaryReader unsterstützt einen sogenannten Stream-Mode:
 * <p>
 * Man kann an beliebegen Stellen innerhalb der Datei einen
 * BufferedInputStream erstellen, mit dem man die Daten effizient
 * einlesen kann. Während der Stream-Mode aktiviert ist, können die anderen
 * Methoden des BinaryReader nicht genutzt werden.
 * <p>
 * Das ganze wird gemacht, da die zugrundeliegende Datenstruktur RandomAccessFile
 * nicht gebuffert wird und somit sehr ineffizient für kleine Schreib- und Lesevorgänge ist.
 * Bessere Lösung wäre es, dieses Buffering einzuführen, dies wurde aber kurzfristig zu komplex.
 */
public class BinaryReader implements AutoCloseable {
    private final RandomAccessFile dataInput;
    /**
     * Gibt die Position innerhalb der Datei an,
     * unmittelbar bevor der Stream-Mode gestartet wurde.
     * <p>
     * Nach Freigeben des Streams wird der Filecursor wieder
     * auf diesen Wert zurückgesetzt.
     * <p>
     * Der Wert -1 steht dafür, dass sich der Reader nicht im
     * Stream-Mode befindet.
     */
    protected long filePositionBeforeStreamStart = -1;

    public BinaryReader(RandomAccessFile dataInput) {
        this.dataInput = dataInput;
    }

    /**
     * Wirft eine Exception, falls sich der Reader im
     * Stream-Mode befindet. Erklärung des Stream-Mode siehe Klassenkommentar.
     */
    protected void throwIfInStreamMode() {
        if (filePositionBeforeStreamStart != -1)
            throw new PropraException("Auf die Instanz des BinaryReader bzw. BinaryWriter kann zurzeit nicht zugegriffen werden, da der Stream-Modus aktiviert ist.");
    }

    /**
     * Liest ein vorzeichenloses Byte aus dem darunterliegenden Eingabestream aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public int readUByte() throws IOException {
        throwIfInStreamMode();

        return dataInput.readUnsignedByte();
    }

    /**
     * Liest ein vorzeichenloses Short (2 Byte) aus dem darunterliegenden Eingabestream im Little-Endian-Format aus.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public int readUShort() throws IOException {
        throwIfInStreamMode();

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
        throwIfInStreamMode();

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
        throwIfInStreamMode();

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
     * Setzt den Datei-Cursor an die Position [pos] ausgehend
     * vom Anfang der Datei.
     */
    public void seek(long pos) throws IOException {
        throwIfInStreamMode();

        dataInput.seek(pos);
    }

    /**
     * Überspringt die nächsten [n] Bytes des darunterliegenden Eingabestream.
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public void skip(int n) throws IOException {
        throwIfInStreamMode();

        // Wir nutzen readFully statt Skip, da
        // die Funktion bei Erreichen des EOF
        // eine Exception auswirft.
        dataInput.readFully(new byte[n]);
    }

    /**
     * Gibt einen BufferedInputStream von der aktuellen Stelle in
     * der Datei zurück und überführt den Reader in den Stream-Mode.
     * <p>
     * Der Benutzer muss den BufferedInputStream mittels releaseInputStream
     * wieder freigeben.
     * <p>
     * Für eine Erläuterung des Stream-Mode: Siehe Klassenkommentar.
     */
    public BufferedInputStream bufferedInputStream() throws IOException {
        throwIfInStreamMode();

        // Alte Position in der Datei merken,
        // um sie wiederherzustellen
        filePositionBeforeStreamStart = dataInput.getFilePointer();

        return new BufferedInputStream(Channels.newInputStream(dataInput.getChannel()));
    }

    /**
     * Beendet den Stream-Mode und gibt den BufferedInputStream wieder frei.
     */
    public void releaseInputStream() throws IOException {
        // Alte Position wiederherstellen
        dataInput.seek(filePositionBeforeStreamStart);
        filePositionBeforeStreamStart = -1;

        // BufferedInputStream darf nicht geschlossen werden,
        // da sonst der zugrundeliegende FileChannel geschlossen würde.
    }

    /**
     * Liest die nächsten [n] Bytes des darunterliegenden Eingabestreams in das [targetArray].
     * <p>
     * Wirft eine Exception, falls das Ende der Datei erreicht wird.
     */
    public void readFully(byte[] targetArray) throws IOException {
        throwIfInStreamMode();

        dataInput.readFully(targetArray);
    }

    @Override
    public void close() throws IOException {
        dataInput.close();
    }

    /**
     * Gibt zurück, ob sich der Datei-Cursor am Ende der
     * Datei befindet. Ändert den Datei-Cursor nicht.
     */
    public boolean isAtEndOfFile() throws IOException {
        throwIfInStreamMode();

        long lastFilePointer = dataInput.getFilePointer();
        int byteRead = dataInput.read();
        dataInput.seek(lastFilePointer);

        return byteRead == -1;
    }
}
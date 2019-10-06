package propra.imageconverter.binary;

import propra.PropraException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.channels.Channels;

/**
 * Die Klasse wrappt einen Eingabe- und Ausgabestream und bietet
 * einfache Methoden zum Schreiben und Lesen von Daten im Little-Endian-Format
 * an.
 * <p>
 * Die Klasse unterstützt den sogenannten Stream-Mode wie der BinaryReader.
 */
public final class BinaryReadWriter extends BinaryReader implements AutoCloseable {
    private final RandomAccessFile dataInputOutput;
    /**
     * Aktueller BufferedOutputStream, falls sich der BinaryReadWriter im Stream-Mode befindet.
     */
    private BufferedOutputStream lastBufferedOutputStream = null;

    public BinaryReadWriter(RandomAccessFile dataInputOutput) {
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
        throwIfInStreamMode();

        if (value < 0 || value >= (2 << 7))
            throwOutOfBounds();

        dataInputOutput.write(value);
    }

    /**
     * Schreibt ein vorzeichenloses Short (2 Byte) in den Datenstream.
     */
    public void writeUShort(int value) throws IOException {
        throwIfInStreamMode();

        if (value < 0 || value >= (2 << 15))
            throwOutOfBounds();

        dataInputOutput.write(value % 256);
        dataInputOutput.write((value >>> 8) % 256);
    }

    /**
     * Schreibt ein vorzeichenloses Int (4 Byte) in den Datenstream.
     */
    public void writeUInt(long value) throws IOException {
        throwIfInStreamMode();

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
        throwIfInStreamMode();

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
     * Gibt einen BufferedOutputStream von der aktuellen Stelle in
     * der Datei zurück und überführt den Reader in den Stream-Mode.
     * <p>
     * Der Benutzer muss den BufferedOutputStream mittels releaseOutputStream
     * wieder freigeben.
     * <p>
     * Für eine Erläuterung des Stream-Mode: Siehe Klassenkommentar.
     */
    public BufferedOutputStream bufferedOutputStream() throws IOException {
        throwIfInStreamMode();

        // Alte Dateiposition merken
        filePositionBeforeStreamStart = dataInputOutput.getFilePointer();
        lastBufferedOutputStream = new BufferedOutputStream(Channels.newOutputStream(dataInputOutput.getChannel()));
        return lastBufferedOutputStream;
    }

    /**
     * Beendet den Stream-Mode und gibt den BufferedOutputStream wieder frei.
     */
    public void releaseBufferedOutputStream() throws IOException {
        // Noch nicht gespeicherte Daten des
        // Ausgabestreams schreiben
        lastBufferedOutputStream.flush();
        lastBufferedOutputStream = null;

        // Alte Dateiposition wiederherstellen
        dataInputOutput.seek(filePositionBeforeStreamStart);
        filePositionBeforeStreamStart = -1;

        // BufferedOutputStream darf nicht geschlossen werden,
        // da sonst der zugrundeliegende FileChannel geschlossen würde.
    }

    /**
     * Schreibt ein Byte-Array in den Datenstream.
     */
    public void writeN(byte[] sourceArray) throws IOException {
        throwIfInStreamMode();

        dataInputOutput.write(sourceArray);
    }

    @Override
    public void close() throws IOException {
        // Noch nicht gespeicherte Daten des
        // Ausgabestreams schreiben
        if (lastBufferedOutputStream != null) {
            lastBufferedOutputStream.flush();
        }

        dataInputOutput.close();
    }
}

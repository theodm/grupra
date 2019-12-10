package propra.imageconverter.base;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Ermöglicht das Schreiben von Bitströmen mit einer Bitzahl von
 * weniger als einem Byte.
 */
public final class BitOutputStream implements AutoCloseable {
    private final OutputStream origin;

    /**
     * Das aktuelle Byte, in dem die geschriebenen Bits zunächst gesammelt werden. Wird geschrieben,
     * wenn alle 8 Bits geschrieben wurden.
     */
    private int currentByte = 0;

    private int counter = 0;
    private int counterBytes = 0;

    /**
     * Der aktuelle Bit-Index innerhalb des geschriebenen Bytes. Bis dahin sind
     * die Bits innerhalb dieses Bytes bereits geschrieben.
     */
    private int currentBitOffset = 0;

    public BitOutputStream(OutputStream origin) {
        this.origin = origin;
    }

    /**
     * Schreibt die nächsten {@param numberOfBits} in den darunterliegenden Ausgabestream mit dem übergebenen
     * Wert {@param value}.
     */

    public void writeBits(int numberOfBits, int value) throws IOException {
        while (numberOfBits > 0) {
            // TODO
            writeBits8(numberOfBits, value % 8);
            numberOfBits = numberOfBits - 8;
            value = value >> 8;
        }
    }

    private void writeBits8(int numberOfBits, int value) throws IOException {
        counter += numberOfBits;
        int leftByte = currentByte;
        // Wir schauen uns das ganze wieder in
        // einer 16-bittigen Zahl an
        int combinedBytes = leftByte << 8;

        // Nun verschieben wir den Wert entsprechend
        // die richtige Stelle.
        combinedBytes = combinedBytes + (value << (16 - currentBitOffset - numberOfBits));

        // Wir extrahieren die Daten in das aktuelle Byte
        // und das womöglich nächste Byte
        int rightByte = combinedBytes & 0b1111_1111;
        leftByte = (combinedBytes & 0b1111_1111_0000_0000) >> 8;

        if (currentBitOffset + numberOfBits >= 8) {
            // Es wird über die Byte-Grenze hinaus geschrieben
            // daher schreiben wir das aktuelle Byte
            currentBitOffset = (currentBitOffset + numberOfBits) % 8;
            origin.write(leftByte);
            counterBytes++;

            // und das neue Byte wird das aktuelle Byte für den nächsten
            // Durchlauf
            currentByte = rightByte;
        } else {
            // Ansonsten, keine Byte-Grenze überschritten
            // daher nur das Bit-Offset erhöhen
            currentBitOffset += numberOfBits;
            currentByte = leftByte;
        }
    }

    @Override
    public void close() throws IOException {
        origin.close();
    }
}

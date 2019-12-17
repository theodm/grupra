package propra.imageconverter.base;

import propra.PropraException;

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

    /**
     * Der aktuelle Bit-Index innerhalb des geschriebenen Bytes. Bis dahin sind
     * die Bits innerhalb dieses Bytes bereits geschrieben.
     */
    private int currentBitOffset = 0;

    public BitOutputStream(OutputStream origin) {
        this.origin = origin;
    }

    /**
     * Schreibt die nächsten {@param numberOfBits} Bits in den darunterliegenden Ausgabestream mit dem übergebenen
     * Wert {@param value}. Unterstützt zwischen 0 und 32 Bits.
     */
    public void writeBits(int numberOfBits, int value) throws IOException {
        // 32bit-Unterstützung wird hier einfach dadurch
        // gewährleistet, dass die darunterliegende Methode writeBits8,
        // welche lediglich maximal 8 Bits schreiben kann,
        // mehrmals aufgerufen wird.
        int first8Bits = (value >> 24) & 0b1111_1111;
        int second8Bits = (value >> 16) & 0b1111_1111;
        int third8Bits = (value >> 8) & 0b1111_1111;
        int fourth8Bits = (value) & 0b1111_1111;

        if (numberOfBits >= 24) {
            writeBits8(numberOfBits % 24, first8Bits);
            writeBits8(8, second8Bits);
            writeBits8(8, third8Bits);
            writeBits8(8, fourth8Bits);
            return;
        }

        if (numberOfBits >= 16) {
            writeBits8(numberOfBits % 16, second8Bits);
            writeBits8(8, third8Bits);
            writeBits8(8, fourth8Bits);
            return;
        }

        if (numberOfBits >= 8) {
            writeBits8(numberOfBits % 8, third8Bits);
            writeBits8(8, fourth8Bits);
            return;
        }

        if (numberOfBits > 0) {
            writeBits8(numberOfBits, fourth8Bits);
        }
    }

    /**
     * Schreibt die nächsten {@param numberOfBits} Bits in den darunterliegenden Ausgabestream mit dem übergebenen
     * Wert {@param value}. Unterstützt zwischen 0 und 8 Bits.
     */
    private void writeBits8(int numberOfBits, int value) throws IOException {
        if (numberOfBits < 0 || numberOfBits > 8) {
            // Zurzeit wird die Unterstützung von > 8 Bits nicht gebraucht;
            // lediglich das Schreiben von mehr als 8 Bits, daher wird der
            // BitInputStream wegen YAGNI nicht erweitert.
            throw new PropraException("BitInputStream#numberOfBits8 kann maximal 8 Bits auf einmal schreiben. (Tatsächlich wurden " + numberOfBits + " angefragt)");
        }

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

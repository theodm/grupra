package propra.imageconverter.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ermöglicht das Lesen von Bitströmen mit einer Bitzahl von
 * weniger als einem Byte.
 */
public class BitInputStream implements AutoCloseable {
    private final InputStream origin;
    /**
     * Gibt die Bitmaske zum Auslesen von Bits
     * bis zur Länge von 1 bis 8 aus einer Zahl zurück.
     */
    // ggf. gäbe es schönere Möglichkeiten, das zu bewerkstelligen?
    private final Map<Integer, Integer> bitMask = Stream.of(
            new SimpleEntry<>(1, 0b1000_0000),
            new SimpleEntry<>(2, 0b1100_0000),
            new SimpleEntry<>(3, 0b1110_0000),
            new SimpleEntry<>(4, 0b1111_0000),
            new SimpleEntry<>(5, 0b1111_1000),
            new SimpleEntry<>(6, 0b1111_1100),
            new SimpleEntry<>(7, 0b1111_1110),
            new SimpleEntry<>(8, 0b1111_1111)
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    /**
     * Der Wert des zuletzt gelesenen Bytes. Dies ist notwendig,
     * da bitweise eingelesen werden kann und das letzte Byte
     * noch nicht fertig gelesen wurde.
     */
    private Integer lastReadByte = null;
    /**
     * Gibt den aktuellen Bit-Index innerhalb des letzten gelesenen Bytes an, der
     * bereits gelesen wurde.
     * <p>
     * Bsp.:
     * Das letzte gelesene Byte ist 0110 0011. Der aktuelle Bit-Index ist 3, das heißt
     * 011 wurde bereits durch den Benutzer der Klasse gelesen.
     */
    private int currentBitOffset = 0;

    public BitInputStream(
            InputStream origin
    ) {
        this.origin = origin;
    }

    /**
     * Liest die nächsten {@param numberOfBits} aus dem darunterliegenden
     * Eingabestream aus.
     *
     * Gibt -1 zurück, falls das Ende des Streams erreicht wurde.
     */
    public int readBits(int numberOfBits) throws IOException {
        // Am Beginn des Streams ist das zuletzt gelesene Byte
        // noch nicht gesetzt; daher muss es hier initial gesetzt werden
        if (lastReadByte == null) {
            lastReadByte = origin.read();
        }

        // Das letzte gelesene Byte war bereits das Ende des darunterliegenden
        // Streams, daher geben teilen wir hier auch das Ende des Streams mit.
        if (lastReadByte == -1) {
            return -1;
        }

        // Vorgehensweise:
        // Wir repräsentieren unser aktuelles Byte und ein
        // ggf. darüberhinausgehendes Byte (falls wir über ein Byte
        // hinauslesen sollen) in einem 16bit-Format.
        //
        // leftByte steht für das akutelle Byte und die ersten 8bit und
        // rightByte für das nächste Byte und die letzten 8bit (falls relevant)
        int leftByte = lastReadByte;
        int rightByte = 0;
        if (currentBitOffset + numberOfBits >= 8) {
            // Wir lesen über das aktuelle Byte hinaus
            // daher müssen wir auch das nächste Byte beachten
            rightByte = origin.read();

            // Das nächste Byte ist für den nächsten Durchlauf
            // das aktuelle byte
            lastReadByte = rightByte;

            if (rightByte == -1) {
                // Ist das Ende der Datei und wir
                // lesen Bits darüber ein (Padding),
                // dann lesen wir einfach nur 0en ein.
                rightByte = 0;
            }
        }
        // Nun stehen in combinedBytes das aktuelle Byte
        // sowie das nächste Byte hintereinander in 16-Bit
        int combinedBytes = (leftByte << 8) + rightByte;

        // Gibt eine Maske an, die es bewerkstelligt nur die Bits aus
        // den Daten zu lesen, die vom Benutzer auch tatsächlich gewollt sind.
        // Dazu wird die Maske auch entsprechend des aktuellen Bit-Index
        // positioniert
        int mask = (bitMask.get(numberOfBits) << 8) >> currentBitOffset;

        // Nun wird die Maske auf die Daten angewendet und danach
        // die extrahierten Bits an die niederwertigsten Stellen geschoben
        // Damit haben wir unser Ergebnis.
        int retVal = (combinedBytes & mask) >> (16 - currentBitOffset - numberOfBits);

        // Nun noch den aktuellen Bit-Index aktualisieren,
        // denn diese Bits haben wir bereits gelesen.
        currentBitOffset = (currentBitOffset + numberOfBits) % 8;

        return retVal;
    }

    @Override
    public void close() throws IOException {
        origin.close();
    }
}

package propra.imageconverter.base;

import propra.PropraException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Die Klasse ermöglicht es Binärdaten
 * in die BaseN-Repräsentierung zu kodieren und
 * aus dieser Repräsentierung wieder zu dekodieren.
 * <p>
 * Unterstützt werden lediglich BaseN für N = 2, 4, 8, 16, 32 und 64.
 */
public final class BaseNEnDecoder {
    /**
     * Bildet die Basis zu dem entsprechenden
     * Anzahl der Bits ab, die zu einem Zeichen
     * des Alphabets werden.
     */
    // Man könnte hier auch den Logarithmus aus den Java-Mathefunktionen verwenden.
    // Dieser arbeitet aber mit Fließkommawerten, insofern ist das hier die sauberere Variante.
    private static final Map<Integer, Integer> baseToLog = Stream.of(
            new SimpleEntry<>(2, 1),
            new SimpleEntry<>(4, 2),
            new SimpleEntry<>(8, 3),
            new SimpleEntry<>(16, 4),
            new SimpleEntry<>(32, 5),
            new SimpleEntry<>(64, 6)
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    private BaseNEnDecoder() {

    }

    /**
     * Überprüft ob das übergebene Alphabet {@param alphabet} von der Klasse verarbeitet werden
     * kann.
     */
    private static void checkSupportedAlphabet(
            String alphabet
    ) {
        if (!baseToLog.containsKey(alphabet.length())) {
            String supportedBases = baseToLog
                    .keySet()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            throw new PropraException("Das Alphabet " + alphabet + " wird nicht unterstützt. Die Länge ist " + alphabet.length() + ". Unterstützt werden nur die Längen " + supportedBases + ".");
        }
    }

    /**
     * Kodiert die Daten aus dem übergebenen Eingabestream {@param inputStream} in eine BaseN-Repräsentierung, wobei
     * N aus dem übergebenen Alphabet {@param alphabet} berechnet wird. Die Ausgabe erfolgt in das übergebene Writer-Objekt
     * {@param writer}.
     * <p>
     * Wirft eine Exception für ein nicht unterstütztes Alphabet.
     */
    public static void encode(
            BitInputStream inputStream,
            Writer writer,
            String alphabet
    ) throws IOException {
        checkSupportedAlphabet(alphabet);

        int bitsToRead = baseToLog.get(alphabet.length());

        while (true) {
            int value = inputStream.readBits(bitsToRead);

            if (value == -1)
                break;

            writer.write(alphabet.charAt(value));
        }
    }

    /**
     * Dekodiert die BaseN-kodierten Daten aus dem übergebenen Reader-Objekt {@param reader} in die repräsentierten Binärdaten,
     * wobei N aus dem übergebenen Alphabet {@param alphabet} berechnet wird. Die Ausgabe erfolgt in den übergebenen
     * Ausgabestream {@param outputStream}
     * <p>
     * Wirft eine Exception für ein nicht unterstütztes Alphabet.
     */
    public static void decode(
            Reader reader,
            BitOutputStream outputStream,
            String alphabet
    ) throws IOException {
        checkSupportedAlphabet(alphabet);

        int bitsToWrite = baseToLog.get(alphabet.length());

        while (true) {
            int value = reader.read();

            if (value == -1)
                break;

            outputStream.writeBits(bitsToWrite, alphabet.indexOf(value));
        }

        // Entsprechend der Augabenstellung wird es durch den BitOutputStream umgesetzt:
        //
        // "Bei der Dekodierung ist das fehlende Padding zu beachten:
        // hört der zu dekodierende Datenstrom auf,
        // werden die Bits eines nicht abgeschlossen dekodierten Bytes verworfen."
    }

}

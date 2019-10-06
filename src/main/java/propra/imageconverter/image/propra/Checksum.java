package propra.imageconverter.image.propra;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Die Implementation des Prüfsummenalgorithmus für Propa-Dateien
 * in einer Form, die es ermöglicht aus einem Stream von Daten
 * die Prüfsumme zu generieren. Somit können die Daten direkt aus
 * einer Datei gelesen werden und müssen nicht im Arbeitsspeicher zwischengespeichert
 * werden.
 */
public final class Checksum {
    /**
     * Der Prüfsummenalgorithmus in einer Form, in dem er einen Stream von
     * Daten ausliest.
     *
     * @param n      Länge der Eingabe
     * @param reader Methode die das nächste Byte der Eingabe zurückgibt
     * @return berechnete Prüfsumme für die aus dem [reader] gelesenen Daten
     */
    public static long calcStreamingChecksum(
            BigInteger n,
            ChecksumByteReader reader
    ) throws IOException {
        BigInteger X = BigInteger.valueOf(65513);

        // Hier befindet sich der im Kursportal vorgestellte Algorithmus.
        // Es wurden die folgenden Anpassungen vorgenommen:
        //
        // - Die rekursive Funktion wurde in einer iterativen Funktion implementiert.
        // - Das Aufsummieren innerhalb der Funktion A wird nicht immer erneut ausgeführt,
        //   sondern die Summen werden zwischengespeichert
        // - Die Aufrufe wurden entsprechend verschoben, sodass die Daten sequentiell aus einem
        //   Stream gelesen wurden.
        // - Es wird BigInteger verwendet um nicht mit vorzeichenlosen Werten abgebildet auf
        //   vorzeichenbehaftete Werte arbeiten zu müssen.
        BigInteger bResult = BigInteger.ONE;
        BigInteger lastASum = BigInteger.ZERO;
        for (BigInteger j = BigInteger.ONE; j.compareTo(n) <= 0; j = j.add(BigInteger.ONE)) {
            int byteRead = reader.readUByte();

            lastASum = lastASum.add(j.add(BigInteger.valueOf(byteRead)));
            bResult = bResult.add(lastASum.remainder(X)).remainder(X);
        }

        BigInteger aResult = lastASum.remainder(X);

        // 2 << 15 == 2^16
        return aResult.multiply(BigInteger.valueOf(2 << 15)).add(bResult).longValueExact();
    }

    /**
     * Das Interface implementiert eine beliebige Datenquelle
     * aus der, der Prüfsummen-Algorithmus seine Daten bekommt.
     */
    @FunctionalInterface
    public interface ChecksumByteReader {
        int readUByte() throws IOException;
    }
}

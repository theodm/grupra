package propra.imageconverter.image.propra;

import java.io.IOException;

/**
 * Die Implementation des Prüfsummenalgorithmus für Propa-Dateien
 * in einer Form, die es ermöglicht aus einem Stream von Daten
 * die Prüfsumme zu generieren. Somit können die Daten direkt aus
 * einer Datei gelesen werden und müssen nicht im Arbeitsspeicher zwischengespeichert
 * werden.
 */
public final class Checksum {
    private Checksum() {

    }

    /**
     * Der Prüfsummenalgorithmus in einer Form, in dem er einen Stream von
     * Daten ausliest. Die Methode ist unter https://moodle-wrm.fernuni-hagen.de/mod/page/view.php?id=40779
     * dolumentiert.
     *
     * @param n      Länge der Eingabe
     * @param reader Methode die das nächste Byte der Eingabe zurückgibt
     * @return berechnete Prüfsumme für die aus dem [reader] gelesenen Daten
     */
    public static long calcStreamingChecksum(
            long n,
            ChecksumByteReader reader
    ) throws IOException {
        long X = 65513;

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
        long bResult = 1;
        long lastASum = 0;
        for (long j = 1; j <= n; j++) {
            int byteRead = reader.readUByte();

            lastASum = (lastASum + (j + byteRead)) % X;
            bResult = (bResult + lastASum) % X;
        }

        long aResult = lastASum;

        // 2 << 15 == 2^16
        return (aResult * (2 << 15)) + bResult;
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

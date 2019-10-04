package propra.imageconverter.image.propra;

import java.io.IOException;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Checksum {
    public static long streamDataAndCalculateChecksum(
            BigInteger n,
            Supplier<Integer> readByte,
            Consumer<Integer> writeByte
    ) throws IOException {
        BigInteger X = BigInteger.valueOf(65513);

        // Hier wurde die rekursive Funktion in eine iterative Funktion
        // umgewandelt, da sonst ein Stackoverflow auftritt.
        //
        // Als zweiter Schritt wird der Aufruf der Methode A integriert,
        // um zu vermeiden, dass die Summierung für jeden Schleifendurchlauf
        // erneut berechnet werden muss.
        BigInteger bResult = BigInteger.ONE;
        BigInteger lastASum = BigInteger.ZERO;
        for (BigInteger j = BigInteger.ONE; j.compareTo(n) <= 0; j = j.add(BigInteger.ONE)) {
            int byteRead = readByte.get();

            // Das einzelne gelesene Byte an den Aufrufer zurückgeben.
            writeByte.accept(byteRead);

            lastASum = lastASum.add(j.add(BigInteger.valueOf(byteRead)));
            bResult = bResult.add(lastASum.remainder(X)).remainder(X);
        }

        BigInteger aResult = lastASum.remainder(X);

        // 2 << 15 == 2^16
        return aResult.multiply(BigInteger.valueOf(2 << 15)).add(bResult).longValueExact();
    }
}

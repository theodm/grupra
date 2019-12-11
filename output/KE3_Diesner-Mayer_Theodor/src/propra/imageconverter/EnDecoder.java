package propra.imageconverter;

import propra.imageconverter.base.BaseNEnDecoder;
import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Start der Dekodier- und Kodierfunktionen
 * mit gegebenen Parametern.
 */
public final class EnDecoder {
    private EnDecoder() {

    }

    /**
     * Kodiert die Eingabedatei {@param inputFile} mittels des übergebenen Alphabets {@param alphabet} in die
     * Ausgabedatei {@param outputFile} mittels BaseN-Kodierung. N wird anhand der Länge des übergebenen Alphabet {@param alphabet}
     * selbst berechnet. Wird der Parameter {@param writeAlphabet} übergeben, wird das Alphabet in die erste Zeile
     * der Ausgabedatei {@param outputFile} geschrieben.
     */
    public static void encode(
            Path inputFile,
            Path outputFile,
            String alphabet,
            boolean writeAlphabet
    ) throws IOException {
        try (BitInputStream bitInputStream = new BitInputStream(new BufferedInputStream(Files.newInputStream(inputFile, StandardOpenOption.READ)))) {
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                if (writeAlphabet) {
                    writer.write(alphabet + "\n");
                }

                BaseNEnDecoder.encode(
                        bitInputStream,
                        writer,
                        alphabet
                );
            }
        }
    }

    /**
     * Dekdiert die mittels BaseN-Kodierung kodierte Eingabedatei {@param inputFile} mittels des übergebenen Alphabets {@param alphabet} in die
     * Ausgabedatei {@param outputFile}. N wird anhand der Länge des übergebenen Alphabet {@param alphabet}
     * selbst berechnet. Wird kein Alphabet mittels des Parmaeter {@param alphabet} übergeben, wird das Alphabet aus der ersten Zeile
     * der Eingabedatei {@param inputFile} eingelesen.
     */
    public static void decode(
            Path inputFile,
            Path outputFile,
            /* @Nullable */ String alphabet
    ) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
            try (BitOutputStream bitOutputStream = new BitOutputStream(new BufferedOutputStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)))) {
                if (alphabet == null) {
                    alphabet = reader.readLine();
                }

                BaseNEnDecoder.decode(
                        reader,
                        bitOutputStream,
                        alphabet
                );
            }
        }

    }
}

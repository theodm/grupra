package propra.imageconverter.image.compression.selector;

import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.CompressionWriter;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CompressionSelector {

    /**
     * Die Methode findet zu einer Liste aus verfügbaren Kompressionen
     * die Kompression, die das optimalste Resultat mit kleinster Länge liefert.
     */
    public static CompressionType findOptimalCompression(
            List<CompressionType> availableCompressions,
            PixelIterator pixelIterator
    ) throws IOException {
        // Anmerkung:
        // Die Methode funktioniert, indem jede Kompressionsart einmal tatsächlich
        // ins Nichts ausgeführt wird und dann gemessen wird, wie lang das Ergebnis ist.
        // Später muss die Komprimierung tatsächlich nochmal ausgeführt werden.
        // Aus Performancegründen wären bessere Varianten denkbar, wie bspsw. das Zwischenspeichern
        // der Ergebnisdaten. Das macht den Code aber deutlich schwieriger nachvollziehbar, daher
        // wurde es hier unterlassen.
        Map<CompressionType, Long> compressionTypeToByteLength
                = new HashMap<>();

        for (CompressionType compressionType : availableCompressions) {
            CompressionWriter compressionWriter
                    = compressionType.getCompressionWriter();

            // Wir schreiben tatsächlich in einen Ausgabestream
            // der nichts tut. Uns interessiert nur die Anzahl
            // der "geschriebenen" Bytes.
            try (BufferedOutputStream out = new BufferedOutputStream(new NullOutputStream())) {
                long compressedLength = compressionWriter
                        .write(pixelIterator, out);

                compressionTypeToByteLength.put(compressionType, compressedLength);
            }

            pixelIterator.reset();
        }

        return compressionTypeToByteLength
                .entrySet()
                .stream()
                .min(Comparator.comparingLong(Map.Entry::getValue))
                // Solange wir mindestens eine verfügbare Kompression übergeben
                // bekommen, kann diese Exception nicht fliegen.
                .orElseThrow()
                .getKey();
    }
}

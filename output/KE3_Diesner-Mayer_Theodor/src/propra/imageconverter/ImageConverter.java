package propra.imageconverter;

import propra.PropraException;
import propra.imageconverter.cmd.CommandLineParser;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.util.PathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ImageConverter {
    private ImageConverter() {

    }

    /**
     * Überprüft, ob die erforderlichen Argumente für die Aktion
     * übergeben wurde und gibt eine Fehlermeldung aus, falls zusätzliche
     * Parameter angegeben wurden aus.
     *
     * @param action       Die aktuell ausgeführte Aktion.
     * @param neededArgs   Die erforderlichen Argumente für diese Aktion
     * @param optionalArgs Die optionalen Argumente für diese Aktion.
     * @param parsedArgs   Alle geparsten Argumente.
     */
    public static void neededCheck(
            String action,
            Set<String> neededArgs,
            Set<String> optionalArgs,
            Map<String, String> parsedArgs
    ) {
        for (String arg : neededArgs) {
            if (!parsedArgs.containsKey(arg)) {
                throw new PropraException("Es wurden nicht alle erforderlichen Argumente für die Aufgabe " + action + " übergeben. Erforderlich sind die Parameter " + String.join(", ", neededArgs) + ".");
            }
        }

        Set<String> incompatibleParams = parsedArgs
                .keySet()
                .stream()
                .filter(it -> !neededArgs.contains(it) && !optionalArgs.contains(it))
                .collect(Collectors.toSet());

        if (incompatibleParams.size() > 0) {
            throw new PropraException("Es wurden für die Aktion " + action + " inkompatible Parameter übergeben. Diese Parameter sind " + String.join(", ", incompatibleParams) + " .");
        }
    }

    /**
     * Convenience für den neededCheck ohne
     * optionale Argumente.
     */
    public static void neededCheck(
            String action,
            Set<String> neededArgs,
            Map<String, String> parsedArgs
    ) {
        neededCheck(action, neededArgs, Collections.emptySet(), parsedArgs);
    }

    /**
     * Gibt zu einem Dateipfad, den Dateipfad zurück,
     * der entsteht, wenn man die Dateiendung {@param removeExtension} weglässt.
     * <p>
     * Hat der Dateipfad diese Dateiendung nicht, wird
     * eine Exception geworfen.
     */
    public static Path removeExtensionIfThere(
            Path file,
            String removeExtension
    ) {
        String currentFileName = file.getFileName().toString();
        String currentExtension = PathUtils.calcFileExtension(currentFileName);
        if (!currentExtension.equals(removeExtension)) {
            throw new PropraException("Die Datei " + file + " hat nicht die benötigte Dateiendung ." + removeExtension + ".");
        }

        return file.getParent().resolve(currentFileName.substring(0, currentFileName.length() - removeExtension.length() - 1));
    }

    /**
     * Gibt zu einem Dateipfad, den Dateipfad zurück,
     * der entsteht, wenn man die Dateiendung {@param newExtension} hinzufügt.
     */
    public static Path appendExtension(
            Path file,
            String newExtension
    ) {
        return file
                .getParent()
                .resolve(file.getFileName() + "." + newExtension);
    }

    /**
     * Führt die Konvertierung enstprechend der Vorgaben aus.
     *
     * @param args Die Argumente für die Kommandozeile. Entsprechend der Anforderungen gibt
     *             es die folgenden Parameter.
     *             <p>
     *             --input Eingabepfad für das zu konvertierende Bild im TGA-Format.
     *             --output Ausgabepfad für das konvertierte Bild im ProPra-Format.
     *             <p>
     *             Beispiel: --input=./src/main/resources/KE1_TestBilder/test_01_uncompressed.tga --output=test.tga
     */
    public static void startWithArgs(String[] args) throws Exception {
        String base32Alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUV";

        Map<String, String> parsedArgs
                = CommandLineParser.parse(args);

        if (parsedArgs.containsKey("encode-base-32")) {
            neededCheck("encode-base-32", Set.of("encode-base-32", "input"), parsedArgs);

            Path input = Paths.get(parsedArgs.get("input"));
            Path output = appendExtension(input, "base-32");

            EnDecoder.encode(
                    input,
                    output,
                    base32Alphabet,
                    false
            );

        } else if (parsedArgs.containsKey("encode-base-n")) {
            neededCheck("encode-base-n", Set.of("encode-base-n", "input"), parsedArgs);

            Path input = Paths.get(parsedArgs.get("input"));
            Path output = appendExtension(input, "base-n");
            String alphabet = parsedArgs.get("encode-base-n");

            EnDecoder.encode(
                    input,
                    output,
                    alphabet,
                    true
            );

        } else if (parsedArgs.containsKey("decode-base-32")) {
            neededCheck("decode-base-32", Set.of("decode-base-32", "input"), parsedArgs);

            Path input = Paths.get(parsedArgs.get("input"));
            Path output = removeExtensionIfThere(input, "base-32");

            EnDecoder.decode(
                    input,
                    output,
                    base32Alphabet
            );

        } else if (parsedArgs.containsKey("decode-base-n")) {
            neededCheck("decode-base-n", Set.of("decode-base-n", "input"), parsedArgs);

            Path input = Paths.get(parsedArgs.get("input"));
            Path output = removeExtensionIfThere(input, "base-n");

            EnDecoder.decode(
                    input,
                    output,
                    null
            );
        } else {
            neededCheck("convert", Set.of("input", "output"), Set.of("compression"), parsedArgs);

            // Um die Abwärtskompatiblität zu KE1 zu gewährleisten,
            // wird für compression als Default-Wert uncompressed genutzt
            String compression = parsedArgs.getOrDefault("compression", "uncompressed");
            String input = parsedArgs.get("input");
            String output = parsedArgs.get("output");

            CompressionType parsedCompressionType
                    = CompressionType.parseCommandLineArgument(compression);

            Converter.convert(
                    Paths.get(input),
                    Paths.get(output),
                    parsedCompressionType
            );
        }
    }

    /**
     * Einstiegspunkt für das Programm entsprechend der Vorgaben.
     */
    public static void main(
            String[] args
    ) {
        try {
            // Die Main-Methode delegiert nur an
            // die Methode startWithArgs, die die Konvertierung
            // durchführt. Da System.exit(...) ausgeführt wird kann die
            // echte Main-Methode nicht durch JUnit ausgeführt werden,
            // da der JUnit-Runner auch beendet würde.
            startWithArgs(args);
        } catch (Exception exception) {
            // Wir fangen hier  alle Exceptions
            // damit sind auch alle Runtime-Exceptions
            // und insbesondere die PropraExceptions berücksichtigt.

            exception.printStackTrace();
            System.err.println("Es ist eine " + exception.getClass().getSimpleName() + " aufgetreten.");
            System.err.println("Folgende Nachricht enthält die Exception: " + (exception.getMessage() != null ? exception.getMessage() : "[Keine Nachricht]"));

            // Fehlerstatuscode zurückgeben
            System.exit(123);
        }

        // Erfolgsstatuscode zurückgeben
        System.exit(0);
    }
}

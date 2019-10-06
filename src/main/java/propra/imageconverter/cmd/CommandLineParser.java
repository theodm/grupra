package propra.imageconverter.cmd;

import propra.PropraException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Ein ganz einfacher Parser für die Kommandozeile. Erstellt aus einem Argument-String
 * eine Map, die das Attribut auf den zugewiesen Wert mappt.
 */
public final class CommandLineParser {
    // Utility-Klasse
    private CommandLineParser() {

    }

    /**
     * Konvertiert ein einzelnes Argument zu einem Paar aus
     * Attribut und Wert.
     * <p>
     * Beispiel:
     * Eingabe: "--input=C:\HalloWelt.txt"
     * Ausgabe: ["input", "C:\HalloWelt.txt"]
     */
    private static String[] parseSingleArgument(String arg) {
        String[] splitted = arg.split("=");

        if (splitted.length != 2
                || !splitted[0].startsWith("--")) {
            throw new PropraException("Das Argument '" + arg + "' ist nicht wohlgeformt. Erwartet ist ein Argument der Form '--XXX=YYY'.");
        }

        return new String[]{
                splitted[0].substring(2),
                splitted[1]
        };
    }

    /**
     * Erstellt aus einem Argument-String der Main-Methode
     * eine Map, die das Attribut auf den zugewiesen Wert mappt.
     * <p>
     * Beispiel:
     * Eingabe: ["--input=C:\HalloWelt.txt", "--output=C:\ByeWorld.bmp"]
     * Ausgabe: {
     * "input": "C:\HalloWelt.txt",
     * "output": "C:\ByeWorld.bmp"
     * }
     */
    public static Map<String, String> parse(String[] args) {
        return Stream.of(args)
                .map(CommandLineParser::parseSingleArgument)
                .collect(Collectors.toMap(arg -> arg[0], arg -> arg[1]));
    }
}

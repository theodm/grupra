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
     * Attribut und Wert der Form --XXX=YYY oder --XXX
     * <p>
     * Beispiel:
     * Eingabe: "--input=C:\HalloWelt.txt"
     * Ausgabe: ["input", "C:\HalloWelt.txt"]
     */
    private static String[] parseSingleArgument(String arg) {
        if (!arg.contains("=")) {
            if (!arg.startsWith("--"))
                throw new PropraException("Das Argument '" + arg + "' ist nicht wohlgeformt. Erwartet ist ein Argument der Form '--XXX=YYY' oder der Form '--XXX'.");

            return new String[]{
                    arg.substring(2),
                    ""
            };
        }

        // Hier wird absichtlich nicht split verwendet:
        // Enthält der Parameterwert selbst ein =, dann würde
        // split fehlerhafterweise ein weiteres Arrayelement daraus machen.
        String paramName = arg.substring(0, arg.indexOf('='));
        String paramValue = arg.substring(arg.indexOf('=') + 1);

        if (!paramName.startsWith("--")) {
            throw new PropraException("Das Argument '" + arg + "' ist nicht wohlgeformt. Erwartet ist ein Argument der Form '--XXX=YYY' oder der Form '--XXX'.");
        }

        return new String[]{
                paramName.substring(2),
                paramValue
        };
    }

    /**
     * Erstellt aus einem Argument-String der Main-Methode
     * eine Map, die das Attribut auf den zugewiesen Wert mappt.
     * <p>
     * Beispiel:
     * Eingabe: ["--input=C:\HalloWelt.txt", "--output=C:\ByeWorld.bmp", "--test"]
     * Ausgabe: {
     * "input": "C:\HalloWelt.txt",
     * "output": "C:\ByeWorld.bmp",
     * "test": ""
     * }
     */
    public static Map<String, String> parse(String[] args) {
        return Stream.of(args)
                .map(CommandLineParser::parseSingleArgument)
                .collect(Collectors.toMap(arg -> arg[0], arg -> arg[1]));
    }
}

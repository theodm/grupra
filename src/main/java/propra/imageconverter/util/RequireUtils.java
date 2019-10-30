package propra.imageconverter.util;

import propra.PropraException;

// ToDO: JAVADOC
public class RequireUtils {
    // ToDO: Alle Vorkommnisse hierauf leiten

    /**
     * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
     */
    private static void require(boolean condition, String message) {
        if (!condition)
            throw new PropraException(message);
    }
}

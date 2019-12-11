package propra.imageconverter.util;

import propra.PropraException;

public final class RequireUtils {
    private RequireUtils() {

    }

    /**
     * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
     */
    public static void require(boolean condition, String message) {
        if (!condition)
            throw new PropraException(message);
    }
}

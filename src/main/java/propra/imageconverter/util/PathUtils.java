package propra.imageconverter.util;

/**
 * Helferleine zum Benutzen
 * von Pfaden und Dateien.
 */
public final class PathUtils {
    private PathUtils() {

    }

    /**
     * Gibt zu einem Dateinamen ohne Pfad die
     * (letzte) Dateiendung in Kleinbuchstaben zurück.
     */
    public static String calcFileExtension(String fileName) {
        String[] fileNameParts = fileName.split("\\.");

        return fileNameParts[fileNameParts.length - 1].toLowerCase();
    }

}

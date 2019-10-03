package propra;

/**
 * Exception-Klasse für das Propra-Projekt.
 * <p>
 * Zwar kann es sinnvoll sein, mehrere Exception-Typen für ein Projekt zu definieren,
 * hier wäre das aber mit unverhältnismäßigen Aufwand verbunden, da wir sowieso
 * alle Exceptions fangen und dann das Programm beenden. (Stichwort YAGNI)
 */
public final class PropraException extends RuntimeException {
    public PropraException(String message) {
        super(message);
    }
}

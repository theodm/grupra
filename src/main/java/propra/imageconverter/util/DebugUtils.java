package propra.imageconverter.util;

import java.util.function.Supplier;

/**
 * Kleine Helfer zum Debuggen.
 */
public final class DebugUtils {
	/**
	 * Debug-Modus für weitere diagnostische Ausgaben.
	 */
	public static final boolean DEBUG_MODE = false;

	private DebugUtils() {

	}

	/**
	 * Logging-Framework für Arme. Die übergebene
	 * Nachricht wird nur ausgewertet und ausgegeben,
	 * falls der Debug-Modus aktiviert ist.
	 */
	public static void log(Supplier<String> messageSupplier) {
		if (DEBUG_MODE)
			System.out.println(messageSupplier.get());
	}
}

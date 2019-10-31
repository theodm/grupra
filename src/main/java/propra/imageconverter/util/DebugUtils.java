package propra.imageconverter.util;

public class DebugUtils {
	public static boolean DEBUG_MODE = true;

	public void log(String message) {
		if (DEBUG_MODE)
			System.out.println(message);
	}
}

package propra.imageconverter.util;

public final class ArrayUtils {
    private ArrayUtils() {

    }

    /**
     * Tauscht in einem Bytearray das Element [index1] mit dem ELement [index2] aus.
     */
    public static void swap(byte[] byteArray, int index1, int index2) {
        byte temp = byteArray[index1];

        byteArray[index1] = byteArray[index2];
        byteArray[index2] = temp;
    }

    public static String debugFormat(byte[] byteArray) {
        StringBuilder result = new StringBuilder();

        for (byte b : byteArray) {
            result.append(String.format("%02X", b));
        }

        return result.toString();
    }

    public static String formatRGBPixelOrNull(byte[] rgb) {
        if (rgb == null)
            return "<null>";

        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }
}
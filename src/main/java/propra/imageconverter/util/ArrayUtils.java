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
}

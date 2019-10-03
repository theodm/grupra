package propra.imageconverter.util;

public class ArrayUtils {
    public static void swap(byte[] byteArray, int index1, int index2) {
        byte temp = byteArray[index1];

        byteArray[index1] = byteArray[index2];
        byteArray[index2] = temp;
    }
}

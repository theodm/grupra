package propra.imageconverter.image.propra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropraParserTest {

    public long calcForString(String str) {
        byte[] test = str.getBytes();

        return new PropraParser().generateChecksum(test, str.length());
    }

    public long calcForByteArray(byte[] bytes) {
        return new PropraParser().generateChecksum(bytes, bytes.length);
    }

    @Test
    void checksumTest() {
        assertEquals(0x00750076, calcForString("t"));
        assertEquals(0x00DC0152, calcForString("te"));
        assertEquals(0x015202A4, calcForString("tes"));
        assertEquals(0x01CA046E, calcForString("test"));
        assertEquals(0x3C56F024, calcForString("Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."));
        assertEquals(0x07AEE0D6, calcForString("Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));

        assertEquals(0x00000001, calcForByteArray(new byte[]{}));
        assertEquals(0x00010002, calcForByteArray(new byte[]{0}));
        assertEquals(0x00020003, calcForByteArray(new byte[]{1}));
        assertEquals(0x00040006, calcForByteArray(new byte[]{0, 1}));
        assertEquals(0x00040007, calcForByteArray(new byte[]{1, 0}));
        assertEquals(0x01820283, calcForByteArray(new byte[]{(byte) 255, (byte) 128}));

    }

}
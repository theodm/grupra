package propra.imageconverter.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitInputStreamTest {

    @Test
    @DisplayName("Lesen exakt bis zum Ende")
    public void testReadBits() throws IOException {
        byte[] data = {
                (byte) 0b1110_0010,
                (byte) 0b1010_0100
        };

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(data));

        assertEquals(bis.readBits(5), 0b1_1100);
        assertEquals(bis.readBits(5), 0b0_1010);
        assertEquals(bis.readBits(6), 0b10_0100);
    }

    @Test
    @DisplayName("Lesen fooba")
    public void testReadBitsFooba() throws IOException {
        byte[] data = "fooba".getBytes();

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(data));

        assertEquals(bis.readBits(5), 0b01100);
        assertEquals(bis.readBits(5), 0b11001);
        assertEquals(bis.readBits(5), 0b10111);
        assertEquals(bis.readBits(5), 0b10110);
        assertEquals(bis.readBits(5), 0b11110);
        assertEquals(bis.readBits(5), 0b11000);
        assertEquals(bis.readBits(5), 0b10011);
        assertEquals(bis.readBits(5), 0b00001);
        assertEquals(bis.readBits(5), -1);
    }

    @Test
    @DisplayName("Lesen über das Ende")
    public void testReadBitsEnd() throws IOException {
        byte[] data = {
                (byte) 0b1110_0010,
                (byte) 0b1010_0100
        };

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(data));

        assertEquals(bis.readBits(5), 0b1_1100);
        assertEquals(bis.readBits(5), 0b0_1010);
        assertEquals(bis.readBits(8), 0b1001_0000);
        assertEquals(bis.readBits(5), -1);
    }
}
package propra.imageconverter.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BitOutputStreamTest {
    @Test
    @DisplayName("Schreiben exakt bis zum Ende")
    public void testWriteBits() throws IOException {
        byte[] expectedData = {
                (byte) 0b1110_0010,
                (byte) 0b1010_0100
        };

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(5, 0b1_1100);
        bos.writeBits(5, 0b0_1010);
        bos.writeBits(6, 0b10_0100);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }


    @Test
    @DisplayName("Schreiben fooba")
    public void testWriteBitsFooba() throws IOException {
        byte[] expectedData = "fooba".getBytes();


        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(5, 0b01100);
        bos.writeBits(5, 0b11001);
        bos.writeBits(5, 0b10111);
        bos.writeBits(5, 0b10110);
        bos.writeBits(5, 0b11110);
        bos.writeBits(5, 0b11000);
        bos.writeBits(5, 0b10011);
        bos.writeBits(5, 0b00001);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }

    @Test
    @DisplayName("Schreiben über das Ende hinaus, letztes wird verworfen")
    public void testWriteEnd() throws IOException {
        byte[] expectedData = {
                (byte) 0b1110_0010,
                (byte) 0b1010_0100
        };

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(5, 0b1_1100);
        bos.writeBits(5, 0b0_1010);
        bos.writeBits(5, 0b1_0010);
        bos.writeBits(5, 0b0_1111);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }
}
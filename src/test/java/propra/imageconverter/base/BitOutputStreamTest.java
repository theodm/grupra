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

    void printByteArray(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1));
        }
        System.out.println();
    }

    @Test
    @DisplayName("Schreiben von 1-32 Bits")
    public void testBits() throws IOException {

        for (int i = 0; i < 32; i++) {
            ByteArrayOutputStream bosOutput
                    = new ByteArrayOutputStream();
            BitOutputStream bos
                    = new BitOutputStream(bosOutput);

            bos.writeBits(i, 0b1111_1111_1111_1111_1111_1111_1111_1111);
            bos.writeBits(32 - i, 0);

            System.out.print("" + i + " : ");
            printByteArray(bosOutput.toByteArray());
        }

    }

    @Test
    @DisplayName("Schreiben von 9 Bits")
    public void testBits9() throws IOException {
        byte[] expectedData = {
                (byte) 0b1111_1111,
                (byte) 0b1000_0000
        };

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(9, 0b1_1111_1111);
        bos.writeBits(7, 0);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }

    @Test
    @DisplayName("Schreiben von 31 Bit5s")
    public void test31Bits() throws IOException {
        byte[] expectedData = {
                (byte) 0b1000_0000,
                (byte) 0b0100_0000,
                (byte) 0b0010_0000,
                (byte) 0b0001_0000,
        };

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(31, 0b01000_0000_0100_0000_0010_0000_0001_000);
        bos.writeBits(1, 0);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }


    @Test
    @DisplayName("Schreiben von 17 Bits")
    public void test32Bits() throws IOException {
        byte[] expectedData = {
                (byte) 0b1000_0000,
                (byte) 0b0100_0000,
                (byte) 0b1000_0000,
        };

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        bos.writeBits(17, 0b1000_0000_0100_0000_1);
        bos.writeBits(7, 0);

        assertArrayEquals(expectedData, bosOutput.toByteArray());
    }
}
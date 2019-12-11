package propra.imageconverter.image.compression.reader.huffman;

import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.IOException;

public class ByteIterator {
    private final PixelIterator origin;

    private byte[] currentPixel;
    private int currentIndexOfPixel = -1;

    public ByteIterator(PixelIterator origin) {
        this.origin = origin;
    }

    public byte readNextByte() throws IOException {
        if (currentIndexOfPixel == -1 || currentIndexOfPixel == 3) {
            currentPixel = origin.readNextPixel();
            currentIndexOfPixel = 0;
        }

        byte result = currentPixel[currentIndexOfPixel];

        currentIndexOfPixel++;

        return result;
    }

    public boolean hasNextByte() {
        return currentIndexOfPixel < 3 || origin.hasNextPixel();
    }

    public void reset() throws IOException {
        origin.reset();
    }
}

package propra.imageconverter.image.compression.reader.huffman;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.image.compression.reader.CompressionReader;

import java.io.IOException;
import java.io.InputStream;

public class HuffmanCompressionReader implements CompressionReader {
    private final BitInputStream inputStream;

    private HuffmanTree.Node huffmanTree;

    public HuffmanCompressionReader(InputStream inputStream) {
        this.inputStream = new BitInputStream(inputStream);
    }

    @Override
    public byte[] readNextPixel() throws IOException {
        if (huffmanTree == null) {
            huffmanTree = HuffmanTree.constructFromStream(inputStream);
        }

        int firstColorValue = huffmanTree.readEncodedData(inputStream);
        int secondColorValue = huffmanTree.readEncodedData(inputStream);
        int thirdColorValue = huffmanTree.readEncodedData(inputStream);

        return new byte[]{
                (byte) firstColorValue,
                (byte) secondColorValue,
                (byte) thirdColorValue
        };
    }
}

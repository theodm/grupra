package propra.imageconverter.image.compression.huffman;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.image.compression.CompressionReader;
import propra.imageconverter.image.compression.huffman.tree.HuffmanTree;
import propra.imageconverter.image.compression.huffman.tree.Node;

import java.io.IOException;
import java.io.InputStream;

public class HuffmanCompressionReader implements CompressionReader {
    private final BitInputStream inputStream;

    /**
     * Aktueller Huffman-Baum, falls er bereits eingelesen wurde,
     * sonst null.
     */
    private Node huffmanTree;

    public HuffmanCompressionReader(InputStream inputStream) {
        this.inputStream = new BitInputStream(inputStream);
    }

    @Override
    public byte[] readNextPixel() throws IOException {
        // Zuerst lesen wir den Huffman-Baum komplett ein.
        if (huffmanTree == null) {
            huffmanTree = HuffmanTree.constructFromStream(inputStream);
        }

        // Nun verbleibt das Auslesen von jeweils 3 Bytes (= 1 Pixel).
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

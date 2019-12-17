package propra.imageconverter.image.compression.huffman.tree;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;

import java.io.IOException;
import java.util.Map;

/**
 * Repräsentiert ein Blatt eines Huffman-Baums.
 */
public final class Leaf implements Node {
    private final byte data;
    private final int numberOfOccurences;
    private InnerNode parent;

    Leaf(InnerNode parent, byte data) {
        this.parent = parent;
        this.data = data;
        this.numberOfOccurences = -1;
    }

    /**
     * Konstruktor mit der Anzahl der Vorkomnisse der Bytes im Ausgabestrom.
     */
    Leaf(InnerNode parent, byte data, int numberOfOccurences) {
        this.parent = parent;
        this.data = data;
        this.numberOfOccurences = numberOfOccurences;
    }

    @Override
    public InnerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(InnerNode node) {
        this.parent = node;
    }

    @Override
    public int readEncodedData(BitInputStream inputStream) {
        return data;
    }

    @Override
    public long writeTree(BitOutputStream outputStream) throws IOException {
        outputStream.writeBits(1, 1);
        outputStream.writeBits(8, Byte.toUnsignedInt(data));

        return 9;
    }

    @Override
    public void constructHuffmanMap(
            Map<Byte, BitPattern> byteToBitPattern,
            int currentBitPattern,
            int currentNumberOfBits
    ) {
        byteToBitPattern.put(data, new BitPattern(currentBitPattern, currentNumberOfBits));
    }

    /**
     * Gibt die im Blatt gespeicherten Daten zurück.
     */
    public int getData() {
        return data;
    }

    @Override
    public int getNumberOfOccurences() {
        return numberOfOccurences;
    }
}

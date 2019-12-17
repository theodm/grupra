package propra.imageconverter.image.compression.huffman.tree;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;
import propra.imageconverter.util.RequireUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Repräsentiert den inneren Knoten eines Huffman-Baums.
 */
public final class InnerNode implements Node {
    private InnerNode parent;
    private Node left;
    private Node right;

    InnerNode(InnerNode parent) {
        this.parent = parent;
    }

    public Node getLeft() {
        return left;
    }

    void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    void setRight(Node right) {
        this.right = right;
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
    public int readEncodedData(BitInputStream inputStream) throws IOException {
        int bit = inputStream.readBits(1);

        if (bit == 0) {
            return getLeft().readEncodedData(inputStream);
        } else {
            return getRight().readEncodedData(inputStream);
        }
    }

    @Override
    public long writeTree(BitOutputStream outputStream) throws IOException {
        long writtenBits = 0;

        outputStream.writeBits(1, 0);
        writtenBits++;

        if (left != null) {
            writtenBits += left.writeTree(outputStream);
        }

        if (right != null) {
            writtenBits += right.writeTree(outputStream);
        }

        return writtenBits;
    }

    @Override
    public void constructHuffmanMap(
            Map<Byte, BitPattern> byteToBitPattern,
            int currentBitPattern,
            int currentNumberOfBits
    ) {
        if (getLeft() != null) {
            getLeft().constructHuffmanMap(byteToBitPattern, (currentBitPattern << 1), currentNumberOfBits + 1);
        }

        if (getRight() != null) {
            getRight().constructHuffmanMap(byteToBitPattern, (currentBitPattern << 1) | 1, currentNumberOfBits + 1);
        }
    }

    @Override
    public int getNumberOfOccurences() {
        int numberOfOccurences = 0;

        if (left != null) {
            numberOfOccurences += left.getNumberOfOccurences();

            RequireUtils.require(left.getNumberOfOccurences() != -1, "Die Anzahl an Vorkomnissen muss für alle Knoten gesetzt sein, falls getNumberOfOccurences genutzt wird.");
        }

        if (right != null) {
            numberOfOccurences += right.getNumberOfOccurences();

            RequireUtils.require(right.getNumberOfOccurences() != -1, "Die Anzahl an Vorkomnissen muss für alle Knoten gesetzt sein, falls getNumberOfOccurences genutzt wird.");
        }

        return numberOfOccurences;
    }
}

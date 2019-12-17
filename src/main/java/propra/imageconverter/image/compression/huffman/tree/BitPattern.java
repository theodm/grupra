package propra.imageconverter.image.compression.huffman.tree;

/**
 * Repräsentiert eine Bitfolge zusammen mit der Anzahl
 * der Bits, die durch sie repräsentiert werden.
 */
public final class BitPattern {
    private final int bitPattern;
    private final int numberOfBits;

    public BitPattern(int bitPattern, int numberOfBits) {
        this.bitPattern = bitPattern;
        this.numberOfBits = numberOfBits;
    }

    public int getBitPattern() {
        return bitPattern;
    }

    public int getNumberOfBits() {
        return numberOfBits;
    }
}

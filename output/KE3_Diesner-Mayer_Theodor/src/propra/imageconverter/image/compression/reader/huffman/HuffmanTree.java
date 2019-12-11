package propra.imageconverter.image.compression.reader.huffman;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HuffmanTree {

    public static List<Node> getSmallestTwo(
            List<Node> nodes
    ) {
        int firstValue = Integer.MAX_VALUE;
        Node firstObject = null;

        int secondValue = Integer.MAX_VALUE;
        Node secondObject = null;

        for (Node node : nodes) {
            if (node.getNumberOfOccurences() < firstValue) {
                secondObject = firstObject;
                secondValue = firstValue;

                firstObject = node;
                firstValue = node.getNumberOfOccurences();
            } else if (node.getNumberOfOccurences() < secondValue) {
                secondObject = node;
                secondValue = node.getNumberOfOccurences();
            }
        }

        // TODO umbennene object
        return Arrays.asList(firstObject, secondObject);
    }

    public static Node constructForOccurenceMap(
            Map<Byte, Integer> occurenceMap
    ) {
        List<Node> nodes = occurenceMap
                .entrySet()
                .stream()
                .map(it -> new Leaf(null, it.getKey(), it.getValue()))
                .sorted(Comparator.comparingInt(a -> a.numberOfOccurences))
                .collect(Collectors.toList());

        while (nodes.size() > 1) {
            List<Node> smallestTwo = getSmallestTwo(nodes);

            Node first = smallestTwo.get(0);
            Node second = smallestTwo.get(1);

            InnerNode parentNode
                    = new InnerNode(null, first.getNumberOfOccurences() + second.getNumberOfOccurences());

            parentNode.setLeft(first);
            parentNode.setRight(second);

            first.setParent(parentNode);
            second.setParent(parentNode);

            nodes.remove(first);
            nodes.remove(second);

            nodes.add(parentNode);
        }

        return nodes.get(0);
    }

    public static Node constructFromStream(
            BitInputStream inputStream
    ) throws IOException {
        boolean isFirstInner = inputStream.readBits(1) == 0;

        if (!isFirstInner) {
            return new Leaf(null, inputStream.readBits(8));
        }

        HuffmanTree.InnerNode rootNode = new InnerNode(null);
        HuffmanTree.InnerNode currentNode = rootNode;

        while (true) {
            boolean isInner = inputStream.readBits(1) == 0;

            // Zunächst erstellen wir den neuen Knoten,
            // entsprechend ob es sich um einen inneren Knoten handelt, oder
            // nicht.
            HuffmanTree.Node nodeToInsert;
            if (isInner) {
                nodeToInsert = new HuffmanTree.InnerNode(currentNode);
            } else {
                nodeToInsert = new HuffmanTree.Leaf(currentNode, inputStream.readBits(8));
            }

            if (currentNode.getLeft() == null) {
                currentNode.setLeft(nodeToInsert);

                if (isInner) {
                    currentNode = (HuffmanTree.InnerNode) nodeToInsert;
                }
            } else if (currentNode.getRight() == null) {
                currentNode.setRight(nodeToInsert);

                if (isInner) {
                    currentNode = (HuffmanTree.InnerNode) nodeToInsert;
                } else {
                    // Bis zum nächsten Knoten zurückgehen,
                    // bei dem noch ein rechter Knoten fehlt
                    while (currentNode.getRight() != null) {
                        currentNode = currentNode.getParent();

                        if (currentNode == null) {
                            return rootNode;
                        }
                    }
                }
            }
        }
    }

    public static interface Node {
        InnerNode getParent();

        void setParent(InnerNode node);

        int getNumberOfOccurences();

        int readEncodedData(BitInputStream inputStream) throws IOException;

        long writeTree(BitOutputStream outputStream) throws IOException;

        void constructHuffmanMap(
                Map<Byte, BitPattern> byteToBitPattern,
                int currentBitPattern,
                int currentNumberOfBits
        );

        default Map<Byte, BitPattern> constructHuffmanMap() {
            Map<Byte, BitPattern> byteToBitPattern =
                    new HashMap<>();

            constructHuffmanMap(
                    byteToBitPattern,
                    0,
                    0
            );

            return byteToBitPattern;
        }
    }

    public static class InnerNode implements Node {
        private final int numberOfOccurences;
        private InnerNode parent;
        private Node left;
        private Node right;

        public InnerNode(InnerNode parent) {
            this.parent = parent;
            this.numberOfOccurences = -1;
        }

        public InnerNode(InnerNode parent, int numberOfOccurences) {
            this.parent = parent;
            this.numberOfOccurences = numberOfOccurences;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
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
            return numberOfOccurences;
        }
    }

    public static class Leaf implements Node {
        private final int data;
        private final int numberOfOccurences;
        private InnerNode parent;

        public Leaf(InnerNode parent, int data) {
            this.parent = parent;
            this.data = data;
            this.numberOfOccurences = -1;
        }

        public Leaf(InnerNode parent, int data, int numberOfOccurences) {
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
        public int readEncodedData(BitInputStream inputStream) throws IOException {
            return data;
        }

        @Override
        public long writeTree(BitOutputStream outputStream) throws IOException {
            outputStream.writeBits(1, 1);
            outputStream.writeBits(8, data);

            return 9;
        }

        @Override
        public void constructHuffmanMap(Map<Byte, BitPattern> byteToBitPattern, int currentBitPattern, int currentNumberOfBits) {
            byteToBitPattern.put((byte) data, new BitPattern(currentBitPattern, currentNumberOfBits));
        }

        public int getData() {
            return data;
        }

        @Override
        public int getNumberOfOccurences() {
            return numberOfOccurences;
        }
    }

    static class BitPattern {
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

        @Override
        public String toString() {
            return "BitPattern{" +
                    "bitPattern=" + bitPattern +
                    ", numberOfBits=" + numberOfBits +
                    '}';
        }
    }
}

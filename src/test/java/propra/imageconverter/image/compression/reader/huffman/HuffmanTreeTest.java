package propra.imageconverter.image.compression.reader.huffman;

import org.junit.jupiter.api.Test;
import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class HuffmanTreeTest {

    public void debugPrint(HuffmanTree.Node node, StringBuilder stringBuilder, String prefix, String childrenPrefix) {
        stringBuilder.append(prefix);
        if (node instanceof HuffmanTree.Leaf)
            stringBuilder.append(((HuffmanTree.Leaf) node).getData());
        stringBuilder.append('\n');

        if (node instanceof HuffmanTree.InnerNode) {
            if (((HuffmanTree.InnerNode) node).getLeft() != null) {
                debugPrint(
                        ((HuffmanTree.InnerNode) node).getLeft(),
                        stringBuilder,
                        childrenPrefix + "├── (l) ",
                        childrenPrefix + "│   "
                );
            }
        }

        if (node instanceof HuffmanTree.InnerNode) {
            if (((HuffmanTree.InnerNode) node).getRight() != null) {
                debugPrint(
                        ((HuffmanTree.InnerNode) node).getRight(),
                        stringBuilder,
                        childrenPrefix + "├── (r) ",
                        childrenPrefix + "│   "
                );
            }
        }

    }

    public void debugPrint(HuffmanTree.Node node) {
        StringBuilder stringBuilder
                = new StringBuilder();

        debugPrint(node, stringBuilder, "", "");

        System.out.println(stringBuilder.toString());
    }

    @Test
    public void moodleExample() throws IOException {
        byte[] data = {
                (byte) 0b001_00000,
                (byte) 0b001_1_0000,
                (byte) 0b0010_01_00,
                (byte) 0b000011_1_0,
                (byte) 0b0000100_0,
        };

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(data));

        HuffmanTree.Node result = HuffmanTree.constructFromStream(bis);

        debugPrint(result);
    }

    @Test
    public void example2() throws IOException {
        byte[] data = {
                (byte) 0x03,
                (byte) 0x01,
                (byte) 0x00,
                (byte) 0x9B,
                (byte) 0xC8,
                (byte) 0x51,
                (byte) 0xC4,
                (byte) 0xCA,
                (byte) 0x86,
                (byte) 0x7F,
                (byte) 0xD4,
                (byte) 0xE8,
                (byte) 0x88,
                (byte) 0x62,
                (byte) 0xCD,
                (byte) 0x63,
                (byte) 0x9B,
                (byte) 0xD8,
                (byte) 0x6F,
                (byte) 0x36,
                (byte) 0xF0,
                (byte) 0x6A,
                (byte) 0xCA,
                (byte) 0x7B, (byte) 0x5E, (byte) 0x0A, (byte) 0x9D, (byte) 0xA5, (byte) 0x48, (byte) 0xBA, (byte) 0x01, (byte) 0xE3, (byte) 0xE4, (byte) 0x68, (byte) 0x5A, (byte) 0x6C, (byte) 0xDE, (byte) 0x16, (byte) 0xC9, (byte) 0x2C, (byte) 0x22, (byte) 0xD2, (byte) 0x72, (byte) 0xDE, (byte) 0x1E, (byte) 0x9E, (byte) 0x9B, (byte) 0xBD, (byte) 0xEE, (byte) 0xC8, (byte) 0x42, (byte) 0xCF, (byte) 0xC8, (byte) 0xFA, (byte) 0x70, (byte) 0xB6, (byte) 0x39, (byte) 0x6B, (byte) 0xD6, (byte) 0xEE, (byte) 0xDD, (byte) 0xED, (byte) 0xDE, (byte) 0xDE, (byte) 0xED, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDE, (byte) 0xED, (byte) 0xEE, (byte) 0xEE, (byte) 0xED, (byte) 0xDE, (byte) 0x98, (byte) 0xC6, (byte) 0xBB, (byte) 0xBD, (byte) 0xDD, (byte) 0xBB, (byte) 0xDB, (byte) 0xDB, (byte) 0xBD, (byte) 0xDB, (byte) 0xDD, (byte) 0x35, (byte) 0xD9, (byte) 0xAE, (byte) 0xCD, (byte) 0xBB, (byte) 0x7B, (byte) 0xB7, (byte) 0x7B, (byte) 0x77, (byte) 0x77, (byte) 0x7B, (byte) 0x77, (byte) 0x66, (byte) 0x31, (byte) 0x82, (byte) 0x10, (byte) 0x98, (byte) 0xC6, (byte) 0xBB, (byte) 0xBB, (byte) 0xDD, (byte) 0x36, (byte) 0xE9, (byte) 0xAE, (byte) 0xCD, (byte) 0x76, (byte) 0x63, (byte) 0x59, (byte) 0x8D, (byte) 0x77, (byte) 0x77, (byte) 0x77, (byte) 0xB7, (byte) 0x76, (byte) 0x6B, (byte) 0xBB, (byte) 0xBB, (byte) 0xBD, (byte) 0xDB, (byte) 0xBB, (byte) 0x35, (byte) 0xDD, (byte) 0xED, (byte) 0xDE, (byte) 0xE9, (byte) 0xB6, (byte) 0xEF, (byte) 0x6E, (byte) 0xF6, (byte) 0xEE, (byte) 0xEE, (byte) 0xEE, (byte) 0xEF, (byte) 0x6C, (byte) 0xD6, (byte) 0x67, (byte) 0x05, (byte) 0xC1, (byte) 0x08, (byte) 0x77, (byte) 0x76, (byte) 0xF7, (byte) 0x6E, (byte) 0xEE, (byte) 0xF7, (byte) 0x6F, (byte) 0x77, (byte) 0x44, (byte) 0x4D, (byte) 0xB7, (byte) 0x7B, (byte) 0x76, (byte) 0x3B, (byte) 0xA4, (byte) 0x3A, (byte) 0x22, (byte) 0x22, (byte) 0x3A, (byte) 0x23, (byte) 0xA3, (byte) 0xBB, (byte) 0xBB, (byte) 0xA2, (byte) 0x22, (byte) 0x44, (byte) 0x24, (byte) 0x42, (byte) 0x42, (byte) 0x24, (byte) 0x42, (byte) 0x25, (byte) 0xA2, (byte) 0x3A, (byte) 0x23, (byte) 0xA2, (byte) 0x3B, (byte) 0xA3, (byte) 0xBA, (byte) 0x22, (byte) 0x24, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x22, (byte) 0x5A, (byte) 0x23, (byte) 0xBA, (byte) 0x22, (byte) 0x22, (byte) 0x44, (byte) 0x42, (byte) 0x24, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x24, (byte) 0x22, (byte) 0x45, (byte) 0xA2, (byte) 0x3A, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x58, (byte) 0x45, (byte) 0x84, (byte) 0x44, (byte) 0x42, (byte) 0x42, (byte) 0x24, (byte) 0x45, (byte) 0x84, (byte) 0x58, (byte) 0x42, (byte) 0x42, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x24, (byte) 0x42, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x42, (byte) 0x24, (byte) 0x45, (byte) 0x84, (byte) 0x38, (byte) 0x42, (byte) 0x44, (byte) 0x24, (byte) 0x22, (byte) 0x42, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x24, (byte) 0x22, (byte) 0x42, (byte) 0x24, (byte) 0x22, (byte) 0x42, (byte) 0x24, (byte) 0x22, (byte) 0x42, (byte) 0x24, (byte) 0x42, (byte) 0x25, (byte) 0xA2, (byte) 0x22, (byte) 0x44, (byte) 0x24, (byte) 0x42, (byte) 0x24, (byte) 0x24, (byte) 0x44, (byte) 0x58, (byte) 0x45, (byte) 0x82, (byte) 0x44, (byte) 0x22, (byte) 0x22, (byte) 0x44, (byte) 0x22, (byte) 0x24, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x44, (byte) 0x5A, (byte) 0x23, (byte) 0xA2, (byte) 0x22, (byte) 0x22, (byte) 0x24, (byte) 0x22, (byte) 0x44, (byte) 0x58, (byte) 0x45, (byte) 0x84, (byte) 0x58, (byte) 0x45, (byte) 0x84, (byte) 0x59, (byte) 0x83, (byte) 0x98, (byte) 0x24, (byte) 0x44, (byte) 0x45, (byte) 0x98, (byte) 0xBC, (byte) 0xCC, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x21, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0xC2, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x12, (byte) 0x22, (byte) 0xC2, (byte) 0x2C, (byte) 0x21, (byte) 0x22, (byte) 0x12, (byte) 0x22, (byte) 0xC2, (byte) 0x12, (byte) 0x21, (byte) 0x22, (byte) 0x12, (byte) 0x21, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x21, (byte) 0x11, (byte) 0x11, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0xC2, (byte) 0xCC, (byte) 0xC2, (byte) 0x22, (byte) 0x11, (byte) 0x11, (byte) 0x22, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x12, (byte) 0x12, (byte) 0x22, (byte) 0x2C, (byte) 0x22, (byte) 0x22, (byte) 0x12, (byte) 0x11, (byte) 0x21, (byte) 0x1C, (byte) 0x21, (byte) 0x22, (byte) 0xD1, (byte) 0x1D, (byte) 0x11, (byte) 0xD1, (byte) 0x1D, (byte) 0x11, (byte) 0xD1, (byte) 0x1D, (byte) 0x11, (byte) 0x11, (byte) 0x21, (byte) 0x12, (byte) 0x12, (byte) 0x21, (byte) 0x22, (byte) 0x2C, (byte) 0xC2, (byte) 0xCC, (byte) 0x12, (byte) 0x21, (byte) 0x12, (byte) 0x12, (byte) 0x2C, (byte) 0x5A, (byte) 0xCB, (byte) 0x16, (byte) 0x45, (byte) 0x84, (byte) 0x58, (byte) 0x58, (byte) 0xB2, (byte) 0x2C, (byte) 0x22, (byte) 0xCC, (byte) 0xC5, (byte) 0xE6, (byte) 0x61, (byte) 0x11, (byte) 0x09, (byte) 0x10, (byte) 0x90, (byte) 0x89, (byte) 0x09, (byte) 0x11, (byte) 0x16, (byte) 0x11, (byte) 0x61, (byte) 0x09, (byte) 0x10, (byte) 0x91, (byte) 0x16, (byte) 0x10, (byte) 0x91, (byte) 0x09, (byte) 0x08, (byte) 0x90, (byte) 0x96, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11
        };

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(data));

        HuffmanTree.Node result = HuffmanTree.constructFromStream(bis);

        debugPrint(result);


        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        result.writeTree(bos);

        System.out.println(Arrays.toString(bosOutput.toByteArray()));
    }

    @Test
    public void constructTreeTest() throws IOException {
        Map<Byte, Integer> map = Stream.of(
                new HashMap.SimpleEntry<>((byte) 1, 8),
                new HashMap.SimpleEntry<>((byte) 2, 2000),
                new HashMap.SimpleEntry<>((byte) 3, 100),
                new HashMap.SimpleEntry<>((byte) 4, 2),
                new HashMap.SimpleEntry<>((byte) 7, 50)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        HuffmanTree.Node result = HuffmanTree.constructForOccurenceMap(map);

        debugPrint(result);

        var x = result.constructHuffmanMap();

        System.out.println(x);

        ByteArrayOutputStream bosOutput
                = new ByteArrayOutputStream();
        BitOutputStream bos
                = new BitOutputStream(bosOutput);

        result.writeTree(bos);

        System.out.println(Arrays.toString(bosOutput.toByteArray()));

        BitInputStream bis
                = new BitInputStream(new ByteArrayInputStream(bosOutput.toByteArray()));

        HuffmanTree.Node result2 = HuffmanTree.constructFromStream(bis);

        debugPrint(result2);
    }

}
package propra.imageconverter.image.compression.huffman;

import propra.imageconverter.base.BitOutputStream;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.CompressionWriter;
import propra.imageconverter.image.compression.huffman.tree.BitPattern;
import propra.imageconverter.image.compression.huffman.tree.HuffmanTree;
import propra.imageconverter.image.compression.huffman.tree.Node;
import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCompressionWriter implements CompressionWriter {
    @Override
    public long write(PixelIterator pixelData, BufferedOutputStream outputStream) throws IOException {
        return write(new ByteIterator(pixelData), new BitOutputStream(outputStream));
    }

    @Override
    public CompressionType getCompressionType() {
        return CompressionType.HUFFMAN;
    }

    private long write(ByteIterator byteData, BitOutputStream outputStream) throws IOException {
        Map<Byte, Integer> byteToOccurences
                = new HashMap<>();

        // Zuerst schauen wir, wie oft ein Byte des Datenstroms
        // in ihm vorkommt
        while (byteData.hasNextByte()) {
            byte currentByte = byteData.readNextByte();

            int oldNumberOfOccurences = byteToOccurences.getOrDefault(currentByte, 0);
            byteToOccurences.put(currentByte, oldNumberOfOccurences + 1);
        }

        // Wir setzen den Datenstrom zurück, denn jetzt müssen
        // wir die ganzen kodierten Daten schreiben
        byteData.reset();

        // Mit den entsprechenden Anzahlen können wir den Huffman-Baum generieren.
        Node huffmanTree
                = HuffmanTree.constructForOccurenceMap(byteToOccurences);

        // Dann schreiben wir ihn in die Datei
        long writtenBits
                = huffmanTree.writeTree(outputStream);

        Map<Byte, BitPattern> huffmanMap
                = huffmanTree.constructHuffmanMap();

        // Nun müssen wir nurnoch die Daten des Stroms
        // kodiert in die Datei schreiben.
        while (byteData.hasNextByte()) {
            BitPattern selectedPattern
                    = huffmanMap.get(byteData.readNextByte());

            outputStream.writeBits(
                    selectedPattern.getNumberOfBits(),
                    selectedPattern.getBitPattern()
            );

            writtenBits += selectedPattern.getNumberOfBits();
        }

        // Und dann füllen wir mit Nullen auf.
        long paddingBits = writtenBits % 8;
        outputStream.writeBits((int) paddingBits, 0);
        writtenBits += paddingBits;

        return writtenBits / 8;
    }
}

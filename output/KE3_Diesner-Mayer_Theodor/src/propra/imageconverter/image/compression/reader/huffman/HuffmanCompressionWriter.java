package propra.imageconverter.image.compression.reader.huffman;

import propra.imageconverter.base.BitOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.image.compression.writer.CompressionWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCompressionWriter implements CompressionWriter {
    @Override
    public long write(PixelIterator pixelData, BufferedOutputStream outputStream) throws IOException {
        return write(new ByteIterator(pixelData), new BitOutputStream(outputStream));
    }

    private long write(ByteIterator byteData, BitOutputStream outputStream) throws IOException {
        Map<Byte, Integer> byteToOccurences
                = new HashMap<>();

        while (byteData.hasNextByte()) {
            byte currentByte = byteData.readNextByte();

            int oldNumberOfOccurences = byteToOccurences.getOrDefault(currentByte, 0);
            byteToOccurences.put(currentByte, oldNumberOfOccurences + 1);
        }

        byteData.reset();

        HuffmanTree.Node huffmanTree
                = HuffmanTree.constructForOccurenceMap(byteToOccurences);

        long writtenBits
                = huffmanTree.writeTree(outputStream);

        Map<Byte, HuffmanTree.BitPattern> huffmanMap
                = huffmanTree.constructHuffmanMap();

        while (byteData.hasNextByte()) {
            HuffmanTree.BitPattern selectedPattern
                    = huffmanMap.get(byteData.readNextByte());

            outputStream.writeBits(
                    selectedPattern.getNumberOfBits(),
                    selectedPattern.getBitPattern()
            );

            writtenBits += selectedPattern.getNumberOfBits();
        }

        long paddingBits = writtenBits % 8;
        outputStream.writeBits((int) paddingBits, 0);
        writtenBits += paddingBits;

        return writtenBits / 8;
    }
}

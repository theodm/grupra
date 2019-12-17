package propra.imageconverter.image.compression.huffman.tree;

import propra.imageconverter.base.BitInputStream;
import propra.imageconverter.base.BitOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert einen Knoten eines
 * Huffman-Baums.
 */
public interface Node {
    /**
     * Gibt den Vaterknoten zurück, falls dieser existiert, sonst null.
     */
    InnerNode getParent();

    /**
     * Setzt den Vaterknoten.
     */
    void setParent(InnerNode node);

    /**
     * Gibt die Anzahl der Vorkomnisse der Daten der darunterliegenden Blattknoten
     * im Eingangsdatenstrom zurück. Dieser Wert ist nur für zu schreibende Huffman-Bäume
     * gesetzt und relevant.
     */
    int getNumberOfOccurences();

    /**
     * Liest mithilfe des aktuellen (Teil)baums den Eingabestrom
     * das nächste kodierte Byte ein.
     */
    int readEncodedData(BitInputStream inputStream) throws IOException;

    /**
     * Schreibt den durch den Knoten repräsentierten Teilbaum in den
     * übergebenen Ausgabestream.
     */
    long writeTree(BitOutputStream outputStream) throws IOException;

    /**
     * Erstellt eine Map, die für jedes verwendete Byte, das entsprechende
     * BitPattern zurück gibt, mit dem das Byte in der Datei kodiert wird.
     * <p>
     * Die übergebene Map {@param byteToBitPattern} wird modifizert.
     * <p>
     * Aufrufer sollten die komfortablere Methode (unten) verwenden.
     */
    void constructHuffmanMap(
            Map<Byte, BitPattern> byteToBitPattern,
            int currentBitPattern,
            int currentNumberOfBits
    );

    /**
     * Erstellt eine Map, die für jedes verwendete Byte, das entsprechende
     * BitPattern zurück gibt, mit dem das Byte in der Datei kodiert wird.
     */
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

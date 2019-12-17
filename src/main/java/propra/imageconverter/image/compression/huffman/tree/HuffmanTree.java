package propra.imageconverter.image.compression.huffman.tree;

import propra.PropraException;
import propra.imageconverter.base.BitInputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static propra.imageconverter.util.RequireUtils.require;

public final class HuffmanTree {
    private HuffmanTree() {
    }

    /**
     * Hilfsmethode, die innerhalb einer Liste von Knoten eines
     * Huffman-Baums, die beiden Knoten zurückgibt, deren Anzahl
     * an Vorkomnissen am kleinsten ist.
     * <p>
     * Das Ergebnis der Methode ist undefiniert, falls die Liste
     * keine zwei Elemente mehr enthält.
     */
    private static List<Node> getSmallestTwoNodes(
            List<Node> nodes
    ) {
        int firstValue = Integer.MAX_VALUE;
        Node firstNode = null;

        int secondValue = Integer.MAX_VALUE;
        Node secondNode = null;

        for (Node node : nodes) {
            if (node.getNumberOfOccurences() < firstValue) {
                secondNode = firstNode;
                secondValue = firstValue;

                firstNode = node;
                firstValue = node.getNumberOfOccurences();
            } else if (node.getNumberOfOccurences() < secondValue) {
                secondNode = node;
                secondValue = node.getNumberOfOccurences();
            }
        }

        return Arrays.asList(firstNode, secondNode);
    }

    /**
     * Erstellt einen Huffman-Baum für eine Map, in der
     * jedes verwendete Byte des Datenstroms und die Anzahl der Vorkomnisse innerhalb
     * des Datenstroms angegeben sind.
     */
    public static Node constructForOccurenceMap(
            Map<Byte, Integer> occurenceMap
    ) {
        require(occurenceMap.size() > 0, "Es muss für die Erstellung eines Huffman-Baums mindestens ein Byte in den Daten vorhanden sein.");

        // Wir behandeln den Spezialfall, dass es mindestens
        // einen inneren Knoten geben muss. Wir machen das,
        // indem wir einfach ein weiteres - noch nicht genutztes -
        // Byte hinzufügen.
        if (occurenceMap.size() == 1) {
            byte onlyByte = occurenceMap
                    .keySet()
                    .stream()
                    .findFirst()
                    // Kann nicht passieren, da es zwingend genau ein Element gibt.
                    // (Das gilt zumindest solange kein Multihreading ins Spiel kommt)
                    .orElseThrow(() -> new PropraException("Es ist eine Race-Condition aufgetreten. Bitte kontaktieren Sie einen Entwickler."));

            // Wir nutzen hier absichtlich den Overflow-Mechanismus
            // so vermeiden wir immer, dass wir ein Byte hinzufügen,
            // welches wir noch nicht verwendet haben
            occurenceMap.put((byte) (onlyByte + 1), 0);
        }

        List<Node> nodes = occurenceMap
                .entrySet()
                .stream()
                .map(it -> new Leaf(null, it.getKey(), it.getValue()))
                .sorted(Comparator.comparingInt(Leaf::getNumberOfOccurences))
                .collect(Collectors.toList());

        // Nun wenden wir den Algorithmus zum Herstellen
        // des möglichst optimalen? Baums an.
        while (nodes.size() > 1) {
            List<Node> smallestTwo = getSmallestTwoNodes(nodes);

            Node first = smallestTwo.get(0);
            Node second = smallestTwo.get(1);

            InnerNode parentNode
                    = new InnerNode(null);

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

    /**
     * Erstellt einen Huffman-Baum aus den im Datenstrom übergebenen Daten.
     */
    public static Node constructFromStream(
            BitInputStream inputStream
    ) throws IOException {
        boolean isFirstInner = inputStream.readBits(1) == 0;

        if (!isFirstInner) {
            // Wir arbeiten hier nach dem Robustheitsgrundsatz
            // "be conservative in what you do, be liberal in what you accept from others"
            System.out.println("Es wurde eine komprimierte Datei " +
                    "mit der Huffman-Komprimierung eingelesen, " +
                    "deren Baum nur aus einem Blatt besteht. " +
                    "Nach der Spezifikation wird eine solche Datei nicht " +
                    "unterstützt. Es wird abweichend von der Spezifikation " +
                    "versucht die Datei einzulesen.");

            return new Leaf(null, (byte) inputStream.readBits(8));
        }

        InnerNode rootNode = new InnerNode(null);
        // Aktuell besuchter Knoten
        InnerNode currentNode = rootNode;

        while (true) {
            boolean isInner = inputStream.readBits(1) == 0;

            // Zunächst erstellen wir den neuen Knoten,
            // entsprechend ob es sich um einen inneren Knoten handelt, oder
            // nicht.
            Node nodeToInsert;
            if (isInner) {
                nodeToInsert = new InnerNode(currentNode);
            } else {
                nodeToInsert = new Leaf(currentNode, (byte) inputStream.readBits(8));
            }

            if (currentNode.getLeft() == null) {
                // Wenn der aktuelle Knoten noch links frei ist, dann gehen
                // versuchen wir im Preorder-Verfahren zunächst diesen zu belegen.
                currentNode.setLeft(nodeToInsert);

                if (isInner) {
                    currentNode = (InnerNode) nodeToInsert;
                }
            } else if (currentNode.getRight() == null) {
                // Ansonsten den Rechten
                currentNode.setRight(nodeToInsert);

                // Der rechte Knoten ist nun belegt.
                if (isInner) {
                    // Wenn er selbst ein innerer Knoten ist, dann
                    // müssen wir diesen als nächstes besuchen
                    currentNode = (InnerNode) nodeToInsert;
                } else {
                    // Ansonsten: Bis zum nächsten Knoten zurückgehen,
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
}

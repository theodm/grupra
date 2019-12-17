package propra.imageconverter.image.compression.huffman;

import propra.imageconverter.image.compression.iterator.PixelIterator;

import java.io.IOException;

/**
 * Der ByteIterator macht aus den Daten eines PixelIterator einen
 * Bytestrom. Das macht es für den Verwender leichter, mit den Daten
 * zu arbeiten, falls ihn selbst nicht die Pixeldaten, sondern
 * die Byte-Daten interessieren.
 */
class ByteIterator {
    private final PixelIterator origin;

    /**
     * Aktueller Pixel, der in 3 einzelne Bytes
     * gesplittet wird.
     */
    private byte[] currentPixel;

    /**
     * Aktuelles Byte innerhalb des Pixel.
     */
    private int currentIndexOfPixel = -1;

    ByteIterator(PixelIterator origin) {
        this.origin = origin;
    }

    /**
     * Liest das nächste Byte aus dem Datenstrom aus.
     */
    byte readNextByte() throws IOException {
        if (currentIndexOfPixel == -1 || currentIndexOfPixel == 3) {
            currentPixel = origin.readNextPixel();
            currentIndexOfPixel = 0;
        }

        byte result = currentPixel[currentIndexOfPixel];

        currentIndexOfPixel++;

        return result;
    }

    /**
     * Gibt an, ob ein weiteres Byte
     * im Datenstrom vorhanden ist.
     */
    public boolean hasNextByte() {
        return currentIndexOfPixel < 3 || origin.hasNextPixel();
    }

    /**
     * Setzt den Cursor des Datenstroms
     * auf den Beginn zurück.
     */
    public void reset() throws IOException {
        origin.reset();
        currentIndexOfPixel = -1;
    }
}

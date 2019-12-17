package propra.imageconverter.image.compression.iterator;

import propra.imageconverter.image.ImageReader;

import java.io.IOException;

/**
 * Siehe die Beschreibung von {@link PixelIterator}. Implementiert
 * die Vorausschau auf den nächsten Bildpunkt.
 */
public abstract class PeekAndConvertPixelIterator implements PixelIterator {
    private final ImageReader imageReader;

    /**
     * Vorgespeicherter Bildpunkt, um das Peeken auf den nächsten Bildpunkt zu ermöglichen.
     */
    private byte[] nextPixel;

    protected PeekAndConvertPixelIterator(ImageReader imageReader) throws IOException {
        this.imageReader = imageReader;

        this.nextPixel = convertPixel(imageReader.readNextPixel());
    }

    @Override
    public final void reset() throws IOException {
        imageReader.reset();

        this.nextPixel = convertPixel(imageReader.readNextPixel());
    }

    /**
     * Konvertierungsmethode für den Pixel-Wert von RGB in das spezifizierte
     * Format.
     */
    protected abstract byte[] convertPixel(byte[] inputPixel);

    @Override
    public final byte[] readNextPixel() throws IOException {
        byte[] currentPixel = nextPixel;

        if (imageReader.hasNextPixel())
            nextPixel = convertPixel(imageReader.readNextPixel());
        else
            nextPixel = null;

        return currentPixel;
    }

    @Override
    public final byte[] peekPixel() {
        return nextPixel;
    }

    @Override
    public final boolean hasNextPixel() {
        return nextPixel != null;
    }

    @Override
    public final int getWidth() {
        return imageReader.getWidth();
    }

    @Override
    public final int getHeight() {
        return imageReader.getHeight();
    }
}

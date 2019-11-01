package propra.imageconverter.image.compression.iterator;

import propra.imageconverter.image.ImageReader;

import java.io.IOException;

// TODO Limitationen des PeekandConvertPixelIterator und Tatsache
// dass er nicht so handlebar ist bzw der ImageReader dann unnütz ist.
public abstract class PeekAndConvertPixelIterator implements PixelIterator {
	private final ImageReader imageReader;
	private byte[] nextPixel;

	PeekAndConvertPixelIterator(ImageReader imageReader) throws IOException {
		this.imageReader = imageReader;

		this.nextPixel = convertPixel(imageReader.readNextPixel());
	}

	abstract byte[] convertPixel(byte[] inputPixel);

	@Override public final byte[] readNextPixel() throws IOException {
		byte[] currentPixel = nextPixel;

		if (imageReader.hasNextPixel())
			nextPixel = convertPixel(imageReader.readNextPixel());
		else
			nextPixel = null;

		return currentPixel;
	}

	@Override public final byte[] peekPixel() {
		return nextPixel;
	}

	@Override public final boolean hasNextPixel() {
		return nextPixel != null;
	}
}

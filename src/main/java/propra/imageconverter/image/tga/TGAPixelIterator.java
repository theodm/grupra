package propra.imageconverter.image.tga;

import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.compression.iterator.PeekAndConvertPixelIterator;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;

public final class TGAPixelIterator extends PeekAndConvertPixelIterator {
	private TGAPixelIterator(ImageReader imageReader) throws IOException {
		super(imageReader);
	}

	public static PixelIterator forImageReader(ImageReader imageReader) throws IOException {
		return new TGAPixelIterator(imageReader);
	}

	@Override
	protected byte[] convertPixel(byte[] inputPixel) {
		byte[] outputPixel = Arrays.copyOf(inputPixel, 3);

		// Konvertierung von RGB in BGR
		ArrayUtils.swap(outputPixel, 0, 2);

		return outputPixel;
	}
}

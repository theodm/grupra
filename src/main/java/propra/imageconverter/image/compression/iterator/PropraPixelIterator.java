package propra.imageconverter.image.compression.iterator;

import propra.imageconverter.image.ImageReader;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;

public final class PropraPixelIterator extends PeekAndConvertPixelIterator {
	private PropraPixelIterator(ImageReader imageReader) throws IOException {
		super(imageReader);
	}

	public static PixelIterator forImageReader(ImageReader imageReader) throws IOException {
		return new PropraPixelIterator(imageReader);
	}

	@Override byte[] convertPixel(byte[] inputPixel) {
		byte[] outputPixel = Arrays.copyOf(inputPixel, 3);

		// Konvertierung von RGB in GBR
		ArrayUtils.swap(outputPixel, 1, 2);
		ArrayUtils.swap(outputPixel, 0, 2);

		return outputPixel;
	}
}

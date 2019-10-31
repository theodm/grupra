package propra.imageconverter.image.tga.compression;

import propra.PropraException;
import propra.imageconverter.image.tga.compression.writer.NoCompressionWriter;
import propra.imageconverter.image.tga.compression.writer.RLECompressionWriter;
import propra.imageconverter.image.tga.compression.writer.TGACompressionWriter;

public enum CompressionType {
	NO_COMPRESSION,
	RLE;

	public static CompressionType parseCommandLineArgument(String cmdLineArg) {
		switch (cmdLineArg) {
			case "uncompressed":
				return NO_COMPRESSION;
			case "rle":
				return RLE;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + cmdLineArg + " wird nicht unterstützt.");
	}

	public int getTgaPictureType() {
		switch (this) {
			case NO_COMPRESSION:
				return 2;
			case RLE:
				return 10;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " wird nicht unterstützt.");
	}

	public TGACompressionWriter getTgaCompressionWriter(
			int pictureWidth
	) {
		switch (this) {
			case NO_COMPRESSION:
				return new NoCompressionWriter();
			case RLE:
				return new RLECompressionWriter(pictureWidth);
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " wird nicht unterstützt.");

	}
}

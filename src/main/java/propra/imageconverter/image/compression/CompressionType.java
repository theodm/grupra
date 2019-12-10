package propra.imageconverter.image.compression;

import propra.PropraException;
import propra.imageconverter.image.compression.reader.huffman.HuffmanCompressionWriter;
import propra.imageconverter.image.compression.writer.CompressionWriter;
import propra.imageconverter.image.compression.writer.NoCompressionWriter;
import propra.imageconverter.image.compression.writer.RLECompressionWriter;

/**
 * Gibt die möglichen Kompressionstypen an.
 */
public enum CompressionType {
	NO_COMPRESSION,
    RLE,
    HUFFMAN;

	/**
	 * Wandelt ein übergebenes Kommandozeilenargument in
	 * den Enum um.
	 */
	public static CompressionType parseCommandLineArgument(String cmdLineArg) {
		switch (cmdLineArg) {
			case "uncompressed":
				return NO_COMPRESSION;
			case "rle":
				return RLE;
            case "huffman":
                return HUFFMAN;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + cmdLineArg + " wird nicht unterstützt.");
	}

	/**
	 * Gibt den entsprechenden CompressionWriter
	 * für den aktuellen Aufzählungswert zurück.
	 */
	public CompressionWriter getCompressionWriter() {
		switch (this) {
			case NO_COMPRESSION:
				return new NoCompressionWriter();
			case RLE:
				return new RLECompressionWriter();
            case HUFFMAN:
                return new HuffmanCompressionWriter();
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " wird nicht unterstützt.");
	}
}

package propra.imageconverter.image.compression;

import propra.PropraException;
import propra.imageconverter.image.compression.huffman.HuffmanCompressionWriter;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.image.compression.rle.RLECompressionWriter;
import propra.imageconverter.image.compression.selector.CompressionSelector;
import propra.imageconverter.image.compression.uncompressed.NoCompressionWriter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gibt die möglichen Kompressionstypen an.
 */
public enum CompressionType {
	NO_COMPRESSION,
    RLE,
	HUFFMAN,
	AUTO;

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
			case "auto":
				return AUTO;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + cmdLineArg + " wird nicht unterstützt.");
	}

	/**
	 * Gibt den entsprechenden CompressionWriter
	 * für den aktuellen Aufzählungswert zurück.
	 * <p>
	 * Ist der Kompressionstyp AUTO kann kein eindeutiger
	 * CompressionWriter ausgewählt werden, ohne das die Bilddaten
	 * analysiert werden. Dafür muss die Methode getCompressionWriterWithAuto
	 * verwendet werden.
	 */
	public CompressionWriter getCompressionWriter() {
		switch (this) {
			case NO_COMPRESSION:
				return new NoCompressionWriter();
			case RLE:
				return new RLECompressionWriter();
            case HUFFMAN:
                return new HuffmanCompressionWriter();
			case AUTO:
				throw new PropraException("Zu dem Kompressionstyp AUTO gibt es keinen allgemeingültigen CompressionWriter. "
						+ "Bitte die Methode getCompressionWriterAuto zur Auflösung verwenden.");
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " wird nicht unterstützt.");
	}

	/**
	 * Gibt den entsprechenden CompressionWriter
	 * für den aktuellen Aufzählungswert zurück.
	 * <p>
	 * Für den Kompressionstyp AUTO wird der optimale Kompressionsalgorithmus
	 * automatisch anhand der übergebenen Bilddaten und unterstützten Kompressionstypen
	 * gewählt.
	 */
	public CompressionWriter getCompressionWriterWithAuto(
			List<CompressionType> supportedCompressions,
			PixelIterator pixelIterator
	) throws IOException {
		if (this != AUTO)
			return getCompressionWriter();

		List<CompressionType> supportedCompressionsWithoutAuto
				= supportedCompressions
				.stream()
				.filter(it -> it != CompressionType.AUTO)
				.collect(Collectors.toList());

		return CompressionSelector
				.findOptimalCompression(supportedCompressionsWithoutAuto, pixelIterator)
				.getCompressionWriter();
	}
}

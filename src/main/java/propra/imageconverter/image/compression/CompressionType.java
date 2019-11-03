package propra.imageconverter.image.compression;

import propra.PropraException;
import propra.imageconverter.image.compression.writer.CompressionWriter;
import propra.imageconverter.image.compression.writer.NoCompressionWriter;
import propra.imageconverter.image.compression.writer.RLECompressionWriter;

/**
 * Gibt die möglichen Kompressionstypen an.
 */
public enum CompressionType {
	NO_COMPRESSION,
	RLE;

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
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + cmdLineArg + " wird nicht unterstützt.");
	}

	/**
	 * Gibt für den aktuellen Aufzählungswert, die
	 * interne Repräsentation (=Kompressionstyp) im Propra-Format zurück.
	 */
	public int getPropraCompressionType() {
		switch (this) {
			case NO_COMPRESSION:
				return 0;
			case RLE:
				return 1;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " (Propra) wird nicht unterstützt.");
	}

	/**
	 * Gibt für den aktuellen Aufzählungswert, die
	 * interne Repräsentation (=Kompressionstyp) im TGA-Format zurück.
	 */
	public int getTgaPictureType() {
		switch (this) {
			case NO_COMPRESSION:
				return 2;
			case RLE:
				return 10;
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " (TGA) wird nicht unterstützt.");
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
		}

		// Kann nicht vorkommen!
		throw new PropraException("Der Kompressionstyp " + this + " wird nicht unterstützt.");
	}
}

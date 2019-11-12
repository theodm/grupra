package propra.imageconverter.image.tga;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.image.compression.writer.CompressionWriter;

import java.io.IOException;

/**
 * Ermöglicht das Schreiben einer TGA-Datei.
 */
public final class TgaWriter implements ImageWriter {
	private final CompressionType compressionType;

	private TgaWriter(CompressionType compressionType) {
		this.compressionType = compressionType;
    }

	/**
	 * Erstellt einen TgaWriter mit dem bestimmten Kompressionstyp.
	 */
	public static TgaWriter create(
            CompressionType compressionType
	) {
		return new TgaWriter(compressionType);
	}

	@Override public void write(
			ImageReader imageReader,
			ReadWriteFile outputFile
	) throws IOException {
		LittleEndianOutputStream outputStream = outputFile.outputStream(0);

		CompressionWriter compressionWriter
				= compressionType.getCompressionWriter();

        outputStream.writeUByte(0); // Länge der Bild-ID
        outputStream.writeUByte(0); // Palettentyp
		outputStream.writeUByte(TGAFileFormat.compressionTypeToTGACompressionType(compressionType)); // Bildtyp, nur unterstützt 2 = RGB (24 Bit) unkomprimiert und 10 = RGB (24 Bit) RLE-komprimiert
        outputStream.writeFully(new byte[] { 0, 0, 0, 0, 0 }); // Palletenbeginn, Palettenlänge und Palettengröße immer 0, da keine Palette vorhanden
        outputStream.writeUShort(0); // X-Koordinate für Nullpunkt, siehe parse()
		outputStream.writeUShort(imageReader.getHeight()); // Y-Koordinate für Nullpunkt
		outputStream.writeUShort(imageReader.getWidth()); // Breite des Bilds
		outputStream.writeUShort(imageReader.getHeight()); // Länge des Bilds
        outputStream.writeUByte(24); // Bits pro Bildpunkt, nach Vorgabe immer 24
        outputStream.writeUByte(0b00100000); // Attribut-Byte, nach Vorgabe, siehe parse()

		PixelIterator pixelIterator
				= TGAPixelIterator.forImageReader(imageReader);

		compressionWriter.write(pixelIterator, outputStream);

		outputFile.releaseOutputStream();
    }
}

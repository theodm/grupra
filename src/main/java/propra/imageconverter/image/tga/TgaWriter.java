package propra.imageconverter.image.tga;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.image.compression.iterator.TGAPixelIterator;
import propra.imageconverter.image.compression.writer.CompressionWriter;

import java.io.IOException;

/**
 * Ermöglicht das pixelweise Schreiben einer TGA-Datei.
 * <p>
 * Der Benutzer ist angehalten, die Instanz nach dem Schreiben
 * wieder zu schließen.
 */
public final class TgaWriter implements ImageWriter {
	private final CompressionType compressionType;

	private TgaWriter(CompressionType compressionType) {
		this.compressionType = compressionType;
    }

    public static TgaWriter create(
            CompressionType compressionType
    ) throws IOException {
		return new TgaWriter(compressionType);
	}

	@Override public void write(
			ImageReader imageReader,
			ReadWriteFile outputFile
	) throws IOException {
		LittleEndianOutputStream outputStream = outputFile.outputStream(0);

		CompressionWriter compressionWriter
				= compressionType.getTgaCompressionWriter(imageReader.getWidth());

        outputStream.writeUByte(0); // Länge der Bild-ID
        outputStream.writeUByte(0); // Palettentyp
        outputStream.writeUByte(compressionType.getTgaPictureType()); // Bildtyp, nur unterstützt 2 = RGB (24 Bit) unkomprimiert und 10 = RGB (24 Bit) RLE-komprimiert
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
		outputFile.close();
    }
}

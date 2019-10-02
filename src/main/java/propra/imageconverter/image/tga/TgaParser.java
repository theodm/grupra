package propra.imageconverter.image.tga;

import propra.imageconverter.image.BinaryReader;
import propra.imageconverter.image.BinaryWriter;
import propra.imageconverter.image.Picture;

import java.io.IOException;

public class TgaParser {

	public void write(
			Picture picture,
			BinaryWriter os
	) throws IOException {
		os.writeByte(0); // Länge der Bild-ID
		os.writeByte(0); // Palettentyp
		os.writeByte(2); // Bildtyp, nur unterstützt 2 = RGB(24 oder 32 Bit) unkomprimiert
		os.writeN(new byte[] { 0, 0, 0, 0, 0 }); // Palletenbeginn, Palettenlänge und Palettengröße immer 0, da keine Palette vorhanden
		os.writeWord(picture.getxZero()); // X-Koordinate für Nullpunkt
		os.writeWord(picture.getyZero()); // Y-Koordinate für Nullpunkt
		os.writeWord(picture.getWidth()); // Breite des Bilds
		os.writeWord(picture.getHeight()); // Länge des Bilds
		os.writeByte(24); // Bits pro Bildpunkt, nach Vorgabe immer 24
		os.writeByte(0b00000011); // Attribut-Byte, nach Vorgabe, siehe parse()
		os.writeN(picture.getRawData()); // Bilddaten in Little-Endian
	}

	public Picture parse(
			BinaryReader is
	) throws IOException {
		// Länge der Bild-ID, per Aufgabenstellung 0
		int lengthOfPictureID = is.readByte();

		assert lengthOfPictureID == 0;

		// Farbpalettentyp
		// 0 = keine Farbpalette
		// 1 = Farbpalette vorhanden
		int paletteType = is.readByte();

		assert paletteType == 0;

		// Bildtyp
		// nur unterstützt: 2 = RGB (24 oder 32 Bit) unkomprimiert
		int pictureType = is.readByte();

		assert pictureType == 2;

		// Palettenbeginn, Palettenlänge und Palettengröße überspringen
		is.skip(5);

		// X-Koordinate für Nullpunkt
		// Y-Koordinate für Nullpunkt
		int xZero = is.readWord();
		int yZero = is.readWord();

		// Breite und Länge des Bilds
		int width = is.readWord();
		int height = is.readWord();

		// Bits pro Bildpunkt, nach Vorgabe immer 24
		int bitsPerPoint = is.readByte();

		assert bitsPerPoint == 24;

		int pictureAttributeByte = is.readByte();

		// Nach Vorgabe ist die Lage des Nullpunkts oben links
		// und die Anzahl der Attributbits pro Punkt 3 (für RBG bzw. GBR)
		assert pictureAttributeByte == 0b00000011;

		// Nach Vorgabe besteht keine Bild-ID und Farbpalette

		// Größe der Daten sind 3 Bytes pro Pixel,
		// wobei es width * height Pixel gibt.
		int numberOfPoints = width * height * 3;
		byte[] pictureData = new byte[numberOfPoints];
		is.readN(pictureData, numberOfPoints);

		return new Picture(
				width,
				height,
				xZero,
				yZero,
				pictureData
		);
	}
}

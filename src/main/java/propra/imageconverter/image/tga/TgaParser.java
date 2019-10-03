package propra.imageconverter.image.tga;

import propra.imageconverter.image.BinaryReader;
import propra.imageconverter.image.BinaryWriter;
import propra.imageconverter.image.ImageParser;
import propra.imageconverter.image.Picture;

import java.io.IOException;

public class TgaParser implements ImageParser {

	@Override
	public void write(
			Picture picture,
			BinaryWriter os
	) throws IOException {
		os.writeByte(0); // Länge der Bild-ID
		os.writeByte(0); // Palettentyp
		os.writeByte(2); // Bildtyp, nur unterstützt 2 = RGB(24 oder 32 Bit) unkomprimiert
		os.writeN(new byte[] { 0, 0, 0, 0, 0 }); // Palletenbeginn, Palettenlänge und Palettengröße immer 0, da keine Palette vorhanden
		os.writeWord(0); // X-Koordinate für Nullpunkt, siehe parse()
		os.writeWord(picture.getHeight()); // Y-Koordinate für Nullpunkt
		os.writeWord(picture.getWidth()); // Breite des Bilds
		os.writeWord(picture.getHeight()); // Länge des Bilds
		os.writeByte(24); // Bits pro Bildpunkt, nach Vorgabe immer 24
		os.writeByte(0b00100000); // Attribut-Byte, nach Vorgabe, siehe parse()
		os.writeN(picture.getRawData()); // Bilddaten in Little-Endian
	}

	@Override
	public Picture parse(
			BinaryReader is
	) throws IOException {
		// Länge der Bild-ID, per Aufgabenstellung 0
		int lengthOfPictureID = is.readByte();
		require(lengthOfPictureID == 0, "Die Bild-ID des TGA-Formats wird vom Konverter nicht unterstützt.");

		// Farbpalettentyp
		// 0 = keine Farbpalette
		// 1 = Farbpalette vorhanden
		int paletteType = is.readByte();
		require(paletteType == 0, "Eine Palette wird im TGA-Format nicht unterstützt.");

		// Bildtyp
		// nur unterstützt: 2 = RGB (24 oder 32 Bit) unkomprimiert
		int pictureType = is.readByte();
		require(pictureType == 2, "Es wird im TGA-Format nur der Bildtyp 2 unterstützt. Angegeben wurde Bildtyp " + pictureType + ".");

		// Palettenbeginn, Palettenlänge und Palettengröße überspringen
		is.skip(5);

		// X-Koordinate für Nullpunkt
		// Y-Koordinate für Nullpunkt
		int xZero = is.readWord();
		int yZero = is.readWord();

		// Breite und Länge des Bilds
		int width = is.readWord();
		int height = is.readWord();

		// xZero und yZero geben immer die untere linke
		// Bildecke an, daher sind das die einzigen
		// erlaubten Werte.
		// Die TGA-Specs sind hier genauer: http://tfc.duke.free.fr/coding/tga_specs.pdf
		require(xZero == 0, "Es wird nur eine X-Koordinate für den Nullpunkt nur 0 unterstützt. Angegeben wurden " + xZero + ".");
		require(yZero == height, "Es wird nur eine Y-Koordinate für den Nullpunkt nur die Bildhöhe unterstützt." + yZero + ".");

		// Höhe und Breite dürfen nicht 0 sein.
		require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
		require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

		// Bits pro Bildpunkt, nach Vorgabe immer 24
		int bitsPerPoint = is.readByte();
		require(bitsPerPoint == 24, "Es werden pro Bildpunkt im TGA-Format nur exakt 24 bits unterstützt. Angegeben wurde " + bitsPerPoint + ".");

		// Nach Vorgabe ist die Lage des Nullpunkts oben links
		// und die Anzahl der Attributbits pro Punkt 3 (für RBG bzw. GBR)
		int pictureAttributeByte = is.readByte();
		require(pictureAttributeByte == 0b00100000, "Ein Bild im TGA-Format wird nur unterstützt, falls das Bildattributsbyte 0b00100000 ist. Das bedeutet die Lage des Nullpunkts ist oben links.");

		// Nach Vorgabe besteht keine Bild-ID und Farbpalette

		// Größe der Daten sind 3 Bytes pro Pixel,
		// wobei es width * height Pixel gibt.
		int numberOfPoints = width * height * 3;
		byte[] pictureData = new byte[numberOfPoints];
		is.readN(pictureData, numberOfPoints);


		return new Picture(
				width,
				height,
				pictureData
		);
	}
}

package propra.imageconverter.image.tga;

import propra.imageconverter.image.BinaryReader;

import java.io.IOException;

public class TgaParser {

	public void parse(
			BinaryReader is
	) throws IOException {
		// Länge der Bild-ID, bei Wert 0 entfällt die Bild-ID
		int lengthOfPictureID = is.readByte();

		// Farbpalettentyp
		// 0 = keine Farbpalette
		// 1 = Farbpalette vorhanden
		int paletteType = is.readByte();

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







	}
}

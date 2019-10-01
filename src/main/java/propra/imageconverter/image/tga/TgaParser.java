package propra.imageconverter.image.tga;

import java.io.IOException;
import java.io.InputStream;

public class TgaParser {

	void parse(
			InputStream is
	) throws IOException {
		// Länge der Bild-ID, bei Wert 0 entfällt die Bild-ID
		int lengthOfPictureID = is.read();

		// Farbpalettentyp
		// 0 = keine Farbpalette
		// 1 = Farbpalette vorhanden
		int paletteType = is.read();

		// Bildtyp
		// nur unterstützt: 2 = RGB (24 oder 32 Bit) unkomprimiert
		int pictureType = is.read();

		assert pictureType == 2;

		byte[] pictureId = new byte[lengthOfPictureID];
		int readBytes = is.read(pictureId, 0, lengthOfPictureID);

		assert readBytes == lengthOfPictureID;

	}
}

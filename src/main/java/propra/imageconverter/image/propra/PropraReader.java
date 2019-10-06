package propra.imageconverter.image.propra;

import propra.PropraException;
import propra.imageconverter.binary.BinaryReader;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static propra.imageconverter.image.propra.PropraFileFormat.MAGIC_HEADER;

/**
 * Der PropraReader ermöglicht das pixelweise Einlesen einer
 * Propra-Datei.
 * <p>
 * Der Benutzer ist angehalten die Instanz der Klasse nach
 * der Nutzung wieder zu schließen.
 */
public class PropraReader implements ImageReader {
	private final BinaryReader binaryInput;
	private final int width;
	private final int height;
	private final BigInteger lengthOfContent;

	/**
	 * Die aktuelle Position des Pixel-Zeigers.
	 */
	private BigInteger currentPosInContent = BigInteger.ZERO;

	private PropraReader(
			BinaryReader binaryInput,
			int width,
			int height,
			BigInteger lengthOfContent
	) {
		this.binaryInput = binaryInput;
		this.width = width;
		this.height = height;
		this.lengthOfContent = lengthOfContent;
	}

	/**
	 * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
	 */
	private static void require(boolean condition, String message) {
		if (!condition)
			throw new PropraException(message);
	}

	/**
	 * Hiermit erstellen wir eine Instanz des PropraReaders. Danach können wir
	 * die Bilddaten pixelweise ablesen.
	 */
	public static PropraReader create(
			BinaryReader binaryInput
	) throws IOException {
		// Formatkennung
		byte[] magicHeader = new byte[MAGIC_HEADER.length];
		binaryInput.readFully(magicHeader);
		require(Arrays.equals(magicHeader, MAGIC_HEADER), "Der Header der Propra-Datei ist nicht wohlgeformt. Der Beginn muss " + new String(MAGIC_HEADER) + " sein.");

		// Breite und Höhe
		int width = binaryInput.readUShort();
		int height = binaryInput.readUShort();

		// Höhe und Breite dürfen nicht 0 sein.
		require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
		require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

		// Bits pro Bildpunkt
		int bitsPerPoint = binaryInput.readUByte();
		require(bitsPerPoint == 24, "Es werden nur 24 bits pro Pixel für Propa-Dateien unterstützt. Angegeben wurden " + bitsPerPoint + " bit.");

		// Kompressionstyp
		int compressionType = binaryInput.readUByte();
		require(compressionType == 0, "Es wird für Propa-Dateien nur der Kompressionstyp 0 unterstützt. Angeben wurde der Kompressionstyp " + compressionType + ".");

		// Länge der Bilddaten
		BigInteger lengthOfContent = binaryInput.readULong();
		BigInteger lengthOfContentPerWidthAndHeight = BigInteger.ONE
				.multiply(BigInteger.valueOf(width))
				.multiply(BigInteger.valueOf(height))
				.multiply(BigInteger.valueOf(3));

		require(lengthOfContentPerWidthAndHeight.compareTo(lengthOfContent) == 0, "Die Länge der Daten muss 3 * Bildbreite * Bildhöhe entsprchen.");

		// Prüfsumme
		long checksum = binaryInput.readUInt();

		// Hier lesen wir die kompletten Daten ein, um die Prüfsumme
		// berechnen zu können. Danach (siehe unten) setzen wir den Datei-Cursor
		// zurück, um für den Benutzer der Klasse, die Daten erneut lesbar zu machen.
		//
		// Das ist zwar für die Geschwindigkeit ineffizient, aber hier ist die
		// Verständlichkeit des Codes und die Arbeitsspeichereffizienz wichtiger als die
		// Geschwindigkeit des Codes.
		long calculatedChecksum = Checksum.calcStreamingChecksum(lengthOfContent, binaryInput::readUByte);

		require(checksum == calculatedChecksum, "Die berechnete Prüfsumme der Daten stimmt nicht mit der in der Datei gespeicherten Prüfsumme überein.");
		require(binaryInput.isAtEndOfFile(), "Die tatsächlichen Bilddaten sind länger, als im Header angegeben.");

		// Wie oben beschrieben setzen wir den File-Cursor
		// auf das Datensegment um dem Benutzer der Klasse
		// das Lesen der Bilddaten zu ermöglichen
		binaryInput.seek(PropraFileFormat.OFFSET_DATA);

		return new PropraReader(binaryInput, width, height, lengthOfContent);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public byte[] readNextPixel() throws IOException {
		byte[] nextPixel = new byte[3];

		binaryInput.readFully(nextPixel);

		// Das Propra-Format speichert die Pixel im GBR-Format
		// wir wollen unseren Benutzern aber die Daten komfortabel
		// im RGB-Format zurückgeben.
		ArrayUtils.swap(nextPixel, 0, 2);
		ArrayUtils.swap(nextPixel, 1, 2);

		currentPosInContent = currentPosInContent.add(BigInteger.valueOf(3));

		return nextPixel;
	}

	public boolean hasNextPixel() {
		return currentPosInContent.compareTo(lengthOfContent) < 0;
	}

	@Override
	public void close() throws Exception {
		binaryInput.close();
	}
}

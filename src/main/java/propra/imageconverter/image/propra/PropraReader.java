package propra.imageconverter.image.propra;

import propra.PropraException;
import propra.imageconverter.image.BinaryReader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static propra.imageconverter.image.propra.Checksum.streamDataAndCalculateChecksum;

public class PropraReader implements AutoCloseable {
	private final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();
	private final BinaryReader is;
	private final int width;
	private final int height;
	// Prüfsumme um nach dem Lesen aller Daten auf
	// Übereinstimmung zu überprüfen.
	private final long checksum;
	private final BigInteger lengthOfContent;

	private PropraReader(BinaryReader is) throws IOException {
		this.is = is;
		// Formatkennung
		byte[] magicHeader = new byte[MAGIC_HEADER.length];
		is.readN(magicHeader, MAGIC_HEADER.length);
		require(Arrays.equals(magicHeader, MAGIC_HEADER), "Der Header der Propra-Datei ist nicht wohlgeformt. Der Beginn muss " + new String(MAGIC_HEADER) + " sein.");

		// Breite und Höhe
		int width = is.readWord();
		int height = is.readWord();

		// Höhe und Breite dürfen nicht 0 sein.
		require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
		require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

		// Bits pro Bildpunkt
		int bitsPerPoint = is.readByte();
		require(bitsPerPoint == 24, "Es werden nur 24 bits pro Pixel für Propa-Dateien unterstützt. Angegeben wurden " + bitsPerPoint + " bit.");

		// Kompressionstyp
		int compressionType = is.readByte();
		require(compressionType == 0, "Es wird für Propa-Dateien nur der Kompressionstyp 0 unterstützt. Angeben wurde der Kompressionstyp " + compressionType + ".");

		// Länge der Bilddaten
		BigInteger lengthOfContent = is.readQWord();
		BigInteger lengthOfContentPerWidthAndHeight = BigInteger.ONE
				.multiply(BigInteger.valueOf(width))
				.multiply(BigInteger.valueOf(height))
				.multiply(BigInteger.valueOf(3));

		require(lengthOfContentPerWidthAndHeight.compareTo(lengthOfContent) == 0, "Die Länge der Daten muss 3 * Bildbreite * Bildhöhe entsprchen.");

		// Prüfsumme
		long checksum = is.readDword();

		// Metadaten in der Instanz
		// abspeichern.
		this.width = width;
		this.height = height;
		this.checksum = checksum;
		this.lengthOfContent = lengthOfContent;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void streamRGB(RGBStream stream) throws IOException {
		PropraByteStreamToRGBStream propraByteStreamToRGBStream = new PropraByteStreamToRGBStream(stream);

		long generatedChecksum = streamDataAndCalculateChecksum(
				lengthOfContent,
				() -> {
					try {
						return is.readByte();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				},
				propraByteStreamToRGBStream::consumeByte
		);

		require(checksum == generatedChecksum, "Die Prüfsumme stimmt nicht mit den Daten überein.");

		int eof = is.readByte();
		require(eof == -1, "Es sind mehr Daten in der Datei vorhanden, als angegeben wurden.");
	}

	/**
	 * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
	 */
	private void require(boolean condition, String message) {
		if (!condition)
			throw new PropraException(message);
	}

	@Override
	public void close() throws Exception {
		is.close();
	}

	private static class PropraByteStreamToRGBStream {
		private final RGBStream stream;
		private int[] currentGBR = new int[3];
		private Integer currentIndex = 0;

		private PropraByteStreamToRGBStream(RGBStream stream) {
			this.stream = stream;
		}

		public void consumeByte(int currentByteValue) {
			currentGBR[currentIndex] = currentByteValue;
			if (currentIndex == 2) {
				// Gelesen wird GBR, daher müssen wir hier vertauschen.
				int[] rgbPoint = new int[3];

				rgbPoint[0] = currentGBR[2];
				rgbPoint[1] = currentGBR[0];
				rgbPoint[2] = currentGBR[1];

				stream.emit(rgbPoint);
			}

			currentIndex = (currentIndex + 1) % 3;
		}
	}
}

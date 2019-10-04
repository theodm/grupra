package propra.imageconverter.image.propra;

import propra.PropraException;
import propra.imageconverter.image.BinaryReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Consumer;

public class PropraReader {
	private final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();
	private int width;
	private int height;
	// Prüfsumme um nach dem Lesen aller Daten auf
	// Übereinstimmung zu überprüfen.
	private long checksum;
	private BigInteger lengthOfContent;

	private PropraReader(BinaryReader is) {
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

	public PropraReader(File file) throws IOException {
		// Header-Daten einlesen
		try (BinaryReader is = new BinaryReader(new FileInputStream(file))) {

			//verifyChecksumAndLength();
		}

	}

	public static PropraReader readHeader() {

		return
	}

	public long streamDataAndCalculateChecksum(BigInteger n, BinaryReader is, Consumer<Integer> byteStream) throws IOException {
		BigInteger X = BigInteger.valueOf(65513);

		// Hier wurde die rekursive Funktion in eine iterative Funktion
		// umgewandelt, da sonst ein Stackoverflow auftritt.
		//
		// Als zweiter Schritt wird der Aufruf der Methode A integriert,
		// um zu vermeiden, dass die Summierung für jeden Schleifendurchlauf
		// erneut berechnet werden muss.
		BigInteger bResult = BigInteger.ONE;
		BigInteger lastASum = BigInteger.ZERO;
		for (BigInteger j = BigInteger.ONE; j.compareTo(n) <= 0; j = j.add(BigInteger.ONE)) {
			int byteRead = is.readByte();

			// Das einzelne gelesene Byte an den Aufrufer zurückgeben.
			byteStream.accept(byteRead);

			lastASum = lastASum.add(j.add(BigInteger.valueOf(byteRead)));
			bResult = bResult.add(lastASum.remainder(X)).remainder(X);
		}

		BigInteger aResult = lastASum.remainder(X);

		// 2 << 15 == 2^16
		return aResult.multiply(BigInteger.valueOf(2 << 15)).add(bResult).longValueExact();
	}

	public void streamRGB(RGBStream stream) {

		long checksum = streamDataAndCalculateChecksum(
				lengthOfContent,
				is,
				currentByte -> currentByte
		)

	}

	/**
	 * Helferfunktion, gibt eine Exception aus, falls [condition] nicht erfüllt ist.
	 */
	private void require(boolean condition, String message) {
		if (!condition)
			throw new PropraException(message);
	}

	static class ByteStreamToTripleStream {
		private final ThreeByteStream stream;
		private int[] currentGBR = new int[3];
		private Integer currentIndex = 0;

		public ByteStreamToTripleStream(ThreeByteStream stream) {
			this.stream = stream;
		}

		public void consumeByte(int currentByteValue) {
			currentGBR[currentIndex] = currentByteValue;
			if (currentIndex == 2) {
				stream.emit(Arrays.copyOf(currentGBR, 3));
			}

			currentIndex = (currentIndex + 1) % 3;
		}
	}

	class Metadata {

	}

}

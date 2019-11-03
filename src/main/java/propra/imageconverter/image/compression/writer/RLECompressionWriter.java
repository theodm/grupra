package propra.imageconverter.image.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.util.ArrayUtils;
import propra.imageconverter.util.DebugUtils;

import java.io.IOException;
import java.util.Arrays;

import static propra.imageconverter.util.DebugUtils.DEBUG_MODE;

/**
 * Implementiert einen CompressionWriter mit
 * RLE-Komprimierung für jede Bildzeile.
 */
public class RLECompressionWriter implements CompressionWriter {
	/**
	 * Maximale Länge, die ein Steuerbyte an folgenden Daten oder folgenden
	 * Wiederholungen anzeigen kann.
	 */
	private final int DATA_OR_REPETITIONS_MAX_LENGTH = 128;

	/**
	 * Schreibt den gesammelten Datenbuffer {@param dataBuffer} mit einem
	 * entsprechenden Datenzähler in die Ausgabe {@param outputStream}.
	 * {@param currentDataBufferIndex} gibt die Länge des DatenBuffer, der auch
	 * tatsächlich belegt ist.
	 * <p>
	 * Gibt die Anzahl der geschriebenen Bytes zurück.
	 */
	private long writeDataBuffer(
			LittleEndianOutputStream outputStream,
			byte[] dataBuffer,
			int currentDataBufferIndex
	) throws IOException {
		DebugUtils.log(() -> "Buffer (Daten) werden geschrieben. (" + currentDataBufferIndex + " Bildpunkt(e))");

		// Der Buffer wurde tatsächlich nur
		// soweit geschrieben.
		byte[] bytesToWrite = Arrays.copyOf(dataBuffer, 3 * currentDataBufferIndex);

		// Steuerbyte enthält die Länge des Datenzählers - 1
		// das 8. Bit bleibt 0, was implizit gegeben ist.
		int controlByte = currentDataBufferIndex - 1;

		// Datenzähler und Daten schreiben
		outputStream.writeUByte(controlByte);
		outputStream.writeFully(bytesToWrite);

		DebugUtils.log(() -> String.format("Written: %02X", controlByte));
		DebugUtils.log(() -> "Written: " + ArrayUtils.debugFormat(bytesToWrite));

		// Im Debug-Mode überschreiben wir
		// den Buffer für das Debuggen mit 0en,
		// damit er anschaulicher wird; benötigt wird
		// das technisch nicht, da er mit currentDataBufferIndex
		// begrenzt wird.
		if (DEBUG_MODE) {
			Arrays.fill(dataBuffer, (byte) 0);
		}

		return 1 + bytesToWrite.length;
	}

	/**
	 * Schreibt die gesammelten wiederholten Bytes entsprechend
	 * mit Steuerbyte in den zugrundeliegenden
	 * Ausgabestream {@param outputStream}.
	 * {@param repeatingPixel} gibt den Bildpunkt an, der {@param repeats}
	 * mal wiederholt wurd.
	 * <p>
	 * Gibt die Anzahl der geschriebenen Bytes zurück.
	 */
	private long writeRepeatingBuffer(
			LittleEndianOutputStream outputStream,
			byte[] repeatingPixel,
			int repeats
	) throws IOException {
		DebugUtils.log(() -> "Buffer (wiederholte Bildpunkte) werden geschrieben. (" + repeats + " x " + ArrayUtils.formatRGBPixelOrNull(repeatingPixel) + ")");

		// Steuerbyte enthält die Länge des Wiederholungszähler - 1
		// das 8. Bit ist 1, was mittels Bit-Operationen bewerkstelligt wird.
		int controlByte = (repeats - 1) | 0b1000_0000;

		outputStream.writeUByte(controlByte);
		outputStream.writeFully(repeatingPixel);

		DebugUtils.log(() -> String.format("Written: %02X", controlByte));
		DebugUtils.log(() -> "Written: " + ArrayUtils.debugFormat(repeatingPixel));

		return 1 + repeatingPixel.length;
	}

	@Override public long write(
			PixelIterator pixelData,
			LittleEndianOutputStream outputStream
	) throws IOException {
		// Wir zählen mit, wieviele Bytes wir schreiben.
		long numberOfBytesWritten = 0;
		int pictureWidth = pixelData.getWidth();

		// DataBuffer hier für Speichereffizienz, grds so nah wie möglich an Ausführung
		byte[] dataBuffer = new byte[Math.min(DATA_OR_REPETITIONS_MAX_LENGTH, pictureWidth) * 3];

		// Der lineCounter zählt ab, in welchem Zeichen
		// der aktuellen Zeile wir uns befinden; am Ende der Zeile
		// müssen wir die aktuellen Wiederholungen oder Datenbuffer schreiben.
		int lineCounter = 0;
		byte[] currentPixel = pixelData.readNextPixel();

		while (pixelData.hasNextPixel()) {
			// Das aktuelle Pixel wiederholt sich
			// im nächsten Pixel
			if (Arrays.equals(currentPixel, pixelData.peekPixel())) {
				byte[] repeatedPixel = currentPixel;
				int repeats = 1;

				while (true) {
					currentPixel = pixelData.readNextPixel();
					lineCounter++;

					// Das aktuelle Pixel befindet sich in einer
					// neuen Zeile; daher brechen wir hier ab und
					// verarbeiten diesen Pixel im nächsten Durchlauf der
					// äußeren While-Schleife
					if (lineCounter == pictureWidth) {
						lineCounter = 0;
						break;
					}

					// Die maximale Anzahl an Wiederholungen für einen Wiederholungszähler
					// wurde erreicht, das Pixel wird im nächsten Durchlauf der
					// äußeren While-Schleife bearbeitet werden
					if (repeats == DATA_OR_REPETITIONS_MAX_LENGTH)
						break;

					// Das aktuelle Pixel ist keine Wiederholung
					// mehr, daher abbrechen.
					if (!Arrays.equals(currentPixel, repeatedPixel))
						break;

					repeats++;
				}

				// Wir schreiben die wiederholten Bytes aus
				numberOfBytesWritten += writeRepeatingBuffer(outputStream, repeatedPixel, repeats);
			} else {
				// Aktuelles Pixel wiederholt sich nicht.
				int currentDataBufferIndex = 0;

				// Wir übertragen das aktuelle Pixel in unseren Buffer
				System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
				currentDataBufferIndex++;

				while (true) {
					currentPixel = pixelData.readNextPixel();
					lineCounter++;

					// Das aktuelle Pixel befindet sich in einer
					// neuen Zeile; daher brechen wir hier ab und
					// verarbeiten diesen Pixel im nächsten Durchlauf der
					// äußeren While-Schleife
					if (lineCounter == pictureWidth) {
						lineCounter = 0;
						break;
					}

					// Die maximale Anzahl an Daten für einen Datenzähler
					// wurde erreicht, das Pixel wird im nächsten Durchlauf der
					// äußeren While-Schleife bearbeitet werden
					if (currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH)
						break;

					// Das aktuelle Byte wiederholt sich, daher muss es als Wiederholung
					// im nächsten Schleifendurchlauf verarbeitet werden.
					if (Arrays.equals(currentPixel, pixelData.peekPixel()))
						break;

					System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
					currentDataBufferIndex++;
				}

				numberOfBytesWritten += writeDataBuffer(outputStream, dataBuffer, currentDataBufferIndex);
			}
		}

		return numberOfBytesWritten;
	}
}

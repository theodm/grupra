package propra.imageconverter.image.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.image.compression.iterator.PixelIterator;
import propra.imageconverter.util.ArrayUtils;
import propra.imageconverter.util.DebugUtils;

import java.io.IOException;
import java.util.Arrays;

import static propra.imageconverter.util.DebugUtils.DEBUG_MODE;

public class RLECompressionWriter implements CompressionWriter {
	private final int DATA_OR_REPETITIONS_MAX_LENGTH = 128;
	private final int pictureWidth;

	public RLECompressionWriter(
			int pictureWidth
	) {
		this.pictureWidth = pictureWidth;
	}

	private void writeDataBuffer(
			LittleEndianOutputStream outputStream,
			byte[] dataBuffer,
			int currentDataBufferIndex
	) throws IOException {
		DebugUtils.log("Buffer (Daten) werden geschrieben. (" + currentDataBufferIndex + " Bildpunkt(e))");

		byte[] bytesToWrite = Arrays.copyOf(dataBuffer, 3 * currentDataBufferIndex);

		// Steuerbyte enthält die Länge des Datenzählers - 1
		// das 8. Bit bleibt 0, was implizit gegeben ist.
		int controlByte = currentDataBufferIndex - 1;

		outputStream.writeUByte(controlByte);
		outputStream.writeFully(bytesToWrite);

		if (DEBUG_MODE) {
			Arrays.fill(dataBuffer, (byte) 0);
		}

	}

	private void writeRepeatingBuffer(
			LittleEndianOutputStream outputStream,
			byte[] repeatingPixel,
			int repeats
	) throws IOException {
		DebugUtils.log("Buffer (wiederholte Bildpunkte) werden geschrieben. (" + repeats + " x " + ArrayUtils.formatRGBPixelOrNull(repeatingPixel) + ")");

		// Steuerbyte enthält die Länge des Wiederholungszähler - 1
		// das 8. Bit ist 1, was mittels Bit-Operationen bewerkstelligt wird.
		int controlByte = (repeats - 1) | 0b1000_0000;

		outputStream.writeUByte(controlByte);
		outputStream.writeFully(repeatingPixel);

		// Wiederholungen zurücksetzen
		repeats = 0;
	}

	@Override public void write(
			PixelIterator pixelData,
			LittleEndianOutputStream outputStream
	) throws IOException {
		int lineCounter = 0;
		// DataBuffer hier für Speichereffizienz, grds so nah wie möglich an Ausführung
		byte[] dataBuffer = new byte[Math.min(DATA_OR_REPETITIONS_MAX_LENGTH, pictureWidth) * 3];

		byte[] currentPixel = pixelData.readNextPixel();
		lineCounter++;

		while (pixelData.hasNextPixel()) {
			if (Arrays.equals(currentPixel, pixelData.peekPixel())) {
				byte[] repeatedPixel = currentPixel;
				int repeats = 0;

				// Schöneres Konstrukt
				while (true) {
					if (!Arrays.equals(currentPixel, repeatedPixel))
						break;

					if (lineCounter == pictureWidth) {
						lineCounter = 0;
						break;
					}

					if (repeats == DATA_OR_REPETITIONS_MAX_LENGTH)
						break;

					repeats++;

					currentPixel = pixelData.readNextPixel();
					lineCounter++;
				}

				writeRepeatingBuffer(outputStream, repeatedPixel, repeats);
			} /*else {
				int currentDataBufferIndex = 0;

				System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
				currentDataBufferIndex++;

				while(true) {
					if (Arrays.equals(currentPixel, pixelData.peekPixel()))
						break;

					if (lineCounter == pictureWidth) {
						lineCounter = 0;
						break;
					}

					if (currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH)
						break;

					System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
					currentDataBufferIndex++;

					currentPixel = pixelData.readNextPixel();
					lineCounter++;
				}

				writeDataBuffer(outputStream, dataBuffer, currentDataBufferIndex);
			}*/
		}
	}
}

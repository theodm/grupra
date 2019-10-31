package propra.imageconverter.image.tga.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;

public class RLECompressionWriter implements TGACompressionWriter {
	private static final boolean DEBUG_MODE = true;
	private final int DATA_OR_REPETITIONS_MAX_LENGTH = 128;
	private final int pictureWidth;
	private RLE_MODE currentMode = RLE_MODE.DATA;
	private int remainingLineLength;
	private int repeats = 0;
	private byte[] lastPixel;
	private int currentDataBufferIndex = 0;
	// Buffer müssen wir nicht mit Nullen überschreiben,
	// wir überschreiben ihn einfach
	private byte[] dataBuffer;

	public RLECompressionWriter(
			int pictureWidth
	) {
		this.pictureWidth = pictureWidth;
		dataBuffer = new byte[DATA_OR_REPETITIONS_MAX_LENGTH * 3];
		this.remainingLineLength = pictureWidth;
	}

	private void writeDataBuffer(LittleEndianOutputStream outputStream) throws IOException {
		// Wir brauchen den Datenzähler nicht zu schreiben,
		// wenn der aktuelle Buffer leer ist
		if (currentDataBufferIndex == 0) {
			return;
		}

		System.out.println("Buffer (Daten) werden geschrieben. (" + currentDataBufferIndex + " Bildpunkt(e))");

		byte[] bytesToWrite = Arrays.copyOf(dataBuffer, 3 * currentDataBufferIndex);

		// Steuerbyte enthält die Länge des Datenzählers - 1
		// das 8. Bit bleibt 0, was implizit gegeben ist.
		int controlByte = currentDataBufferIndex - 1;

		outputStream.writeUByte(controlByte);
		outputStream.writeFully(bytesToWrite);

		// Datenzähler zurücksetzen
		currentDataBufferIndex = 0;
		if (DEBUG_MODE) {
			Arrays.fill(dataBuffer, (byte) 0);
		}

	}

	private void writeRepeatingBuffer(
			LittleEndianOutputStream outputStream,
			byte[] repeatingPixel
	) throws IOException {
		System.out.println("Buffer (wiederholte Bildpunkte) werden geschrieben. (" + repeats + " x " + ArrayUtils.formatRGBPixelOrNull(repeatingPixel) + ")");
		// Steuerbyte enthält die Länge des Wiederholungszähler - 1
		// das 8. Bit ist 1, was mittels Bit-Operationen bewerkstelligt wird.
		int controlByte = (repeats - 1) | 0b1000_0000;

		outputStream.writeUByte(controlByte);
		outputStream.writeFully(repeatingPixel);

		// Wiederholungen zurücksetzen
		repeats = 0;
	}

	void handle(
			LittleEndianOutputStream outputStream,
			byte[] currentPixel) throws IOException {
		boolean currentPixelEqLastPixel
				= Arrays.equals(currentPixel, lastPixel);

		System.out.println(
				"Current State: Mode = " + currentMode + ", currentPixel = " + ArrayUtils.formatRGBPixelOrNull(currentPixel) + ", lastPixel = " + ArrayUtils.formatRGBPixelOrNull(lastPixel)
						+ ", bufferIndex = " + this.currentDataBufferIndex + ", repeats = " + this.repeats
		);

		if (currentMode == RLE_MODE.REPEAT
				&& currentPixelEqLastPixel) {
			repeats++;
		}

		if (currentMode == RLE_MODE.DATA
				&& !currentPixelEqLastPixel) {
			System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
			currentDataBufferIndex++;
			repeats = 1;
		}

		if (currentMode == RLE_MODE.DATA
				&& currentPixelEqLastPixel) {
			// Sonderfall Buffer wurde wegen Größe geschrieben und dann sind die Pixel gleich
			if (currentDataBufferIndex > 0) {
				currentDataBufferIndex--;
				writeDataBuffer(outputStream);

				currentMode = RLE_MODE.REPEAT;
				repeats = 2;
			}
			else {
				currentMode = RLE_MODE.REPEAT;
				repeats = 1;
			}

		}

		if (currentMode == RLE_MODE.REPEAT &&
				!currentPixelEqLastPixel) {
			writeRepeatingBuffer(outputStream, lastPixel);

			// Optimistischerweise in den DATA-Modus?
			currentMode = RLE_MODE.DATA;
			System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
			currentDataBufferIndex++;
			repeats = 1;
		}

	}

	//	private byte[] lastPixel = null;
	//	private byte[] currentPixel = null;
	//
	//	@Override
	//	public void writeNextPixel(
	//			LittleEndianOutputStream outputStream,
	//			byte[] nextPixel
	//	) throws IOException {
	//		if (currentPixel != null) {
	//			writePixel(
	//					outputStream,
	//					remainingLineLength == pictureWidth ? null : lastPixel,
	//					currentPixel,
	//					remainingLineLength == 1 ? null : nextPixel
	//			);
	//
	//			remainingLineLength--;
	//		}
	//
	//		lastPixel = currentPixel;
	//		currentPixel = nextPixel;
	//
	//		if (remainingLineLength == 0) {
	//			remainingLineLength = pictureWidth;
	//		}
	//	}
	//
	//	// Müsste nicht sein  aber unglaublich viel einfacher
	//	private void writePixel(
	//			LittleEndianOutputStream outputStream,
	//			byte[] lastPixel,
	//			byte[] currentPixel,
	//			byte[] nextPixel
	//	) throws IOException {
	//		System.out.println(ArrayUtils.formatRGBPixelOrNull(lastPixel) + " " + ArrayUtils.formatRGBPixelOrNull(currentPixel) + " " + ArrayUtils.formatRGBPixelOrNull(nextPixel));
	//
	//		boolean currentIsLast = Arrays.equals(currentPixel, lastPixel);
	//		boolean currentIsNext = Arrays.equals(currentPixel, nextPixel);
	//
	//		if (currentIsLast) {
	//			repeats++;
	//		}
	//
	//		if (currentIsLast && !currentIsNext) {
	//			writeDataBuffer(outputStream);
	//		}
	//
	//		if (!currentIsLast && currentIsNext) {
	//			writeRepeatingBuffer(outputStream, lastPixel);
	//			repeats = 1;
	//		}
	//
	//		if (!currentIsLast && !currentIsNext) {
	//			System.arraycopy(currentPixel, 0, dataBuffer, currentDataBufferIndex * 3, 3);
	//
	//			currentDataBufferIndex++;
	//			repeats = 1;
	//		}
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//		boolean dataBufferFull = currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH;
	//		boolean repeatBufferFull = repeats == DATA_OR_REPETITIONS_MAX_LENGTH;
	//
	//		if (dataBufferFull) {
	//			writeDataBuffer(outputStream);
	//		}
	//
	//		if (repeatBufferFull) {
	//			writeRepeatingBuffer(outputStream, lastPixel);
	//		}
	//	}

	@Override
	public void writeNextPixel(
			LittleEndianOutputStream outputStream,
			byte[] rgbPixel
	) throws IOException {
		handle(outputStream, rgbPixel);

		lastPixel = Arrays.copyOf(rgbPixel, 3);
		remainingLineLength--;

		boolean dataBufferFull = currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH;
		boolean repeatBufferFull = repeats == DATA_OR_REPETITIONS_MAX_LENGTH;

		if (dataBufferFull) {
			writeDataBuffer(outputStream);
			currentMode = RLE_MODE.DATA;
		}

		if (repeatBufferFull) {
			writeRepeatingBuffer(outputStream, lastPixel);
			currentMode = RLE_MODE.DATA;
		}

		if (remainingLineLength == 0) {
			if (currentMode == RLE_MODE.DATA) {
				writeDataBuffer(outputStream);
				currentMode = RLE_MODE.DATA;
			}

			if (currentMode == RLE_MODE.REPEAT) {
				writeRepeatingBuffer(outputStream, lastPixel);
				currentMode = RLE_MODE.DATA;
			}

			remainingLineLength = pictureWidth;
			lastPixel = null;

			System.out.println("Zeile wurde zurückgesetzt.");
		}
	}

	@Override
	public void flush(LittleEndianOutputStream outputStream) throws IOException {
		if (currentMode == RLE_MODE.DATA) {
			writeDataBuffer(outputStream);
		}

		if (currentMode == RLE_MODE.REPEAT) {
			writeRepeatingBuffer(outputStream, lastPixel);
		}
	}

	static enum RLE_MODE {
		NONE,
		REPEAT,
		DATA
	}

}

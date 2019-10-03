package propra.imageconverter.image;

/**
 * Datenstruktur, mit der ein Bild im Speicher gehalten wird.
 * <p>
 * Folgendes ist zu beachten:
 * - Die Anzahl der gespeicherten Bilddaten sind durch die Nutzung eines Byte-Arrays limitiert. (auf 2^31 - 1)
 * - Aus Effizienzgründen wird auf das Erstellen eines eigenen Datentyps für einen Bildpunkt verzichtet.
 * - Die Daten werden intern im Format BGR abgespeichert.
 */
public final class Picture {
	private final int width;
	private final int height;

	private final byte[] rawData;

    public Picture(int width, int height, byte[] rawData) {
		this.width = width;
		this.height = height;
		this.rawData = rawData;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Liefert die Bilddaten im Format BGR zurück. Das interne Byte-Array wird zurückgegeben
	 * Änderungen führen dazu, dass die Bilddaten verändert werden. Aus Effizienzgründen wird keine Kopie erstellt,
	 * bei Veränderung sollte dies durch den Aufrufer geschehen.
	 */
	public byte[] getRawData() {
		return rawData;
	}
}

package propra.imageconverter.image;

public class Picture {
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

	public byte[] getRawData() {
		return rawData;
	}
}

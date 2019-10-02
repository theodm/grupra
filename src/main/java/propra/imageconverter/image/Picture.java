package propra.imageconverter.image;

public class Picture {
	private final int width;
	private final int height;

	private final int xZero;
	private final int yZero;

	private final byte[] rawData;

	public Picture(int width, int height, int xZero, int yZero, byte[] rawData) {
		this.width = width;
		this.height = height;
		this.xZero = xZero;
		this.yZero = yZero;
		this.rawData = rawData;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getxZero() {
		return xZero;
	}

	public int getyZero() {
		return yZero;
	}

	public byte[] getRawData() {
		return rawData;
	}
}

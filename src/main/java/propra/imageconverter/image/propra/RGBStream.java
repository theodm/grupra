package propra.imageconverter.image.propra;

public interface RGBStream extends ThreeByteStream {
	@Override
	void emit(int[] rgb);
}

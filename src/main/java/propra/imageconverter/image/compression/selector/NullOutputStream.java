package propra.imageconverter.image.compression.selector;

import java.io.OutputStream;

/**
 * OutputStream ohne darunterliegendes Datenziel.
 */
class NullOutputStream extends OutputStream {
    @Override
    public void write(int i) {
    }
}

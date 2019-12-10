package propra.imageconverter.image.compression.reader;

import propra.imageconverter.binary.LittleEndianInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementiert einen CompressionReader für
 * unkomprimierte Daten.
 */
public class NoCompressionReader implements CompressionReader {
    private final LittleEndianInputStream inputStream;

    public NoCompressionReader(InputStream inputStream) {
        this.inputStream = new LittleEndianInputStream(inputStream);
    }

    @Override
    public byte[] readNextPixel() throws IOException {
        // Ohne Kompression müssen wir einfach nur
        // den nächsten Bildpunkt wählen.
        byte[] pixel = new byte[3];

        inputStream.readFully(pixel);

        return pixel;
    }
}

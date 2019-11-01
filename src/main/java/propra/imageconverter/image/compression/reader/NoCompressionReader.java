package propra.imageconverter.image.compression.reader;

import propra.imageconverter.binary.LittleEndianInputStream;

import java.io.IOException;

public class NoCompressionReader implements TGACompressionReader {
    @Override
    public byte[] readNextPixel(LittleEndianInputStream inputStream) throws IOException {
        // Ohne Kompression müssen wir einfach nur
        // den nächsten Bildpunkt wählen.
        byte[] pixel = new byte[3];

        inputStream.readFully(pixel);

        // Hier keine Kopie, da wir unser Byte-Array
        // weiter nutzen.
        return pixel;
    }
}

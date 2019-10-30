package propra.imageconverter.image.tga.compression.reader;

import propra.PropraException;
import propra.imageconverter.binary.LittleEndianInputStream;

import java.io.IOException;

public interface TGACompressionReader {

    public static TGACompressionReader fromPictureType(int pictureType) {
        switch (pictureType) {
            case 2:
                return new NoCompressionReader();
            case 10:
                return new RLECompressionReader();
        }

        throw new PropraException("Der ausgewählte Picture-Typ " + pictureType + " wird nicht unterstützt.");
    }

    byte[] readNextPixel(
            LittleEndianInputStream inputStream
    ) throws IOException;
}

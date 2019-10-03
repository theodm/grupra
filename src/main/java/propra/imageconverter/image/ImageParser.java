package propra.imageconverter.image;

import propra.PropraException;

import java.io.IOException;

public interface ImageParser {
    Picture parse(BinaryReader is) throws IOException;

    void write(Picture picture, BinaryWriter os) throws IOException;

    default void require(boolean condition, String message) {
        if (!condition)
            throw new PropraException(message);
    }
}

package propra.imageconverter.image.propra;

import propra.imageconverter.image.BinaryWriter;

import java.io.IOException;
import java.math.BigInteger;

public class PropraWriter {
    private final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();

    private PropraWriter() {

    }

    public static void writePropra(BinaryWriter os, PropraReader propraReader) throws IOException {
        os.writeN(MAGIC_HEADER); // Formatkennung
        os.writeWord(propraReader.getWidth()); // Bildbreite
        os.writeWord(propraReader.getHeight()); // Bildhöhe
        os.writeByte(24); // Bits pro Bildpunkt (=24)
        os.writeByte(0); // Kompressionstyp (0=unkomprimiert)
        // Die Länge ist ein Int, das Feld wird mit QWORD-Länge also niemals ausgefüllt.
        // Daten in solcher Länge werden nicht unterstützt, das Feld muss aber trotzdem so geschrieben werden.
        BigInteger lengthOfContent = BigInteger.ONE
                .multiply(BigInteger.valueOf(propraReader.getWidth()))
                .multiply(BigInteger.valueOf(propraReader.getHeight()))
                .multiply(BigInteger.valueOf(3));

        os.writeQWord(lengthOfContent); // Länge des Datensegments in Bytes (vorzeichenlos)

        os.writeDWord(generateChecksum(pictureDataCopy, pictureDataCopy.length)); // Prüfsumme über die Bytes des Datensegments (vorzeichenlos)
        os.writeN(pictureDataCopy); // Datensegment
    }
}

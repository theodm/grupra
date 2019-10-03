package propra.imageconverter.image.propra;

import propra.imageconverter.image.BinaryReader;
import propra.imageconverter.image.BinaryWriter;
import propra.imageconverter.image.ImageParser;
import propra.imageconverter.image.Picture;
import propra.imageconverter.util.ArrayUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PropraParser implements ImageParser {
    private final static byte[] MAGIC_HEADER = "ProPraWS19".getBytes();

    private final static Map<Long, Long> modulos = new HashMap<>();

    private long B(byte[] data, long i) {
        long X = 65513;

        long currentResult = 1;
        long lastASum = 0;
        for (long j = 1; j <= i; j++) {
            lastASum += (j) + Byte.toUnsignedInt(data[(int) j - 1]);
            currentResult = (currentResult + (lastASum % X)) % X;
        }

        return currentResult;
    }

    private long A(byte[] data, long n) {
        long X = 65513;

        long sum = 0;
        for (long i = 0; i < n; i++) {
            sum += (i + 1) + Byte.toUnsignedInt(data[(int) i]);
        }

        return (sum) % X;
    }

    public long generateChecksum(byte[] data, long n) {
        return A(data, n) * (2 << 15) + B(data, n);
    }

    private void inPlaceBGRToGBR(byte[] pictureData) {
        // Das Propra-Format speichert die Pixeldaten im Format GBR,
        // intern speichern wir die Pixeldaten aber in BGR,
        // daher müssen wir hier für jedes Pixel Kompoente 1 mit Komponente 2 austauschen.
        for (int i = 0; i < pictureData.length; i += 3) {
            ArrayUtils.swap(pictureData, i, i + 1);
        }

    }

    @Override
    public void write(
            Picture picture,
            BinaryWriter os
    ) throws IOException {
        os.writeN(MAGIC_HEADER); // Formatkennung
        os.writeWord(picture.getWidth()); // Bildbreite
        os.writeWord(picture.getHeight()); // Bildhöhe
        os.writeByte(24); // Bits pro Bildpunkt (=24)
        os.writeByte(0); // Kompressionstyp (0=unkomprimiert)
        // Die Länge ist ein Int, das Feld wird mit QWORD-Länge also niemals ausgefüllt.
        // Daten in solcher Länge werden nicht unterstützt, das Feld muss aber trotzdem so geschrieben werden.
        os.writeQWord(BigInteger.valueOf(picture.getRawData().length)); // Länge des Datensegments in Bytes (vorzeichenlos)

        byte[] pictureDataCopy = Arrays.copyOf(picture.getRawData(), picture.getRawData().length);
        inPlaceBGRToGBR(pictureDataCopy);

        os.writeDWord(generateChecksum(pictureDataCopy, pictureDataCopy.length)); // Prüfsumme über die Bytes des Datensegments (vorzeichenlos)
        os.writeN(pictureDataCopy); // Datensegment
    }

    @Override
    public Picture parse(
            BinaryReader is
    ) throws IOException {
        // Formatkennung
        byte[] magicHeader = new byte[MAGIC_HEADER.length];
        is.readN(magicHeader, MAGIC_HEADER.length);
        require(Arrays.equals(magicHeader, MAGIC_HEADER), "Der Header der Propra-Datei ist nicht wohlgeformt. Der Beginn muss " + new String(MAGIC_HEADER) + " sein.");

        // Breite und Höhe
        int width = is.readWord();
        int height = is.readWord();

        // Höhe und Breite dürfen nicht 0 sein.
        require(width != 0, "Die Breite eines Bilds darf nicht 0 sein.");
        require(height != 0, "Die Höhe eines Bilds darf nicht 0 sein.");

        // Bits pro Bildpunkt
        int bitsPerPoint = is.readByte();
        require(bitsPerPoint == 24, "Es werden nur 24 bits pro Pixel für Propa-Dateien unterstützt. Angegeben wurden " + bitsPerPoint + " bit.");

        // Kompressionstyp
        int compressionType = is.readByte();
        require(compressionType == 0, "Es wird für Propa-Dateien nur der Kompressionstyp 0 unterstützt. Angeben wurde der Kompressionstyp " + compressionType + ".");

        // Länge der Bilddaten
        int lengthOfContent = is.readQWord().intValueExact();
        require(width * height * 3 == lengthOfContent, "Die Länge der Daten muss 3 * Bildbreite * Bildhöhe entsprchen.");

        // Prüfsumme
        long checksum = is.readDword();

        byte[] pictureData = new byte[lengthOfContent];
        is.readN(pictureData, lengthOfContent);

        int eof = is.readByte();
        require(eof == -1, "Es sind mehr Daten in der Datei vorhanden, als angegeben wurden.");
        // Weniger Daten als gedacht wird bereits durch is.readN erkannt.

        require(checksum == generateChecksum(pictureData, lengthOfContent), "Die Prüfsumme stimmt nicht mit den Daten überein.");

        inPlaceBGRToGBR(pictureData);

        return new Picture(
                width,
                height,
                pictureData
        );
    }
}

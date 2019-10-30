package propra.imageconverter.image.tga.compression.writer;

import propra.imageconverter.binary.LittleEndianOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class RLECompressionWriter {
    private final int DATA_OR_REPETITIONS_MAX_LENGTH = 128;

    private final int pictureWidth;

    private int remainingLineLength;

    private int repeatsOfLastPixel = 0;
    private byte[] lastPixel = null;

    private int currentDataBufferIndex = 0;
    // Buffer müssen wir nicht mit Nullen überschreiben,
    // wir überschreiben ihn einfach
    private byte[] dataBuffer;

    public RLECompressionWriter(
            int pictureWidth
    ) {
        this.pictureWidth = pictureWidth;
        dataBuffer = new byte[DATA_OR_REPETITIONS_MAX_LENGTH * 3];
        this.remainingLineLength = pictureWidth;
    }

    private void writeAndResetDataBuffer(LittleEndianOutputStream outputStream) throws IOException {
        byte[] bytesToWrite = Arrays.copyOf(dataBuffer, 3 * currentDataBufferIndex);

        // Steuerbyte enthält die Länge des Datenzählers - 1
        // das 8. Bit bleibt 0, was implizit gegeben ist.
        int controlByte = currentDataBufferIndex - 1;

        outputStream.writeUByte(controlByte);
        outputStream.writeFully(bytesToWrite);

        // Datenzähler zurücksetzen
        currentDataBufferIndex = 0;
    }

    private void writeAndResetRepeatingPixel(LittleEndianOutputStream outputStream) throws IOException {
        // Steuerbyte enthält die Länge des Wiederholungszähler - 1
        // das 8. Bit ist 1, was mittels Bit-Operationen bewerkstelligt wird.
        int controlByte = (currentDataBufferIndex - 1) | 0b1000_0000;

        outputStream.writeUByte(controlByte);
        outputStream.writeFully(this.lastPixel);


    }

    public void writeNextPixel(
            LittleEndianOutputStream outputStream,
            byte[] rgbPixel
    ) throws IOException {
        Objects.requireNonNull(rgbPixel);

        if (this.lastPixel == null) {

        }

    }

    public void writeNextPixel(
            LittleEndianOutputStream outputStream,
            byte[] currentPixel,
            byte[] nextPixel
    ) throws IOException {

        // Gibt an, ob gerade ein Pixel wiederholt wird, oder Daten in den
        // Datenbuffer geschrieben werden. (also ob nachher ein Steuerbyte mit Wiederholungszähler
        // oder mit Datenzähler geschrieben wird)
        boolean isInRepeatingMode = repeatsOfLastPixel > 1;

        if (!isInRepeatingMode) {
            boolean dataBufferFull = currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH;
            boolean lineEndReached = remainingLineLength == 0;
            boolean repeatingPixelFound = Arrays.equals(lastPixel, rgbPixel);

            if (repeatingPixelFound) {

            }

            // Der aktuelle Buffer ist voll (maximal in Steuerbyte speicherbare Länge)
            // oder die aktuelle Zeile ist zu Ende und TODO
            // der Buffer muss geschrieben werden.
            if (dataBufferFull || lineEndReached) {
                // Wir müssen bevor wir den nächsten Bildpunkt schreiben,
                // vorher unseren Buffer ausschreiben
                writeAndResetDataBuffer(outputStream);

                // Nun können wir erneut versuchen den Pixel zu schreiben.
                writeNextPixel(outputStream, rgbPixel);

                return;
            }

            // Ansonsten schreiben wir den nächsten Bildpunkt in den Buffer und merken
            // ihn uns als den letzten geschriebenen Bildpunkt
            System.arraycopy(rgbPixel, );


        }

        if (!isInRepeatingMode &&
                (currentDataBufferIndex == DATA_OR_REPETITIONS_MAX_LENGTH
                        || remainingLineLength == 0)) {
            // Wir müssen bevor wir den nächsten Bildpunkt schreiben,
            // vorher unseren Buffer ausschreiben
            writeAndResetDataBuffer(outputStream);

            // Nun können wir erneut versuchen den Pixel zu schreiben.
            writeNextPixel(outputStream, rgbPixel);

            return;
        }

        // Der aktuell wiederholte Pixel wird nicht mehr wiederholt
        // oder die aktuelle Zeile ist zu Ende und
        // muss nun geschrieben werden
        if (isInRepeatingMode
                && (!Arrays.equals(lastPixel, rgbPixel) || remainingLineLength == 0)) {
            // Wir müssen bevor wir den nächsten Bildpunkt schreiben,
            // vorher unseren wiederholten Bildpunkt schreiben
            writeAndResetRepeatingPixel(outputStream);

            // Nun können wir erneut versuchen den Pixel zu schreiben
            writeNextPixel(outputStream, rgbPixel);

            return;
        }

        // Letzter Bildpunkt ist gleich dem aktuellen Bildpunkt,
        // daher erhöhen wir nur unseren Zähler für Wiederholungen
        if (Arrays.equals(lastPixel, rgbPixel)) {
            repeatsOfLastPixel++;

            return;
        }

        // Der aktuelle Bildpunkt ist unterschiedlich zum
        // letzten Bildpunkt und wird daher unserem Daten-Buffer
        // hinzugefügt

    }
}

package propra.imageconverter;

import propra.PropraException;
import propra.imageconverter.binary.ReadWriteFile;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.image.compression.CompressionType;
import propra.imageconverter.image.propra.PropraReader;
import propra.imageconverter.image.propra.PropraWriter;
import propra.imageconverter.image.tga.TgaReader;
import propra.imageconverter.image.tga.TgaWriter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static propra.imageconverter.util.PathUtils.calcFileExtension;

final class Converter {
    private Converter() {

    }

    /**
     * Erstellt einen ImageReader für das Format, welches anhand der
     * Dateiendung des übergebenen Pfads erkannt wurde.
     */
    private static ImageReader createImageReaderForFileName(Path path, ReadWriteFile readWriteFile)
            throws IOException {
        String extension = calcFileExtension(path.getFileName().toString());

        switch (extension) {
            case "tga":
                return TgaReader.create(readWriteFile);
            case "propra":
                return PropraReader.create(readWriteFile);
        }

        throw new PropraException("Das Format mit der Dateiendung " + extension + " wird nicht unterstützt.");
    }

    /**
     * Erstellt einen ImageWriter für das Format, welches anhand der
     * Dateiendung des übergebenen Pfads erkannt wurde. Der Kompressionstyp
     * muss übergeben werden.
     */
    private static ImageWriter createImageWriterForFileName(
            Path path,
            CompressionType compressionType
    ) {
        String extension = calcFileExtension(path.getFileName().toString());

        switch (extension) {
            case "tga":
                return new TgaWriter(compressionType);
            case "propra":
                return new PropraWriter(compressionType);
        }

        throw new PropraException("Das Format mit der Dateiendung " + extension + " wird nicht unterstützt.");
    }

    /**
     * Kovertiert die Eingabebilddatei {@param inputFilePath} in die Ausgabebilddatei {@param outputFilePath};
     * Konvertierungsquelle und -ziel werden anhand der Dateiendung bestimmt. Der Kompressionstyp muss übergeben
     * werden.
     */
    public static void convert(
            Path inputFilePath,
            Path outputFilePath,
            CompressionType compression
    ) throws Exception {
        // Öffnet die Eingabedatei zum Lesen.
        // Wird implizit durch das Schließen des ImageReader geschlossen.
        ReadWriteFile inputReadWriteFile =
                ReadWriteFile.createReadWriteFile(
                        new RandomAccessFile(
                                inputFilePath.toFile(), "r"
                        )
                );

        try (ImageReader imageReader = createImageReaderForFileName(
                inputFilePath,
                inputReadWriteFile
        )) {
            // Öffnet die Ausgabedatei zum Lesen und zum Schreiben
            // Wird implizit durch das Schließen des ImageWriter geschlossen.
            ReadWriteFile outputReadWriteFile = ReadWriteFile.overwriteReadWriteFile(
                    new RandomAccessFile(
                            outputFilePath.toFile(), "rw"
                    )
            );

            try (outputReadWriteFile) {
                ImageWriter imageWriter = createImageWriterForFileName(
                        outputFilePath,
                        compression
                );

                imageWriter.write(imageReader, outputReadWriteFile);
            }
        }
    }
}

package propra.imageconverter;

import propra.PropraException;
import propra.imageconverter.binary.BinaryReadWriter;
import propra.imageconverter.binary.BinaryReader;
import propra.imageconverter.cmd.CommandLineParser;
import propra.imageconverter.image.ImageReader;
import propra.imageconverter.image.ImageWriter;
import propra.imageconverter.image.propra.PropraReader;
import propra.imageconverter.image.propra.PropraWriter;
import propra.imageconverter.image.tga.TgaReader;
import propra.imageconverter.image.tga.TgaWriter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class ImageConverter {
	private ImageConverter() {

	}

	/**
	 * Gibt zu einem Dateinamen ohne Pfad die
	 * Dateiendung zurück.
	 */
	private static String calcFileExtension(String fileName) {
		String[] fileNameParts = fileName.split("\\.");

		assert fileNameParts.length == 2;

		return fileNameParts[1].toLowerCase();
	}

	/**
	 * Erstellt einen ImageReader für das Format, welches anhand der
	 * Dateiendung des übergebenen Pfads erkannt wurde.
	 */
	private static ImageReader createImageReaderForFileName(Path path, BinaryReader binaryReader) throws IOException {
		String extension = calcFileExtension(path.getFileName().toString());

		switch (extension) {
			case "tga":
				return TgaReader.create(binaryReader);
			case "propra":
				return PropraReader.create(binaryReader);
		}

		throw new PropraException("Das Format mit der Dateiendung " + extension + " wird nicht unterstützt.");
	}

	/**
	 * Erstellt einen ImageWriter für das Format, welches anhand der
	 * Dateiendung des übergebenen Pfads erkannt wurde.
	 */
	private static ImageWriter createImageWriterForFileName(Path path, BinaryReadWriter binaryReadWriter, int width, int height) throws IOException {
		String extension = calcFileExtension(path.getFileName().toString());

		switch (extension) {
			case "tga":
				return TgaWriter.create(binaryReadWriter, width, height);
			case "propra":
				return PropraWriter.create(binaryReadWriter, width, height);
		}

		throw new PropraException("Das Format mit der Dateiendung " + extension + " wird nicht unterstützt.");
	}

	/**
	 * Einstiegspunkt für das Programm entsprechend der Vorgaben.
	 *
	 * @param args Die Argumente für die Kommandozeile. Entsprechend der Anforderungen gibt
	 *             es die folgenden Parameter.
	 *
	 *             --input Eingabepfad für das zu konvertierende Bild im TGA-Format.
	 *             --output Ausgabepfad für das konvertierte Bild im ProPra-Format.
	 *
	 *             Beispiel: --input=./src/main/resources/KE1_TestBilder/test_01_uncompressed.tga --output=test.tga
	 */
	public static void main(
			String[] args
	) {
		// Anmerkungen an den oder die Korrektor/in
		//
		// Diese Implementierung des Bildkonvertierers hat auch das Ziel große Bilddateien
		// zu unterstützen. Siehe dazu auch die Diskussion im Moodle-Diskussionsforum unter
		// https://moodle-wrm.fernuni-hagen.de/mod/forum/discuss.php?d=23707.
		//
		// Die Bilddaten werden deshalb niemals im Arbeitsspeicher gehalten und innerhalb der
		// Lese- und Schreibroutinen ist es notwendig, innerhalb der Datei den Lesecursor vor- und zurückzubewegen.
		// Die Dateien können also nicht mehr in einem Durchlauf geschrieben und gelesen werden. Das macht den
		// Code gegenüber einer In-Memory-Implementierung deutlich komplexer.
		//
		// Die Daten werden pixelweise übertragen. Das ist ohne ein zwischengelagertes Buffering langsam. Hier
		// könnte in einem späteren Schritt optimiert werden. (bisher aber: YAGNI)
		//
		// Auch werden die Daten redundant mehrfach gelesen und die Prüfsumme redundant mehrfach berechnet. Dies
		// ist nicht geschwindigkeitseffizient, macht den Code aber deutlich einfacher und lesbarer. Das Programm
		// ist im Wesentlichen also auf Speichereffizienz und Verständlichkeit des Programmcodes optimiert.
		//
		// Ich bitte darum, dies zu berücksichtigen.
		try {
			Map<String, String> parsedArgs
					= CommandLineParser.parse(args);

			String inputFilePath = parsedArgs.get("input");
			String outputFilePath = parsedArgs.get("output");

			if (inputFilePath == null
					|| outputFilePath == null) {
				throw new PropraException("Es wurden kein Eingabepfad (--input) oder kein Ausgabepfad (--output) angegeben. Beide sind erfoderlich.");
			}

			// Öffnet die Eingabedatei zum Lesen.
			// Wird implizit durch das Schließen des ImageReader geschlossen.
			BinaryReader binaryReader =
					new BinaryReader(
							new RandomAccessFile(
									Paths.get(inputFilePath).toFile(), "r")
					);

			try (ImageReader imageReader = createImageReaderForFileName(Paths.get(inputFilePath), binaryReader)) {
				// Öffnet die Ausgabedatei zum Lesen und zum Schreiben
				// Wird implizit durch das Schließen des ImageWriter geschlossen.
				BinaryReadWriter binaryReadWriter = new BinaryReadWriter(
						new RandomAccessFile(
								Paths.get(outputFilePath).toFile(), "rw")
				);

				// Erstellt einen ImageWriter mit den Dimensionen der
				// Eingabedatei.
				try (ImageWriter imageWriter = createImageWriterForFileName(Paths.get(outputFilePath), binaryReadWriter, imageReader.getWidth(), imageReader.getHeight())) {
					while (imageReader.hasNextPixel()) {
						// Pixelweise werden die Bilddaten von einer Datei
						// in die andere kopiert.
						byte[] rgbPixel = imageReader.readNextPixel();

						imageWriter.writeNextPixel(rgbPixel);
					}
				}

			}

		} catch (Exception exception) {
			// Wir fangen hier einfacher erstmal alle Exceptions
			// damit sind auch alle Runtime-Exceptions
			// und insbesondere die PropraExceptions berücksichtigt.

			exception.printStackTrace();
			System.out.println("Es ist eine " + exception.getClass().getSimpleName() + " aufgetreten.");
			System.out.println("Folgende Nachricht enthält die Exception: " + (exception.getMessage() != null ? exception.getMessage() : "[Keine Nachricht]"));

			System.exit(123);
		}

		System.exit(0);
	}
}

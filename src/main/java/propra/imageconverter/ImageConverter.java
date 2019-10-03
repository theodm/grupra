package propra.imageconverter;

import propra.PropraException;
import propra.imageconverter.cmd.CommandLineParser;
import propra.imageconverter.image.BinaryReader;
import propra.imageconverter.image.BinaryWriter;
import propra.imageconverter.image.ImageParser;
import propra.imageconverter.image.Picture;
import propra.imageconverter.image.propra.PropraParser;
import propra.imageconverter.image.tga.TgaParser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		return fileNameParts[1];
	}

	/**
	 * Gibt eine Instanz von ImageParser für die ausgewählte Datei zurück.
	 * Der Typ der Datei wird anhand der Dateiendung bestimmt.
	 */
	private static ImageParser getImageParserForFileName(Path path) {
		// Konfiguration: Welche Datei wird mit welchem Parser bearbeitet-
		Map<String, ImageParser> fileExtensionToParserMap = Stream
				.of(
						new AbstractMap.SimpleEntry<>("tga", new TgaParser()),
						new AbstractMap.SimpleEntry<>("propra", new PropraParser())
				)
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		String extension = calcFileExtension(path.getFileName().toString());

		if (!fileExtensionToParserMap.containsKey(extension)) {
			throw new PropraException("Das Format mit der Dateiendung " + extension + " wird nicht unterstützt.");
		}

		return fileExtensionToParserMap.get(extension);
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
		try {
			Map<String, String> parsedArgs
					= CommandLineParser.parse(args);

			String inputFilePath = parsedArgs.get("input");
			String outputFilePath = parsedArgs.get("output");

			if (inputFilePath == null
					|| outputFilePath == null) {
				throw new PropraException("Es wurden kein Eingabepfad (--input) oder kein Ausgabepfad (--output) angegeben. Beide sind erfoderlich.");
			}

			ImageParser inputParser = getImageParserForFileName(Paths.get(inputFilePath));
			ImageParser outputParser = getImageParserForFileName(Paths.get(outputFilePath));

			Picture picture = null;
			try (BinaryReader fs = new BinaryReader(new FileInputStream(inputFilePath))) {
				picture = inputParser.parse(fs);
			}

			try (BinaryWriter os = new BinaryWriter(new FileOutputStream(outputFilePath))) {
				outputParser.write(picture, os);
			}

		} catch (Exception exception) {
			// Wir fangen hier einfach alle Exceptions
			// damit sind auch alle Runtime-Exceptions
			// und insbesondere die PropraExceptions berücksichtigt.
			System.out.println("Die Konvertierung ist fehlgeschlagen: " + exception.getMessage());

            System.exit(123);
        }

        System.exit(0);
	}
}

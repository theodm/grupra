package propra.imageconverter;

import propra.imageconverter.cmd.CommandLineParser;
import propra.imageconverter.image.BinaryReader;
import propra.imageconverter.image.tga.TgaParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class ImageConverter {
	/**
	 * Einstiegspunkt für das Programm entsprechend der Vorgaben.
	 *
	 * @param args Die Argumente für die Kommandozeile. Entsprechend der Anforderungen gibt
	 *             es die folgenden Parameter.
	 *             <p>
	 *             --input Eingabepfad für das zu konvertierende Bild im TGA-Format.
	 *             --output Ausgabepfad für das konvertierte Bild im ProPra-Format.
	 *             <p>
	 *             Beispiel: TODO
	 */
	public static void main(
			String[] args
	) throws IOException {
		Map<String, String> parsedArgs
				= CommandLineParser.parse(args);

		String inputFilePath = parsedArgs.get("input");
		String outputFilePath = parsedArgs.get("output");

		if (inputFilePath == null
				|| outputFilePath == null) {
			throw new RuntimeException("Es wurden kein Eingabepfad (--input) oder kein Ausgabepfad (--output) angegeben. Beide sind erfoderlich.");
		}

		Paths.get(inputFilePath);

		TgaParser tgaParser = new TgaParser();

		tgaParser.parse(new BinaryReader(new FileInputStream(inputFilePath)));
	}
}

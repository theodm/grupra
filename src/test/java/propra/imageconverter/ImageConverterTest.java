package propra.imageconverter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ImageConverterTest {
    static List<TestCase> errorTestCases() {
        return List.of(
                new TestCase("[Fehlerfall wird erkannt] Datensegment ist zu kurz (Propra)", "propra_daten_zu_kurz.propra", "propra_daten_zu_kurz.tga"),
                new TestCase("[Fehlerfall wird erkannt] Datensegment ist zu lang (Propra)", "propra_daten_zu_lang.propra", "propra_daten_zu_lang.tga"),
                new TestCase("[Fehlerfall wird erkannt] Breite ist 0 (Propra)", "propra_0breite.propra", "propra_0breite.tga"),
                new TestCase("[Fehlerfall wird erkannt] Höhe ist 0 (Propra)", "propra_0hoehe.propra", "propra_0hoehe.tga"),
                new TestCase("[Fehlerfall wird erkannt] Nicht unterstützte Anzahl von Bits pro Bildpunkt (Propra)", "propra_wrong_bit.propra", "propra_wrong_bit.tga"),
                new TestCase("[Fehlerfall wird erkannt] Prüfsumme ist falsch (Propra)", "propra_wrong_checksum.propra", "propra_wrong_checksum.tga"),
                new TestCase("[Fehlerfall wird erkannt] Kompression wird nicht unterstützt (Propra)", "propra_wrong_compression.propra", "propra_wrong_compression.tga"),

                new TestCase("[Fehlerfall wird erkannt] Breite ist 0 (TGA)", "tga_0breite.tga", "tga_0breite.propra"),
                new TestCase("[Fehlerfall wird erkannt] Höhe ist 0 (TGA)", "tga_0hoehe.tga", "tga_0hoehe.propra"),
                new TestCase("[Fehlerfall wird erkannt] Datensegment ist zu kurz (TGA)", "tga_daten_zu_kurz.tga", "tga_daten_zu_kurz.propra"),
                new TestCase("[Fehlerfall wird erkannt] Der angegebene Bildtyp wird nicht unterstützt. (TGA)", "tga_falscher_bildtyp.tga", "tga_falscher_bildtyp.propra"),
                new TestCase("[Fehlerfall wird erkannt] Nicht unterstützte Anzahl von Bits pro Bildpunkt (TGA)", "tga_falsche_bits.tga", "tga_falsche_bits.propra"),
                new TestCase("[Fehlerfall wird erkannt] Attributbyte wird nicht unterstützt (Propra)", "tga_falsches_attributbyte.tga", "tga_falsches_attributbyte.propra")
        );
    }

    static List<TestCase> successfulTestCases() {
        return List.of(
                new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: mit 1 x 1 Pixel", "tga_1pixel.tga", "tga_1pixel.propra"),
                //       new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: mit Metadaten", "tga_with_meta.tga", "tga_with_meta.propra"),
                new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: vorgegebener Testfall 1", "test_01_uncompressed.tga", "test_01_uncompressed.propra"),
                new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: vorgegebener Testfall 2", "test_02_uncompressed.tga", "test_02_uncompressed.propra"),
                new TestCase("[Erfolgsfall] Propra -> TGA -> Propra: vorgegebener Testfall 3", "test_03_uncompressed.propra", "test_03_uncompressed.tga"),
                new TestCase("[Erfolgsfall] Propra -> TGA -> Propra: vorgegebener Testfall 4", "test_04_uncompressed.propra", "test_04_uncompressed.tga"),
                new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: besonders große Datei (> 200 MB)", "tga_grosse_datei.tga", "tga_grosse_datei.propra")
        );
    }

    /**
     * Zwei Dateien auf Gleichheit überprüfen.
     */
    private void checkEqualFiles(String pathA, String pathB) throws IOException {
        byte[] bytesA = Files.readAllBytes(Paths.get(pathA));
        byte[] bytesB = Files.readAllBytes(Paths.get(pathB));

        assertArrayEquals(bytesA, bytesB);
    }


    @ParameterizedTest
    @MethodSource("successfulTestCases")
    @DisplayName("Erfolgsfälle werden erfolgreich konvertiert und entsprechen später wieder der Eingabe.")
    public void allTests(TestCase testCase) throws Exception {
        String fileNameInput = testCase.inputFile;
        String fileNameOutput = testCase.outputFile;

        ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + fileNameInput, "--output=" + fileNameOutput});
        ImageConverter.startWithArgs(new String[]{"--input=" + fileNameOutput, "--output=" + fileNameInput});
        checkEqualFiles("./src/main/resources/KE1_TestBilder/" + fileNameInput, fileNameInput);

        ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + fileNameInput, "--output=bbb_" + fileNameInput});
        checkEqualFiles("./src/main/resources/KE1_TestBilder/" + fileNameInput, "./bbb_" + fileNameInput);
    }


    @ParameterizedTest
    @MethodSource("errorTestCases")
    @DisplayName("Fehlerfälle werden erfolgreich erkannt.")
    void testErrorFiles(TestCase testCase) throws IOException {
        try {
            ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + testCase.inputFile, "--output=" + testCase.outputFile});
        } catch (Exception e) {
            System.out.println("ERROR: " + testCase.inputFile + "\n");
            e.printStackTrace();
            System.out.println("\n" + e.getMessage() + "\n");
            return;
        }

        fail();
    }

    static class TestCase {
        final String displayName;
        final String inputFile;
        final String outputFile;

        TestCase(String displayName, String inputFile, String outputFile) {
            this.displayName = displayName;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
        }

        @Override
        public String toString() {
            return "TestCase{" +
                    "displayName='" + displayName + '\'' +
                    ", inputFile='" + inputFile + '\'' +
                    ", outputFile='" + outputFile + '\'' +
                    '}';
        }
    }

}
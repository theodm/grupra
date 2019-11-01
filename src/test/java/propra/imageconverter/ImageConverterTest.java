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
                new TestCase("[KE1] [Fehlerfall wird erkannt] Datensegment ist zu kurz (Propra)", "KE1_TestBilder", "propra_daten_zu_kurz.propra", "propra_daten_zu_kurz.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Datensegment ist zu lang (Propra)", "KE1_TestBilder", "propra_daten_zu_lang.propra", "propra_daten_zu_lang.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Breite ist 0 (Propra)", "KE1_TestBilder", "propra_0breite.propra", "propra_0breite.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Höhe ist 0 (Propra)", "KE1_TestBilder", "propra_0hoehe.propra", "propra_0hoehe.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Nicht unterstützte Anzahl von Bits pro Bildpunkt (Propra)", "KE1_TestBilder", "propra_wrong_bit.propra", "propra_wrong_bit.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Prüfsumme ist falsch (Propra)", "KE1_TestBilder", "propra_wrong_checksum.propra", "propra_wrong_checksum.tga"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Kompression wird nicht unterstützt (Propra)", "KE1_TestBilder", "propra_wrong_compression.propra", "propra_wrong_compression.tga"),

                new TestCase("[KE1] [Fehlerfall wird erkannt] Breite ist 0 (TGA)", "KE1_TestBilder", "tga_0breite.tga", "tga_0breite.propra"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Höhe ist 0 (TGA)", "KE1_TestBilder", "tga_0hoehe.tga", "tga_0hoehe.propra"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Datensegment ist zu kurz (TGA)", "KE1_TestBilder", "tga_daten_zu_kurz.tga", "tga_daten_zu_kurz.propra"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Der angegebene Bildtyp wird nicht unterstützt. (TGA)", "KE1_TestBilder", "tga_falscher_bildtyp.tga", "tga_falscher_bildtyp.propra"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Nicht unterstützte Anzahl von Bits pro Bildpunkt (TGA)", "KE1_TestBilder", "tga_falsche_bits.tga", "tga_falsche_bits.propra"),
                new TestCase("[KE1] [Fehlerfall wird erkannt] Attributbyte wird nicht unterstützt (Propra)", "KE1_TestBilder", "tga_falsches_attributbyte.tga", "tga_falsches_attributbyte.propra")
        );
    }

    static List<TestCase> successfulTestCases() {
        return List.of(
                //       new TestCase("[Erfolgsfall] TGA -> Propra -> TGA: mit Metadaten", "tga_with_meta.tga", "tga_with_meta.propra"),
                new TestCase("[KE1] [Erfolgsfall] TGA -> Propra -> TGA: mit 1 x 1 Pixel", "KE1_TestBilder", "tga_1pixel.tga", "tga_1pixel.propra"),
                new TestCase("[KE1V] [Erfolgsfall] TGA -> Propra -> TGA: vorgegebener Testfall 1", "KE1_TestBilder", "test_01_uncompressed.tga", "test_01_uncompressed.propra"),
                new TestCase("[KE1V] [Erfolgsfall] TGA -> Propra -> TGA: vorgegebener Testfall 2", "KE1_TestBilder", "test_02_uncompressed.tga", "test_02_uncompressed.propra"),
                new TestCase("[KE1V] [Erfolgsfall] Propra -> TGA -> Propra: vorgegebener Testfall 3", "KE1_TestBilder", "test_03_uncompressed.propra", "test_03_uncompressed.tga"),
                new TestCase("[KE1V] [Erfolgsfall] Propra -> TGA -> Propra: vorgegebener Testfall 4", "KE1_TestBilder", "test_04_uncompressed.propra", "test_04_uncompressed.tga")
                //   new TestCase("[KE1] [Erfolgsfall] TGA -> Propra -> TGA: besonders große Datei (> 200 MB)", "tga_grosse_datei.tga", "tga_grosse_datei.propra"),

                //           new TestCase("[KE2] [Erfolgsfall] TGA (rle) -> Propra -> TGA: test_02_rle.tga", "KE2_TestBilder", "test_02_rle.tga", "test_02_rle.propra"),
                //           new TestCase("[KE2] [Erfolgsfall] TGA (rle) -> TGA (rle): test_02_rle.tga", "KE2_TestBilder", "test_02_rle.tga", "test_02_rle.tga", "rle", "rle")
        );
    }

    static List<TestCase> equalsTestCases() {
        return List.of(
                new TestCase("[KE2] [Erfolgsfall] TGA (rle) -> TGA (rle): simple_compressed_1616.tga", "KE2_TestBilder", "simple_compressed_1616.tga", "simple_compressed_1616.tga", "rle",
                        "rle"),
                new TestCase("[KE2] [Erfolgsfall] TGA (rle) -> TGA (rle): test_02_rle.tga", "KE2_TestBilder", "test_02_rle.tga", "test_02_rle.tga", "rle", "rle")
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
    @MethodSource("equalsTestCases")
    @DisplayName("Erfolgsfälle werden erfolgreich konvertiert und entsprechen später wieder der Eingabe.")
    public void equalsTest(TestCase testCase) throws Exception {
        String fileNameInput = testCase.inputFile;
        String fileNameOutput = testCase.outputFile;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[] { "--input=" + testFileFolder + fileNameInput, "--output=" + fileNameOutput, "--compression=" + testCase.outputCompression });

        checkEqualFiles(testFileFolder + fileNameInput, fileNameOutput);
    }

    @ParameterizedTest
    @MethodSource("successfulTestCases")
    @DisplayName("Erfolgsfälle werden erfolgreich konvertiert und entsprechen später wieder der Eingabe.")
    public void allTests(TestCase testCase) throws Exception {
        String fileNameInput = testCase.inputFile;
        String fileNameOutput = testCase.outputFile;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[] { "--input=" + testFileFolder + fileNameInput, "--output=" + fileNameOutput, "--compression=" + testCase.outputCompression });
        // Kovertierung von Ausgabe in Eingabe
        ImageConverter.startWithArgs(new String[] { "--input=" + fileNameOutput, "--output=" + fileNameInput, "--compression=" + testCase.inputCompression });

        // Konvertierung von Eingabe in Eingabe
        ImageConverter.startWithArgs(new String[] { "--input=" + testFileFolder + fileNameInput, "--output=bbb_" + fileNameInput, "--compression=" + testCase.inputCompression });

        checkEqualFiles(testFileFolder + fileNameInput, fileNameInput);
        checkEqualFiles(testFileFolder + fileNameInput, "./bbb_" + fileNameInput);
    }


    @ParameterizedTest
    @MethodSource("errorTestCases")
    @DisplayName("Fehlerfälle werden erfolgreich erkannt.")
    void testErrorFiles(TestCase testCase) throws IOException {
        try {
            String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

            ImageConverter.startWithArgs(new String[]{"--input=" + testFileFolder + testCase.inputFile, "--output=" + testCase.outputFile});
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
        final String resourcesFolder;
        final String inputFile;
        final String outputFile;
        final String inputCompression;
        final String outputCompression;

        TestCase(String displayName, String resourcesFolder, String inputFile, String outputFile) {
            this.displayName = displayName;
            this.resourcesFolder = resourcesFolder;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.inputCompression = "uncompressed";
            this.outputCompression = "uncompressed";
        }

        public TestCase(String displayName, String resourcesFolder, String inputFile, String outputFile, String inputCompression, String outputCompression) {
            this.displayName = displayName;
            this.resourcesFolder = resourcesFolder;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.inputCompression = inputCompression;
            this.outputCompression = outputCompression;
        }

        @Override public String toString() {
            return "TestCase{" +
                    "displayName='" + displayName + '\'' +
                    ", resourcesFolder='" + resourcesFolder + '\'' +
                    ", inputFile='" + inputFile + '\'' +
                    ", outputFile='" + outputFile + '\'' +
                    ", inputCompression='" + inputCompression + '\'' +
                    ", outputCompression='" + outputCompression + '\'' +
                    '}';
        }
    }

}
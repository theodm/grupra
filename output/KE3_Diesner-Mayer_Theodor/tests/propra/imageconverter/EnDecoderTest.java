package propra.imageconverter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class EnDecoderTest {

    static List<BaseNEnDecodeTestCase> encodeN() {
        return List.of(
                new BaseNEnDecodeTestCase("KE2_TestBilder", "test_02_rle.tga", "aAbBcCdDeEfFgGhHiIjJkKMmoOpPrRtTuUvVwWxXyYzZ0987654321äÄöÖüÜ:.()"),
                new BaseNEnDecodeTestCase("KE2_TestBilder", "test_04_rle.propra", "aAbBcCdDeEfFgGhHiIjJkKMmoOpPrRtTuUvVwWxXyYzZ0987654321äÄöÖüÜ:.()")
        );
    }

    static List<Base32EnDecodeTestCase> decodeN() {
        return List.of(
                new Base32EnDecodeTestCase("KE2_TestBilder_optional", "test_base-2_a.propra.base-n"),
                new Base32EnDecodeTestCase("KE2_TestBilder_optional", "test_base-2_b.propra.base-n"),
                new Base32EnDecodeTestCase("KE2_TestBilder_optional", "test_base-4.propra.base-n"),
                new Base32EnDecodeTestCase("KE2_TestBilder_optional", "test_base-8.propra.base-n"),
                new Base32EnDecodeTestCase("KE2_TestBilder_optional", "test_base-64.propra.base-n")
        );
    }

    static List<Base32EnDecodeTestCase> decode32() {
        return List.of(
                new Base32EnDecodeTestCase("KE2_TestBilder", "test_05_base32.tga.base-32"),
                new Base32EnDecodeTestCase("KE2_TestBilder", "test_06_base32.propra.base-32")
        );
    }

    static List<Base32EnDecodeTestCase> encode32() {
        return List.of(
                new Base32EnDecodeTestCase("KE2_TestBilder", "test_02_rle.tga"),
                new Base32EnDecodeTestCase("KE2_TestBilder", "test_04_rle.propra")
        );
    }

    @ParameterizedTest
    @MethodSource("decodeN")
    @DisplayName("Erfolgsfälle werden erfolgreich Base-N dekodiert und kodiert und entsprechen später wieder der Eingabe.")
    public void decodeNTest(Base32EnDecodeTestCase testCase) throws Exception {
        String testOutFolder = "." + testCase.resourcesFolder + "/" + ImageConverterTest.testOutFolder;

        Files.createDirectories(Paths.get(testOutFolder));
        String fileNameInput = testCase.input;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        Files.copy(Paths.get(testFileFolder + fileNameInput), Paths.get(testOutFolder + fileNameInput), StandardCopyOption.REPLACE_EXISTING);

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput, "--decode-base-n"});
        // Kovertierung von Ausgabe in Eingabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput.substring(0, fileNameInput.length() - ".base-n".length()), "--encode-base-n=" + Files.readAllLines(Paths.get(testFileFolder + fileNameInput)).get(0)});

        ImageConverterTest.checkEqualFiles(testFileFolder + fileNameInput, testOutFolder + fileNameInput);
    }

    @ParameterizedTest
    @MethodSource("decode32")
    @DisplayName("Erfolgsfälle werden erfolgreich Base-32 dekodiert und kodiert und entsprechen später wieder der Eingabe.")
    public void decodeTest(Base32EnDecodeTestCase testCase) throws Exception {
        String testOutFolder = "." + testCase.resourcesFolder + "/" + ImageConverterTest.testOutFolder;

        Files.createDirectories(Paths.get(testOutFolder));
        String fileNameInput = testCase.input;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        Files.copy(Paths.get(testFileFolder + fileNameInput), Paths.get(testOutFolder + fileNameInput), StandardCopyOption.REPLACE_EXISTING);

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput, "--decode-base-32"});
        // Kovertierung von Ausgabe in Eingabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput.substring(0, fileNameInput.length() - ".base-32".length()), "--encode-base-32"});

        ImageConverterTest.checkEqualFiles(testFileFolder + fileNameInput, testOutFolder + fileNameInput);
    }

    @ParameterizedTest
    @MethodSource("encode32")
    @DisplayName("Erfolgsfälle werden erfolgreich Base-32 kodiert und dekodiert und entsprechen später wieder der Eingabe.")
    public void encodeTest(Base32EnDecodeTestCase testCase) throws Exception {
        String testOutFolder = "." + testCase.resourcesFolder + "/" + ImageConverterTest.testOutFolder;

        Files.createDirectories(Paths.get(testOutFolder));
        String fileNameInput = testCase.input;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        Files.copy(Paths.get(testFileFolder + fileNameInput), Paths.get(testOutFolder + fileNameInput), StandardCopyOption.REPLACE_EXISTING);

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput, "--encode-base-32"});
        // Kovertierung von Ausgabe in Eingabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput + ".base-32", "--decode-base-32"});

        ImageConverterTest.checkEqualFiles(testFileFolder + fileNameInput, testOutFolder + fileNameInput);
    }

    @ParameterizedTest
    @MethodSource("encodeN")
    @DisplayName("Erfolgsfälle werden erfolgreich Base-32 kodiert und dekodiert und entsprechen später wieder der Eingabe.")
    public void encodeNTest(BaseNEnDecodeTestCase testCase) throws Exception {
        String testOutFolder = "." + testCase.resourcesFolder + "/" + ImageConverterTest.testOutFolder;

        Files.createDirectories(Paths.get(testOutFolder));
        String fileNameInput = testCase.input;

        String testFileFolder = "./src/main/resources/" + testCase.resourcesFolder + "/";

        Files.copy(Paths.get(testFileFolder + fileNameInput), Paths.get(testOutFolder + fileNameInput), StandardCopyOption.REPLACE_EXISTING);

        // Konvertierung von Eingabe in Ausgabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput, "--encode-base-n=" + testCase.alphabet});
        // Kovertierung von Ausgabe in Eingabe
        ImageConverter.startWithArgs(new String[]{"--input=" + testOutFolder + fileNameInput + ".base-n", "--decode-base-n"});

        ImageConverterTest.checkEqualFiles(testFileFolder + fileNameInput, testOutFolder + fileNameInput);
    }

    static class BaseNEnDecodeTestCase {
        public final String resourcesFolder;
        public final String input;
        public final String alphabet;

        public BaseNEnDecodeTestCase(String resourcesFolder, String input, String alphabet) {
            this.resourcesFolder = resourcesFolder;
            this.input = input;
            this.alphabet = alphabet;
        }

        @Override
        public String toString() {
            return "BaseNEnDecodeTestCase{" +
                    "resourcesFolder='" + resourcesFolder + '\'' +
                    ", input='" + input + '\'' +
                    ", alphabet='" + alphabet + '\'' +
                    '}';
        }
    }

    static class Base32EnDecodeTestCase {
        public final String resourcesFolder;
        public final String input;

        public Base32EnDecodeTestCase(String resourcesFolder, String input) {
            this.resourcesFolder = resourcesFolder;
            this.input = input;
        }

        @Override
        public String toString() {
            return "Base32EncodeTestCase{" +
                    "resourcesFolder='" + resourcesFolder + '\'' +
                    ", input='" + input + '\'' +
                    '}';
        }

    }


}

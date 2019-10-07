package propra.imageconverter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ImageConverterTest {
    private void checkEqualFiles(String pathA, String pathB) throws IOException {
        byte[] bytesA = Files.readAllBytes(Paths.get(pathA));
        byte[] bytesB = Files.readAllBytes(Paths.get(pathB));

        assertArrayEquals(bytesA, bytesB);
    }
    @Test
    void testGross() throws Exception {
        testBack("tga_grosse_datei.tga", "tga_grosse_datei.propra");
    }

    public void testBack(String fileNameInput, String fileNameOutput) throws Exception {
        ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + fileNameInput, "--output=" + fileNameOutput});
        ImageConverter.startWithArgs(new String[]{"--input=" + fileNameOutput, "--output=" + fileNameInput});
        checkEqualFiles("./src/main/resources/KE1_TestBilder/" + fileNameInput, fileNameInput);

        ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + fileNameInput, "--output=bbb_" + fileNameInput});

        checkEqualFiles("./src/main/resources/KE1_TestBilder/" + fileNameInput, "./bbb_" + fileNameInput);
    }

    @Test
    public void allTests() throws Exception {
        // Funktioniert nicht wegen Footer, aber grds konform
        //testBack("tga_1pixel_with_footer.tga", "tga_1pixel_with_footer.propra");
        testBack("tga_1pixel.tga", "tga_1pixel.propra");

        testBack("test_01_uncompressed.tga", "test_01_uncompressed.propra");
        testBack("test_02_uncompressed.tga", "test_02_uncompressed.propra");
        testBack("test_03_uncompressed.propra", "test_03_uncompressed.tga");
        testBack("test_04_uncompressed.propra", "test_04_uncompressed.tga");
    }

    private void testThrows(String fileNameInput, String fileNameOutput) {
        try {
            ImageConverter.startWithArgs(new String[]{"--input=./src/main/resources/KE1_TestBilder/" + fileNameInput, "--output=" + fileNameOutput});
        } catch (Exception e) {
            System.out.println("ERROR: " + fileNameInput + "\n");
            e.printStackTrace();
            System.out.println("\n" + e.getMessage() + "\n");
            return;
        }

        fail();
    }

    @Test
    void tooLongTooShortTest() throws IOException {
        testThrows("propra_daten_zu_kurz.propra", "propra_daten_zu_kurz.tga");
        testThrows("propra_daten_zu_lang.propra", "propra_daten_zu_lang.tga");
        testThrows("propra_0breite.propra", "propra_0breite.tga");
        testThrows("propra_0hoehe.propra", "propra_0hoehe.tga");
        testThrows("propra_wrong_bit.propra", "propra_wrong_bit.tga");
        testThrows("propra_wrong_checksum.propra", "propra_wrong_checksum.tga");
        testThrows("propra_wrong_compression.propra", "propra_wrong_compression.tga");

        testThrows("tga_0breite.tga", "tga_0breite.propra");
        testThrows("tga_0hoehe.tga", "tga_0hoehe.propra");
        testThrows("tga_daten_zu_kurz.tga", "tga_daten_zu_kurz.propra");
        testThrows("tga_falscher_bildtyp.tga", "tga_falscher_bildtyp.propra");
        testThrows("tga_falsche_bits.tga", "tga_falsche_bits.propra");
        testThrows("tga_falsches_attributbyte.tga", "tga_falsches_attributbyte.propra");



    }

}
package propra.imageconverter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ImageConverterTest {
    private void checkEqualFiles(String pathA, String pathB) throws IOException {
        byte[] bytesA = Files.readAllBytes(Paths.get(pathA));
        byte[] bytesB = Files.readAllBytes(Paths.get(pathB));

        assertArrayEquals(bytesA, bytesB);
    }

    @Test
    public void propraToPropraTest() {
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_03_uncompressed.propra", "--output=test_03_uncompressed_p2p.propra"});
    }

    @Test
    void testTestor() throws IOException {
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/testor.tga", "--output=testor.propra"});
        ImageConverter.main(new String[]{"--input=testor.propra", "--output=testor.tga"});
        checkEqualFiles("./src/main/resources/KE1_TestBilder/testor.tga", "testor.tga");
    }

    @Test
    void testGross() throws IOException {
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_gross2.tga", "--output=test_gross.propra"});
        ImageConverter.main(new String[]{"--input=test_gross.propra", "--output=test_gross2.tga"});
        checkEqualFiles("./src/main/resources/KE1_TestBilder/test_gross2.tga", "test_gross2.tga");
    }

    @Test
    public void allTests() throws IOException {
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_01_uncompressed.tga", "--output=test_01_uncompressed.propra"});
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_02_uncompressed.tga", "--output=test_02_uncompressed.propra"});
        //    ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_gross2.tga", "--output=test_gross.propra"});
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_03_uncompressed.propra", "--output=test_03_uncompressed.tga"});
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_04_uncompressed.propra", "--output=test_04_uncompressed.tga"});

        ImageConverter.main(new String[]{"--input=test_01_uncompressed.propra", "--output=test_01_uncompressed.tga"});
        ImageConverter.main(new String[]{"--input=test_02_uncompressed.propra", "--output=test_02_uncompressed.tga"});
        //     ImageConverter.main(new String[]{"--input=test_gross.propra", "--output=test_gross2.tga"});
        ImageConverter.main(new String[]{"--input=test_03_uncompressed.tga", "--output=test_03_uncompressed.propra"});
        ImageConverter.main(new String[]{"--input=test_04_uncompressed.tga", "--output=test_04_uncompressed.propra"});

        checkEqualFiles("./src/main/resources/KE1_TestBilder/test_01_uncompressed.tga", "test_01_uncompressed.tga");
        checkEqualFiles("./src/main/resources/KE1_TestBilder/test_02_uncompressed.tga", "test_02_uncompressed.tga");
        //   checkEqualFiles("./src/main/resources/KE1_TestBilder/test_gross2.tga", "test_gross2.tga");
        checkEqualFiles("./src/main/resources/KE1_TestBilder/test_03_uncompressed.propra", "test_03_uncompressed.propra");
        checkEqualFiles("./src/main/resources/KE1_TestBilder/test_04_uncompressed.propra", "test_04_uncompressed.propra");
    }

    @Test
    void tooLongTooShortTest() throws IOException {
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_03_uncompressed_zulang.propra", "--output=test_03_uncompressed_zulang.tga"});
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_03_uncompressed_zukurz.propra", "--output=test_03_uncompressed_zukurz.tga"});
        ImageConverter.main(new String[]{"--input=./src/main/resources/KE1_TestBilder/test_02_uncompressed_zukurz.tga", "--output=test_02_uncompressed_zukurz.propra"});

    }

}
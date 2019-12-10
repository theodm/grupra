mkdir KE2_Diesner-Mayer_Theodor
cd KE2_Diesner-Mayer_Theodor
mkdir KE2_Konvertiert
unzip ../KE2_Diesner-Mayer_Theodor.zip
unzip ../KE2_TestBilder.zip
javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_01_uncompressed.tga --output=../KE2_Konvertiert/test_01.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga --output=../KE2_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_03_uncompressed.propra --output=../KE2_Konvertiert/test_03.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra --output=../KE2_Konvertiert/test_04.tga --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_05_base32.tga.base-32 --decode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_06_base32.propra.base-32 --decode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga --encode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra --encode-base-32
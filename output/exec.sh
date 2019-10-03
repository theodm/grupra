mkdir KE1_Diesner-Mayer_Theodor
cd KE1_Diesner-Mayer_Theodor
mkdir KE1_Konvertiert
unzip ../KE1_Diesner-Mayer_Theodor.zip
unzip ../KE1_TestBilder.zip
javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.tga
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.tga

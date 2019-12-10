mkdir KE3_Diesner-Mayer_Theodor
cd KE3_Diesner-Mayer_Theodor
mkdir KE3_Konvertiert
unzip ../KE3_Diesner-Mayer_Theodor.zip
unzip ../KE3_TestBilder.zip
javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_01_uncompressed.tga  --output=../KE3_Konvertiert/test_01.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_02_rle.tga           --output=../KE3_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_03_uncompressed.propra --output=../KE3_Konvertiert/test_03.tga  --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_04_rle.propra          --output=../KE3_Konvertiert/test_04.tga  --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE3_TestBilder/test_05_huffman.propra      --output=../KE3_Konvertiert/test_05.tga  --compression=rle
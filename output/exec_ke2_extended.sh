mkdir KE2_Diesner-Mayer_Theodor
cd KE2_Diesner-Mayer_Theodor
mkdir KE1_Konvertiert
mkdir KE2_Konvertiert
unzip ../KE2_Diesner-Mayer_Theodor.zip
unzip ../KE1_TestBilder.zip
unzip ../KE2_TestBilder.zip
unzip ../KE2_TestBilder_optional.zip
javac --source-path src src/propra/imageconverter/ImageConverter.java -d bin
cd bin
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_01_uncompressed.tga --output=../KE1_Konvertiert/test_01.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_02_uncompressed.tga --output=../KE1_Konvertiert/test_02.propra
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_03_uncompressed.propra --output=../KE1_Konvertiert/test_03.tga
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE1_TestBilder/test_04_uncompressed.propra --output=../KE1_Konvertiert/test_04.tga
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_01_uncompressed.tga --output=../KE2_Konvertiert/test_01.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga --output=../KE2_Konvertiert/test_02.propra --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_03_uncompressed.propra --output=../KE2_Konvertiert/test_03.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra --output=../KE2_Konvertiert/test_04.tga --compression=uncompressed
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_05_base32.tga.base-32 --decode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_06_base32.propra.base-32 --decode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_02_rle.tga --encode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder/test_04_rle.propra --encode-base-32
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_a.propra.base-n --decode-base-n
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-2_b.propra.base-n --decode-base-n
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-4.propra.base-n --decode-base-n
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-8.propra.base-n --decode-base-n
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_base-64.propra.base-n --decode-base-n
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_grosses_bild.propra --output=../KE2_Konvertiert/test_grosses_bild_compressed.tga --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_grosses_bild.propra --output=../KE2_Konvertiert/test_grosses_bild_compressed.propra --compression=rle
java -Xmx256m propra.imageconverter.ImageConverter --input=../KE2_TestBilder_optional/test_grosses_bild.propra --encode-base-n=aAbBcCdDeEfFgGhHiIjJkKMmoOpPrRtTuUvVwWxXyYzZ0987654321äÄöÖüÜ:.\(\)
md5sum ../KE1_Konvertiert/test_01.propra
md5sum ../KE1_Konvertiert/test_02.propra
md5sum ../KE1_Konvertiert/test_03.tga
md5sum ../KE1_Konvertiert/test_04.tga
md5sum ../KE2_Konvertiert/test_01.propra
md5sum ../KE2_Konvertiert/test_02.propra
md5sum ../KE2_Konvertiert/test_03.tga
md5sum ../KE2_Konvertiert/test_04.tga
md5sum ../KE2_TestBilder/test_05_base32.tga
md5sum ../KE2_TestBilder/test_06_base32.propra
md5sum ../KE2_TestBilder/test_02_rle.tga.base-32
md5sum ../KE2_TestBilder/test_04_rle.propra.base-32
md5sum ../KE2_TestBilder_optional/test_base-2_a.propra
md5sum ../KE2_TestBilder_optional/test_base-2_b.propra
md5sum ../KE2_TestBilder_optional/test_base-4.propra
md5sum ../KE2_TestBilder_optional/test_base-8.propra
md5sum ../KE2_TestBilder_optional/test_base-64.propra
md5sum ../KE2_Konvertiert/test_grosses_bild_compressed.tga
md5sum ../KE2_Konvertiert/test_grosses_bild_compressed.propra
md5sum ../KE2_TestBilder_optional/test_grosses_bild.propra.base-n
wc -c < ../KE1_Konvertiert/test_01.propra
wc -c < ../KE1_Konvertiert/test_02.propra
wc -c < ../KE1_Konvertiert/test_03.tga
wc -c < ../KE1_Konvertiert/test_04.tga
wc -c < ../KE2_Konvertiert/test_01.propra
wc -c < ../KE2_Konvertiert/test_02.propra
wc -c < ../KE2_Konvertiert/test_03.tga
wc -c < ../KE2_Konvertiert/test_04.tga
wc -c < ../KE2_TestBilder/test_05_base32.tga
wc -c < ../KE2_TestBilder/test_06_base32.propra
wc -c < ../KE2_TestBilder/test_02_rle.tga.base-32
wc -c < ../KE2_TestBilder/test_04_rle.propra.base-32
wc -c < ../KE2_TestBilder_optional/test_base-2_a.propra
wc -c < ../KE2_TestBilder_optional/test_base-2_b.propra
wc -c < ../KE2_TestBilder_optional/test_base-4.propra
wc -c < ../KE2_TestBilder_optional/test_base-8.propra
wc -c < ../KE2_TestBilder_optional/test_base-64.propra
wc -c < ../KE2_Konvertiert/test_grosses_bild_compressed.tga
wc -c < ../KE2_Konvertiert/test_grosses_bild_compressed.propra
wc -c < ../KE2_TestBilder_optional/test_grosses_bild.propra.base-n
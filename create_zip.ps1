New-Item -ItemType Directory -Force -Path ./output
New-Item -ItemType Directory -Force -Path ./temp
New-Item -ItemType Directory -Force -Path ./temp/src
New-Item -ItemType Directory -Force -Path ./temp/tests
Remove-Item ./output/KE3_Diesner-Mayer_Theodor.zip
Copy-Item -Path ./src/main/java/* -Destination ./temp/src -Recurse
Copy-Item -Path ./src/test/java/* -Destination ./temp/tests -Recurse
cd ./temp
7z a ../output/KE3_Diesner-Mayer_Theodor.zip -tzip -i!src -i!tests
cd ..
Remove-Item ./temp -Force -Recurse
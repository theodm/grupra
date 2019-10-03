New-Item -ItemType Directory -Force -Path ./output
New-Item -ItemType Directory -Force -Path ./temp
New-Item -ItemType Directory -Force -Path ./temp/src
Remove-Item ./output/KE1_Diesner-Mayer_Theodor.zip
Copy-Item -Path ./src/main/java/* -Destination ./temp/src -Recurse
cd ./temp
7z a ../output/KE1_Diesner-Mayer_Theodor.zip -tzip -i!src
cd ..
Remove-Item ./temp -Force -Recurse
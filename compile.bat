dir /s /b *.java > srcfiles.txt
javac -d ./bin -target 1.5 @srcfiles.txt
pause
cd bin
dir /s /b *.class *.html *.jpg *.gif *.png > ../jarfiles.txt

@echo OFF
echo Please change jarfiles.txt to relative paths....
@echo ON

pause
jar cvfm ../ClassicalBuilder.jar ../manifest.mf @../jarfiles.txt
cd ..
jarsigner -verbose ClassicalBuilder.jar ClassicalBuilder
pause
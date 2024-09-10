del RealTimeChess.jar
rd /s /q app

call gradlew lwjgl3:dist

FOR /F "tokens=* USEBACKQ" %%F IN (`git describe --tags`) DO (SET describe=%%F)
FOR /f "tokens=1 delims=-" %%a in ("%describe%") do (SET tag=%%a)
FOR /f "tokens=2 delims=-" %%a in ("%describe%") do (SET build=%%a)

robocopy lwjgl3\build\libs\ . RealTimeChess-latest.jar
rename RealTimeChess-latest.jar RealTimeChess.jar

java -jar /Apps/packr-all-4.0.0.jar packr-config.json

del RealTimeChess.jar

echo RealTimeChess %tag%.%build% > "app/RealTimeChess %tag%.%build%.version

pause

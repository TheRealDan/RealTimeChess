call gradlew lwjgl3:dist

FOR /F "tokens=* USEBACKQ" %%F IN (`git describe --tags`) DO (SET describe=%%F)
FOR /f "tokens=1 delims=-" %%a in ("%describe%") do (SET tag=%%a)
FOR /f "tokens=2 delims=-" %%a in ("%describe%") do (SET build=%%a)

robocopy lwjgl3\build\libs\ jars\ RealTimeChess-latest.jar
rename jars\RealTimeChess-latest.jar "RealTimeChess %tag%.%build%.jar"

pause

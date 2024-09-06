call gradlew lwjgl3:dist

FOR /F "tokens=* USEBACKQ" %%F IN (`git describe --tags`) DO (SET describe=%%F)
FOR /f "tokens=1 delims=-" %%a in ("%describe%") do (SET tag=%%a)
FOR /f "tokens=2 delims=-" %%a in ("%describe%") do (SET build=%%a)

robocopy lwjgl3\build\libs\ builds\ RealTimeChess-latest.jar
rename builds\RealTimeChess-latest.jar "RealTimeChess %tag%.%build%.jar"

pause

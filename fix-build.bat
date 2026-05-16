@echo off
setlocal
cd /d "%~dp0"

if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%~dp0.gradle-home"

echo Stopping Gradle daemons...
call gradlew.bat --stop

echo.
echo Close the IAE application if it is running, then press any key to delete old build folders...
pause >nul

if exist "temp" (
    echo Removing temp\ ...
    rmdir /s /q "temp" 2>nul
)
if exist "build" (
    echo Removing build\ ...
    rmdir /s /q "build" 2>nul
)

echo.
echo Running clean build...
call gradlew.bat clean run --no-daemon

endlocal

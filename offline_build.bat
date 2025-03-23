@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Offline Build Script
echo ===================================
echo.

:: Set Java environment
echo Setting up JDK 17...
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Using JDK: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

:: Stop the Gradle daemon
echo Stopping Gradle daemons...
call gradlew.bat --stop
timeout /t 2 /nobreak > nul

:: Check for stub fragments existence
echo Checking for stub fragments...
if not exist "app\src\main\java\com\example\electricalcalculator\ui\pipebending\PipeBendingFragment.kt" (
    echo Warning: Stub fragments not found.
)
echo.

:: Set Gradle options to disable incremental compilation and daemon
set "GRADLE_OPTS=-Dkotlin.incremental=false -Dorg.gradle.daemon=false -Dkotlin.compiler.execution.strategy=in-process"

echo.
echo Building app using OFFLINE mode...
echo This will use only previously downloaded dependencies.
echo.

:: Clean and build with offline mode
call gradlew.bat clean --offline
echo.
echo Starting offline build...
echo.

call gradlew.bat assembleDebug --offline --refresh-dependencies=false --no-daemon -Dorg.gradle.java.home="%JDK_PATH%" --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ===================================
    echo Build failed in offline mode.
    echo ===================================
    echo.
    echo Possible solutions:
    echo 1. Try running the standard fix_sourceset_and_build.bat script to download dependencies
    echo 2. Check if your Gradle cache is valid
    echo 3. Verify your environment settings
)

echo.
echo Press any key to exit...
pause > nul

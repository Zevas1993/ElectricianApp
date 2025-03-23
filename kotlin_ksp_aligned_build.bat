@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Kotlin-KSP Aligned Build
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

:: Set JDK module fixes
echo Setting JDK module system fixes...
set "JVM_ARGS=--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"

:: Clean everything
echo Performing deep clean to remove any Kotlin 1.9.0 remnants...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
rmdir /s /q "feature\box\build" 2>nul
rmdir /s /q "feature\conduit\build" 2>nul
rmdir /s /q "feature\dwelling\build" 2>nul
echo.

echo Building app with Kotlin 1.8.22 and KSP 1.8.22-1.0.11 alignment...
echo This ensures that the KSP and Kotlin versions match properly.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Build with Kotlin 1.8.22 and KSP 1.8.22-1.0.11 explicitly specified
echo Building with Kotlin-KSP version alignment...
call gradlew.bat assembleDebug --no-daemon -Dkotlin.version=1.8.22 -Dksp.version=1.8.22-1.0.11 -Dkotlin.compiler.execution.strategy=in-process -Dorg.gradle.java.home="%JDK_PATH%" %JVM_ARGS% --refresh-dependencies --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Kotlin-KSP version alignment fixed the build.
    echo - Kotlin version: 1.8.22
    echo - KSP version: 1.8.22-1.0.11
    echo - JDK 17 module system fixes were applied
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Check if any modules still reference Kotlin 1.9.0
    echo 2. Look for version conflicts in the error output
    echo 3. Try running with '--debug' for more detailed logs
)

echo.
echo Press any key to exit...
pause > nul

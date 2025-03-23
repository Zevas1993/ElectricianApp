@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp JDK Module System Fix
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
set "GRADLE_OPTS=--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"

:: Clean kapt directories
echo Cleaning kapt generated code directories...
rmdir /s /q "data\build\tmp\kapt3" 2>nul
rmdir /s /q "data\build\generated\source\kapt" 2>nul
rmdir /s /q "domain\build\tmp\kapt3" 2>nul
rmdir /s /q "domain\build\generated\source\kapt" 2>nul
rmdir /s /q "app\build\tmp\kapt3" 2>nul
rmdir /s /q "app\build\generated\source\kapt" 2>nul
rmdir /s /q "feature\box\build\tmp\kapt3" 2>nul
rmdir /s /q "feature\conduit\build\tmp\kapt3" 2>nul
rmdir /s /q "feature\dwelling\build\tmp\kapt3" 2>nul

:: Clean build cache
echo Cleaning build cache...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
echo.

echo Building app with JDK module system fixes...
echo This may take several minutes to download dependencies and compile.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Clean first
call gradlew.bat clean --no-daemon
echo.
echo Clean completed. Starting build...
echo.

:: Build with JDK module fixes
echo Building app with JDK module system fixes...
call gradlew.bat assembleDebug --no-daemon -Dorg.gradle.java.home="%JDK_PATH%" %GRADLE_OPTS% --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Note: This build used JDK module system fixes to resolve compiler errors.
    echo       The app is using standard KAPT annotation processing.
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Check for JDK 17 module errors not covered by the current fixes
    echo 2. Examine the full error message for specific class conflicts
    echo 3. Try running with --debug for more detailed output
)

echo.
echo Press any key to exit...
pause > nul

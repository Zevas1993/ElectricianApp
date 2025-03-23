@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Clean Build Script
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

:: Clean Gradle kapt generated directories
echo Cleaning kapt generated code...
rmdir /s /q "data\build\generated" 2>nul
rmdir /s /q "domain\build\generated" 2>nul
rmdir /s /q "app\build\generated" 2>nul
rmdir /s /q "feature\box\build\generated" 2>nul
rmdir /s /q "feature\conduit\build\generated" 2>nul
rmdir /s /q "feature\dwelling\build\generated" 2>nul

rmdir /s /q "data\build\tmp\kapt3" 2>nul
rmdir /s /q "domain\build\tmp\kapt3" 2>nul
rmdir /s /q "app\build\tmp\kapt3" 2>nul
rmdir /s /q "feature\box\build\tmp\kapt3" 2>nul
rmdir /s /q "feature\conduit\build\tmp\kapt3" 2>nul
rmdir /s /q "feature\dwelling\build\tmp\kapt3" 2>nul

echo Cleaning build cache...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
echo.

:: Set Gradle options to disable incremental compilation and kapt caching
set "GRADLE_OPTS=-Dkotlin.incremental=false -Dorg.gradle.daemon=false -Dkotlin.compiler.execution.strategy=in-process"

echo Building the app with clean kapt directories...
echo This may take several minutes to download dependencies and compile.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Clean and build with longer timeouts
call gradlew.bat clean --info
echo.
echo Clean completed. Starting build...
echo.

:: Download dependencies first
echo Downloading dependencies (this may take a while)...
call gradlew.bat --refresh-dependencies dependencies --no-daemon

:: Then build with all optimizations
echo.
echo Building app...
call gradlew.bat assembleDebug --no-daemon -Dkapt.verbose=true -Dorg.gradle.java.home="%JDK_PATH%" -Dkapt.use.worker.api=false --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Check the Room entity and DAO classes for duplicate definitions
    echo 2. Look for conflicting kapt generated classes
    echo 3. Examine the full error message for specific class conflicts
    echo 4. Try running with --debug for more detailed output
)

echo.
echo Press any key to exit...
pause > nul

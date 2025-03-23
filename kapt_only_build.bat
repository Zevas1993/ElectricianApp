@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp KAPT-Only Build
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
echo Performing deep clean...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
rmdir /s /q "feature\box\build" 2>nul
rmdir /s /q "feature\conduit\build" 2>nul
rmdir /s /q "feature\dwelling\build" 2>nul
echo.

echo Building app with KAPT only (no KSP)...
echo This provides the most stable build configuration.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Build with KAPT only and JDK module system fixes
echo Building with KAPT only...
call gradlew.bat assembleDebug --no-daemon -Dkotlin.incremental=false -Dkapt.use.worker.api=false -Dkapt.incremental.apt=false -Dorg.gradle.java.home="%JDK_PATH%" %JVM_ARGS% --refresh-dependencies --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo KAPT-only build configuration was successful.
    echo - Removed KSP to avoid version conflicts
    echo - Applied JDK 17 module system fixes
    echo - Used standard KAPT annotation processing
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Check the detailed build log for specific errors
    echo 2. Look for dependency version conflicts
    echo 3. Consider downgrading AGP from 8.9.0 to 8.2.x
)

echo.
echo Press any key to exit...
pause > nul

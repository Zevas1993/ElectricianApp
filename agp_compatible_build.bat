@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp AGP Compatible Build
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

echo Building app with AGP 8.2.2 and Gradle 8.0...
echo This configuration is more stable for Android Studio compatibility.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Clean first
call gradlew.bat clean --info --no-daemon
echo.
echo Clean completed. Starting build...
echo.

:: Build with optimized settings for Android Studio
echo Building with AGP 8.2.2 compatibility...
call gradlew.bat assembleDebug --no-daemon -Dkotlin.incremental=false -Dkapt.use.worker.api=false -Dkapt.incremental.apt=false -Dkapt.include.compile.classpath=false -Dkapt.verbose=true -Dorg.gradle.java.home="%JDK_PATH%" %JVM_ARGS% --refresh-dependencies --info

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo The build was successful using:
    echo - Android Gradle Plugin 8.2.2 (downgraded from 8.9.0)
    echo - Gradle 8.0 (downgraded from 8.11.1)
    echo - KAPT optimizations
    echo - JDK 17 module system fixes
    echo.
    echo For Android Studio: Re-import the project and do "File > Sync Project with Gradle Files"
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting steps for Android Studio:
    echo 1. Delete .idea directory in the project
    echo 2. Invalidate caches (File > Invalidate Caches / Restart)
    echo 3. Re-import the project
    echo 4. Try running the build from Android Studio terminal
)

echo.
echo Press any key to exit...
pause > nul

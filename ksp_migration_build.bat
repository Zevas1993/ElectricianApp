@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp KSP Migration Build
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

:: Clean KSP and KAPT directories
echo Cleaning generated code directories...
rmdir /s /q "data\build\generated\ksp" 2>nul
rmdir /s /q "data\build\generated\source\ksp" 2>nul
rmdir /s /q "domain\build\generated\ksp" 2>nul
rmdir /s /q "domain\build\generated\source\ksp" 2>nul
rmdir /s /q "data\build\tmp\kapt3" 2>nul
rmdir /s /q "data\build\generated\source\kapt" 2>nul
rmdir /s /q "domain\build\tmp\kapt3" 2>nul
rmdir /s /q "domain\build\generated\source\kapt" 2>nul

:: Set Gradle options
set "GRADLE_OPTS=-Xmx4096m -Dfile.encoding=UTF-8 --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"

echo.
echo Building app with KSP migration...
echo This build uses both KAPT and KSP to allow gradual migration.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Clean first
call gradlew.bat clean --info
echo.
echo Clean completed. Starting build...
echo.

:: Build with KSP and KAPT
echo Building app with KSP and KAPT...
call gradlew.bat assembleDebug --no-daemon -Dkotlin.incremental=false -Dksp.incremental=false -Dkapt.use.worker.api=false --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo KSP Migration Notes:
    echo 1. The project is configured for a hybrid KSP/KAPT approach
    echo 2. Room now uses KSP for much faster build times
    echo 3. Hilt still uses KAPT until the migration is complete
    echo 4. To finish the migration, switch other annotation processors to KSP when ready
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. If you see KSP errors but KAPT worked before, temporarily disable KSP:
    echo    - Comment out the KSP plugin and dependencies
    echo    - Run again with just KAPT until the issues are resolved
    echo 2. Check for JDK module system errors and make sure JVM args are correct
    echo 3. Clear KSP caches and run a clean build before retrying
)

echo.
echo Press any key to exit...
pause > nul

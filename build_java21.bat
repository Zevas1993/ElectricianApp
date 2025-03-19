@echo off
setlocal

echo ============================================================
echo ElectricianApp APK Builder - Java 21 + Gradle 8.10
echo ============================================================
echo.

echo === Step 1: Checking Java Environment ===

:: Check if Java is available in the system
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not available in the PATH
    echo Please install Java and ensure it's in the PATH
    goto :error_exit
)

java -version
echo.

echo.
echo === Step 2: Configuring Environment ===
set PATH=%JAVA_HOME%\bin;%PATH%
set JAVA_OPTS=-Xmx2048m

echo.
echo === Step 3: Verifying Project Files ===
if not exist "gradle.properties" (
    echo ERROR: Not in the correct project directory!
    echo Please run this script from the root of the ElectricianApp project.
    goto :error_exit
)

echo Gradle version: 8.10 (specified in gradle-wrapper.properties)
echo Android Gradle Plugin: 8.2.2 (specified in build.gradle)
echo App name: Electrician Pro Tools
echo.

echo === Step 4: Cleaning Project ===
call gradlew.bat clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to clean project!
    goto :error_exit
)

echo.
echo === Step 5: Building Debug APK ===
call gradlew.bat --info assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build debug APK!
    goto :error_exit
)

echo.
echo === Step 6: Verifying Build Output ===
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ERROR: APK not found at expected location!
    goto :error_exit
)

echo.
echo ============================================================
echo BUILD SUCCESSFUL!
echo ============================================================
echo.
echo APK file is available at:
echo %CD%\app\build\outputs\apk\debug\app-debug.apk
echo.
echo To install directly to a connected device, run:
echo   gradlew.bat installDebug
echo.
echo To build a release APK (requires signing configuration), run:
echo   gradlew.bat assembleRelease
echo.
goto :end

:error_exit
echo.
echo ============================================================
echo BUILD FAILED!
echo ============================================================
echo Please review the error messages above and try again.
echo Common issues:
echo  - Java 21 installation not found
echo  - Missing dependencies
echo  - Configuration errors in build.gradle files
echo.
exit /b 1

:end
echo.
echo Press any key to exit...
pause > nul
exit /b 0

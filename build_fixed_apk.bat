@echo on
setlocal enabledelayedexpansion

echo ============================================================
echo ElectricianApp APK Builder - Fixed Configuration
echo ============================================================
echo.

echo === Step 1: Checking Java Environment ===
echo Looking for Java installation...

:: Check if JAVA_HOME is already set
if defined JAVA_HOME (
    echo Using existing JAVA_HOME: %JAVA_HOME%
    goto :java_found
)

:: Try to find any installed Java
for /f "tokens=*" %%a in ('where java 2^>nul') do (
    set JAVA_EXE=%%a
    echo Found Java in PATH: !JAVA_EXE!
    for %%i in ("!JAVA_EXE!") do set JAVA_BIN_DIR=%%~dpi
    for %%i in ("!JAVA_BIN_DIR!..") do set JAVA_HOME=%%~fi
    goto :java_found
)

:: If we're here, try to find Java in Program Files
for /d %%d in ("C:\Program Files\Java\*") do (
    if exist "%%d\bin\java.exe" (
        set JAVA_HOME=%%d
        set JAVA_EXE=%%d\bin\java.exe
        goto :java_found
    )
)

:: Try Java in Program Files (x86) as a last resort
for /d %%d in ("C:\Program Files (x86)\Java\*") do (
    if exist "%%d\bin\java.exe" (
        set JAVA_HOME=%%d
        set JAVA_EXE=%%d\bin\java.exe
        goto :java_found
    )
)

echo WARNING: No Java installation found automatically.
echo.
echo If you have Java installed, please set JAVA_HOME manually before running this script:
echo   set JAVA_HOME=C:\path\to\your\java
echo.
echo Attempting to continue with default system Java...
echo.

:java_found
if defined JAVA_HOME (
    echo Using JAVA_HOME: %JAVA_HOME%
    set PATH=%JAVA_HOME%\bin;%PATH%
) else (
    echo No JAVA_HOME set, will try to use system Java
)

:: Verify Java version
java -version 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not available in PATH
    echo Please install Java 8 or 11 and ensure it's in the PATH
    goto :error_exit
)

echo.
echo === Step 2: Configuring Environment ===
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME=%JAVA_HOME%
echo PATH=%PATH%

echo.
echo === Step 3: Verifying Project Files ===
if not exist "gradle.properties" (
    echo ERROR: Not in the correct project directory!
    echo Please run this script from the root of the ElectricianApp project.
    goto :error_exit
)

echo Gradle version: 6.7.1 (compatible with Android Gradle Plugin 4.2.2)
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
echo  - Java version compatibility
echo  - Missing dependencies
echo  - Configuration errors in build.gradle files
echo.
exit /b 1

:end
echo.
echo Press any key to exit...
pause > nul
exit /b 0

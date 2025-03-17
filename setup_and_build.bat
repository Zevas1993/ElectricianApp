@echo off
setlocal EnableDelayedExpansion

echo ElectricianApp Setup and Build Helper
echo ====================================
echo.

:: ===========================
:: Check if Java is installed by capturing its output
:: ===========================
set "javaVersionOutput="
for /f "delims=" %%a in ('java -version 2^>^&1') do (
    set "javaVersionOutput=%%a"
    goto gotVersion
)
:gotVersion
echo Detected Java version output: !javaVersionOutput!
:: Use findstr with a regex that looks for a line starting with "java version"
echo !javaVersionOutput! | findstr /r /c:"^java version" >nul
if errorlevel 1 (
    echo Java not found. You need to install Java JDK before building.
    echo.
    echo Installation steps:
    echo 1. Download JDK from: https://www.oracle.com/java/technologies/downloads/
    echo    (or use OpenJDK: https://adoptium.net/)
    echo 2. Install the JDK (recommended version: JDK 11 or newer)
    echo 3. Run this script again after installation
    echo.
    pause
    exit /b 1
) else (
    echo Java detected.
)
echo.

:: ===========================
:: Find Java home directory
:: ===========================
for /f "tokens=*" %%i in ('where java') do (
    set "JAVA_PATH=%%i"
)
:: Remove \bin\java.exe from the path to get JAVA_HOME
set "JAVA_PATH=%JAVA_PATH:\bin\java.exe=%"
:: Set JAVA_HOME temporarily
set "JAVA_HOME=%JAVA_PATH%"
echo Using JAVA_HOME: %JAVA_HOME%
echo.

:: ===========================
:: Add Java to PATH temporarily
:: ===========================
set "PATH=%PATH%;%JAVA_HOME%\bin"
echo Java has been added to PATH for this session.
echo.

:: ===========================
:: Check Android SDK path
:: ===========================
if not exist local.properties (
    echo local.properties file not found!
    echo Please create a local.properties file with the following content:
    echo.
    echo sdk.dir = C:\path\to\android\sdk
    echo.
    pause
    exit /b 1
)

findstr /c:"sdk.dir" local.properties >nul 2>&1 || (
    echo Android SDK path not found in local.properties.
    echo Please install Android Studio first.
    echo Then run this script again.
    pause
    exit /b 1
)

:: ===========================
:: Option to permanently set JAVA_HOME
:: ===========================
echo Would you like to permanently set JAVA_HOME in your system? (Y/N)
set /p SET_JAVA_HOME=
if /i "!SET_JAVA_HOME!"=="Y" (
    setx JAVA_HOME "!JAVA_PATH!"
    setx PATH "!PATH!;!JAVA_HOME!\bin"
    echo JAVA_HOME and PATH have been permanently set!
    echo Please restart your command prompt after this script finishes.
)
echo.

:: ===========================
:: Build APK
:: ===========================
echo ===== Building APK =====
echo.
call build_apk.bat

echo.
echo Build process completed.
echo If the build was successful, the APK file is located at:
echo %CD%\app\build\outputs\apk\debug\app-debug.apk
echo.

pause

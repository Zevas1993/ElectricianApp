@echo on
echo Starting APK build process for ElectricianApp...
echo.

:: Check for Java installation
for /f "tokens=*" %%a in ('where /r "C:\Program Files\Java" java.exe 2^>nul') do (
    set JAVA_EXE=%%a
    goto :found_java
)
for /f "tokens=*" %%a in ('where /r "C:\Program Files (x86)\Java" java.exe 2^>nul') do (
    set JAVA_EXE=%%a
    goto :found_java
)

echo Java not found in standard locations. Checking registry...
for /f "tokens=2*" %%a in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit" /s ^| findstr "JavaHome"') do (
    set JAVA_HOME=%%b
    goto :found_java_home
)

echo Java not found. Please install JDK or set JAVA_HOME manually.
goto :end

:found_java
echo Found Java at: %JAVA_EXE%
for %%i in ("%JAVA_EXE%") do set JAVA_BIN_DIR=%%~dpi
for %%i in ("%JAVA_BIN_DIR%..") do set JAVA_HOME=%%~fi
echo Setting JAVA_HOME to: %JAVA_HOME%
goto :build

:found_java_home
echo Found JAVA_HOME in registry: %JAVA_HOME%

:build
echo.
echo ===== Setting up environment =====
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME=%JAVA_HOME%
echo.

echo ===== Cleaning project =====
call gradlew.bat clean

echo.
echo ===== Assembling debug APK =====
call gradlew.bat --info assembleDebug

echo.
echo ===== Building complete =====
echo.

echo APK file should be available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

echo ===== Optional: Install directly to connected device =====
echo To install directly to a connected device, run:
echo gradlew.bat installDebug
echo.

echo ===== Optional: Build release APK =====
echo To build a release APK (requires signing configuration), run:
echo gradlew.bat assembleRelease
echo.

:end
pause

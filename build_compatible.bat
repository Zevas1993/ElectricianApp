@echo on
echo Starting APK build process for ElectricianApp...
echo.

echo ===== Java Version Compatibility Check =====
:: We need Java 11-17 for Gradle 7.5
for /f "tokens=*" %%a in ('where /r "C:\Program Files\Java" java.exe 2^>nul ^| findstr /v "jdk-[2-9][0-9]"') do (
    set JAVA_EXE=%%a
    goto :found_java
)

echo Couldn't find compatible Java version. Attempting to update Gradle wrapper...
call gradlew.bat wrapper --gradle-version 8.7 --warning-mode all

echo ===== Setting up environment =====
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
goto :end

:found_java
echo Found compatible Java at: %JAVA_EXE%
for %%i in ("%JAVA_EXE%") do set JAVA_BIN_DIR=%%~dpi
for %%i in ("%JAVA_BIN_DIR%..") do set JAVA_HOME=%%~fi
echo Setting JAVA_HOME to: %JAVA_HOME%

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

:end
echo ===== Optional: Install directly to connected device =====
echo To install directly to a connected device, run:
echo gradlew.bat installDebug
echo.

echo ===== Optional: Build release APK =====
echo To build a release APK (requires signing configuration), run:
echo gradlew.bat assembleRelease
echo.

pause

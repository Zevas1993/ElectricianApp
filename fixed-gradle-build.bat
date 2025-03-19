@echo off
echo ===================================
echo ElectricianApp Fixed Gradle Build
echo ===================================
echo.

echo Setting JAVA_HOME for this session...
for /f "tokens=*" %%i in ('where java') do set "JAVA_BIN=%%i"
if not defined JAVA_BIN (
    echo Java executable not found in PATH.
    goto :error
)

REM Extract the Java home from the bin path
set "JAVA_BIN=%JAVA_BIN:"=%"
for %%i in ("%JAVA_BIN%") do set "JAVA_HOME_TEMP=%%~dpi"
set "JAVA_HOME_TEMP=%JAVA_HOME_TEMP:~0,-4%"

echo Using Java from: %JAVA_HOME_TEMP%
set "JAVA_HOME=%JAVA_HOME_TEMP%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo Cleaning Gradle caches to ensure dependency updates are applied...
call gradlew.bat --stop
rd /s /q ".gradle" 2>nul
rd /s /q "build" 2>nul
rd /s /q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\androidx.navigation" 2>nul

echo.
echo Running fixed Gradle build...
echo.

call gradlew.bat clean build

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed with error code: %ERRORLEVEL%
    goto :error
)

echo.
echo Build completed successfully!
echo APK should be available in app/build/outputs/apk/
goto :end

:error
echo.
echo Build process encountered an error.
echo.

:end
pause

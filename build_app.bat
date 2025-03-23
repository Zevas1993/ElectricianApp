@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Build Script
echo ===================================
echo.

echo Setting up JDK 17...
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Using JDK: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

echo Cleaning build cache...
call gradlew.bat --stop
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
for /d %%d in (feature\*) do (
    if exist "%%d\build" rmdir /s /q "%%d\build" 2>nul
)
echo.

echo Building the app...
echo This may take several minutes to download dependencies and compile.
echo.

call gradlew.bat clean assembleDebug -Dorg.gradle.java.home="%JDK_PATH%"

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
)

echo.
echo Press any key to exit...
pause > nul

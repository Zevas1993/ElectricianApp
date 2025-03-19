@echo off
setlocal enabledelayedexpansion

echo ===================================
echo ElectricianApp Build Script
echo ===================================
echo.

:: Check if JAVA_HOME is set correctly
if not defined JAVA_HOME (
    echo JAVA_HOME is not set. Running JDK fix script first...
    call fix_jdk.bat
    echo.
    echo Continuing with build process...
) else (
    echo Using JAVA_HOME: %JAVA_HOME%
    if not exist "%JAVA_HOME%\bin\java.exe" (
        echo Warning: JAVA_HOME points to invalid location
        echo Running JDK fix script to correct it...
        call fix_jdk.bat
        echo.
        echo Continuing with build process...
    )
)

:: Clean important Gradle caches to ensure a fresh build
echo.
echo Cleaning Gradle caches...
call gradlew.bat --stop 2>nul
if exist ".gradle\caches\modules-2\files-2.1\androidx.navigation" (
    echo Cleaning navigation cache...
    rd /s /q ".gradle\caches\modules-2\files-2.1\androidx.navigation" 2>nul
)

:: Run the build with clean
echo.
echo Building ElectricianApp...
echo.
call gradlew.bat clean build --stacktrace

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed with error code: %ERRORLEVEL%
    echo.
    echo Possible solutions:
    echo 1. Make sure Java is properly installed and configured
    echo 2. Check Android SDK installation
    echo 3. Review the error messages above for specific issues
    goto :end
) else (
    echo.
    echo Build completed successfully!
    echo.
    echo APK should be available in app/build/outputs/apk/
    
    :: Check if APK was generated
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
        
        :: Copy APK to project root for easy access
        copy "app\build\outputs\apk\debug\app-debug.apk" "electrician-app.apk" >nul
        echo.
        echo For convenience, the APK has been copied to:
        echo electrician-app.apk
    )
)

:end
echo.
echo Build process completed.
pause

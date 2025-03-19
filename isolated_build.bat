@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Isolated Build
echo ===================================
echo.

:: Hard-code the JDK path
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"

:: Verify JDK existence
if not exist "%JDK_PATH%\bin\java.exe" (
    echo ERROR: JDK not found at: %JDK_PATH%
    pause
    exit /b 1
)

:: Create a clean temporary gradle.properties file
echo # Temporary gradle.properties for isolated build > temp_gradle.properties
echo org.gradle.java.home=%JDK_PATH:\=\\% >> temp_gradle.properties
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 >> temp_gradle.properties
echo android.useAndroidX=true >> temp_gradle.properties
echo android.nonTransitiveRClass=true >> temp_gradle.properties
echo android.defaults.buildfeatures.buildconfig=true >> temp_gradle.properties

echo Isolated build config created.
echo.

:: Set environment variables for this session only
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

:: Show config
echo Using JDK: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

:: Navigate to the project directory
cd /d "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

:: Stop any running Gradle daemon
call gradlew.bat --stop
echo.

:: Run the build using the temporary properties file
echo Building project with isolated configuration...
echo.

call gradlew.bat --no-daemon -Dorg.gradle.java.home="%JDK_PATH%" clean assembleDebug --info

set BUILD_RESULT=%ERRORLEVEL%

:: Display result
echo.
if %BUILD_RESULT% EQU 0 (
    echo =============================
    echo BUILD SUCCESSFUL!
    echo =============================
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
        copy "app\build\outputs\apk\debug\app-debug.apk" "C:\Users\Chris Boyd\Desktop\electrician-app.apk" >nul 2>&1
        if %ERRORLEVEL% EQU 0 (
            echo.
            echo For convenience, the APK has been copied to:
            echo C:\Users\Chris Boyd\Desktop\electrician-app.apk
        )
    )
) else (
    echo =============================
    echo BUILD FAILED!
    echo =============================
    echo.
    echo Please refer to JDK_TROUBLESHOOTING.md for more details.
)

:: Clean up
del temp_gradle.properties >nul 2>&1

echo.
echo Press any key to exit...
pause > nul

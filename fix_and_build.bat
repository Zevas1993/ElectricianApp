@echo off
echo ===================================
echo ElectricianApp Fix and Build
echo ===================================
echo.

echo Step 1: Fixing Java Home
echo -----------------------

:: Try common JDK locations
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Amazon Corretto\jdk17.0.9_8"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

echo Could not find Java in common locations.
echo Please install Java JDK 17 and try again.
goto end

:java_found
echo Found JDK at: %JAVA_HOME%
echo Setting system JAVA_HOME...
setx JAVA_HOME "%JAVA_HOME%" /m
echo Setting current session JAVA_HOME...
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo Testing Java installation:
"%JAVA_HOME%\bin\java.exe" -version
echo.

echo Step 2: Stopping any running Gradle daemons
echo ------------------------------------------
call gradlew.bat --stop

echo.
echo Step 3: Building the app
echo ----------------------
call gradlew.bat clean build --stacktrace

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code: %ERRORLEVEL%
) else (
    echo Build completed successfully!
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
        
        copy "app\build\outputs\apk\debug\app-debug.apk" "electrician-app.apk" >nul
        echo.
        echo For convenience, the APK has been copied to:
        echo electrician-app.apk
    )
)

:end
pause

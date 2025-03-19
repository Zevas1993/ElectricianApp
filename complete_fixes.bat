@echo off
echo ===================================
echo ElectricianApp Complete Fixes Script
echo ===================================
echo.

echo Step 1: Setting Java Environment
echo -------------------------------
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
echo Step 2: Stopping any running Gradle daemons
echo ------------------------------------------
call gradlew.bat --stop

echo.
echo Step 3: Cleaning Gradle caches and build files
echo ------------------------------------------
if exist ".gradle" (
    echo Cleaning .gradle directory...
    rd /s /q ".gradle" 2>nul
)
if exist "build" (
    echo Cleaning build directory...
    rd /s /q "build" 2>nul
)
if exist "app\build" (
    echo Cleaning app module build...
    rd /s /q "app\build" 2>nul
)
if exist "domain\build" (
    echo Cleaning domain module build...
    rd /s /q "domain\build" 2>nul
)
if exist "data\build" (
    echo Cleaning data module build...
    rd /s /q "data\build" 2>nul
)
if exist "feature\box\build" (
    echo Cleaning feature:box module build...
    rd /s /q "feature\box\build" 2>nul
)
if exist "feature\conduit\build" (
    echo Cleaning feature:conduit module build...
    rd /s /q "feature\conduit\build" 2>nul
)
if exist "feature\dwelling\build" (
    echo Cleaning feature:dwelling module build...
    rd /s /q "feature\dwelling\build" 2>nul
)

echo.
echo Step 4: Building the app with all fixes applied
echo ---------------------------------------------
echo.
echo Using clean build with detailed logging...
call gradlew.bat clean build --info

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code: %ERRORLEVEL%
    
    echo.
    echo Fixes that have been applied:
    echo ----------------------------
    echo 1. Fixed invalid XML escaping in layout files
    echo 2. Removed deprecated package attribute from AndroidManifest.xml files
    echo 3. Added proper launcher icon resources
    echo 4. Added dependency injection support (Hilt)
    echo 5. Updated Gradle configurations for compatibility
    echo 6. Properly set JAVA_HOME to a valid JDK
    
    echo.
    echo If issues persist, try running with --scan for more information:
    echo   gradlew.bat clean build --scan
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

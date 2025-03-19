@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Direct JDK 17 Build
echo ===================================
echo.

:: Set JDK path directly in the script - no dependency on environment variables
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"

:: Verify JDK existence
if not exist "%JDK_PATH%\bin\java.exe" (
    echo ERROR: JDK not found at: %JDK_PATH%
    echo Please edit this script to set the correct JDK_PATH
    goto :error
)

:: Override environment variables for this session only
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Step 1: JDK 17 Configuration
echo ---------------------------
echo Using JDK from: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to run Java. Path may be incorrect.
    goto :error
)
echo.

:: Navigate to the project directory
cd /d "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

echo Step 2: Setting up Gradle to use JDK 17
echo -------------------------------------
echo org.gradle.java.home=%JDK_PATH% > gradle_local.properties
type gradle.properties >> gradle_local.properties
move /y gradle_local.properties gradle.properties >nul
echo Updated gradle.properties with JDK 17 path
echo.

echo Step 3: Removing any running Gradle daemons
echo ----------------------------------------
call gradlew.bat --stop
echo.

echo Step 4: Cleaning build directories
echo -------------------------------
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Clean failed, but continuing...
)
echo.

echo Step 5: Building with detailed logging
echo ----------------------------------
call gradlew.bat assembleDebug --info
set BUILD_RESULT=%ERRORLEVEL%

echo.
if %BUILD_RESULT% NEQ 0 (
    echo Build failed with error code: %BUILD_RESULT%
    
    echo.
    echo Summary of fixes previously applied:
    echo ----------------------------------
    echo 1. Added missing entity classes (DwellingUnitEntity, ApplianceEntity, DwellingUnitApplianceCrossRef)
    echo 2. Fixed ElectricianDatabase configuration
    echo 3. Updated Gradle build files to include proper Hilt support
    echo 4. Set buildConfig = true to fix deprecation warnings
    
    echo.
    echo Additional troubleshooting steps:
    echo ------------------------------
    echo 1. Try forcing sync with: gradlew.bat --refresh-dependencies
    echo 2. Check all imports in the entity classes
    echo 3. Try with stacktrace: gradlew.bat assembleDebug --stacktrace
    goto :error
) else (
    echo Build completed successfully!
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
        
        copy "app\build\outputs\apk\debug\app-debug.apk" "%PROJECT_DIR%\electrician-app.apk" >nul 2>&1
        if %ERRORLEVEL% EQU 0 (
            echo.
            echo For convenience, the APK has been copied to:
            echo %PROJECT_DIR%\electrician-app.apk
        )
    )
)

goto :end

:error
echo.
echo Build process encountered errors.
echo Please refer to the JDK17_MIGRATION.md file for more information.
exit /b 1

:end
echo.
echo Process complete. Press any key to exit.
pause > nul

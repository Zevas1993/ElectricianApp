@echo off
echo ===================================
echo ElectricianApp Build with JDK 17
echo ===================================
echo.

cd /d "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

echo Step 1: Ensuring JDK 17 is being used
echo --------------------------------------
echo JAVA_HOME=%JAVA_HOME%
java -version
echo.

echo Step 2: Updating gradle.properties to enforce JDK 17
echo -----------------------------------------------------
echo org.gradle.java.home=%JAVA_HOME% >> gradle.properties
echo.

echo Step 3: Cleaning the project
echo ---------------------------
call gradlew.bat clean
echo.

echo Step 4: Assembling the app
echo -------------------------
call gradlew.bat assembleDebug --info
echo.

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code: %ERRORLEVEL%
    
    echo.
    echo Summary of changes applied:
    echo --------------------------
    echo 1. Added missing entity classes (DwellingUnitEntity, ApplianceEntity, DwellingUnitApplianceCrossRef)
    echo 2. Fixed ElectricianDatabase configuration
    echo 3. Updated Gradle build files to include proper Hilt support
    echo 4. Set buildConfig = true to fix deprecation warnings
    echo 5. Set JDK 17 as the project Java environment
    
    echo.
    echo Troubleshooting suggestions:
    echo --------------------------
    echo 1. Run with more detailed logging: gradlew.bat assembleDebug --stacktrace
    echo 2. Check for Hilt annotations in your project that might need fixing
    echo 3. Verify all AndroidManifest.xml files are properly configured
) else (
    echo Build completed successfully!
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
    )
)

pause

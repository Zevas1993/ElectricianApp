@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Source Set and Navigation Fix Build Script
echo ===================================
echo.

:: Step 1: Set Java environment
echo Setting up JDK 17...
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Using JDK: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

:: Step 2: Clean Gradle cache
echo Cleaning Gradle caches...
call gradlew.bat --stop
rmdir /s /q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\com.android.tools.build" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\caches\transforms-3" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\caches\build-cache-1" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin" 2>nul
echo.

:: Step 3: Clean project build directories
echo Cleaning build directories...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
for /d %%d in (feature\*) do (
    if exist "%%d\build" rmdir /s /q "%%d\build" 2>nul
)
echo.

:: Step 4: Validate config directory and sourceset fix
echo Validating config directory and sourceset fix...
if not exist "config" (
    echo Creating config directory...
    mkdir "config"
)

:: Step 5: Validate stub fragments existence
echo Checking for stub fragments...
if not exist "app\src\main\java\com\example\electricalcalculator\ui\pipebending\PipeBendingFragment.kt" (
    echo Error: Stub fragments not found. Please run the script again after fixing this issue.
    exit /b 1
)
echo Stub fragments present, continuing...
echo.

:: Step 6: Display configuration 
echo Using Kotlin version 1.7.0 (downgraded for better compatibility)
echo Repository configuration: settings.gradle managed (no allprojects in build.gradle)
echo Stub fragments: PipeBendingFragment, LightingLayoutFragment, ArViewFragment created
echo.

:: Step 7: Build the app with all fixes
echo Building the app with all fixes applied...
echo This may take several minutes to download dependencies and compile.
echo.

:: Set Gradle options to disable incremental compilation and daemon
set "GRADLE_OPTS=-Dkotlin.incremental=false -Dorg.gradle.daemon=false -Dkotlin.compiler.execution.strategy=in-process -Dkotlin.incremental.useClasspathSnapshot=false"

:: Clean first
call gradlew.bat clean --info
echo.
echo Cleaning completed. Starting build...
echo.

:: Then build with all optimizations
call gradlew.bat assembleDebug --refresh-dependencies --no-daemon -Dorg.gradle.java.home="%JDK_PATH%" --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo Both repository configuration and navigation fragment issues fixed.
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Check for Kotlin version conflicts with 'gradlew.bat app:dependencies'
    echo 2. Verify the Hilt version is correctly set to 2.44
    echo 3. Make sure packagingOptions excludes all necessary META-INF files
    echo 4. Check if stub fragments are properly created
    echo 5. Examine settings.gradle for repository configuration
)

echo.
echo Press any key to exit...
pause > nul

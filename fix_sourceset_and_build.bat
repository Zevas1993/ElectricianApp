@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Source Set and Kotlin Compatibility Fix and Build Script
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

:: Step 5: Display Kotlin version information
echo Using Kotlin version 1.7.0 (downgraded from 1.9.22)

:: Step 6: Build the app with Kotlin compatibility fixes
echo Building the app with enhanced Kotlin compatibility fixes...
echo This may take several minutes to download dependencies and compile.
echo.

:: Set Gradle options to disable incremental compilation and daemon
set "GRADLE_OPTS=-Dkotlin.incremental=false -Dorg.gradle.daemon=false -Dkotlin.compiler.execution.strategy=in-process -Dkotlin.incremental.useClasspathSnapshot=false"

:: Clean and build with refresh dependencies flag
call gradlew.bat clean --info
echo.
echo Cleaning completed. Starting build...
echo.

call gradlew.bat assembleDebug --refresh-dependencies --no-daemon -Dorg.gradle.java.home="%JDK_PATH%" --stacktrace

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
    echo.
    echo Troubleshooting tips:
    echo 1. Check for Kotlin version conflicts with 'gradlew.bat app:dependencies'
    echo 2. Verify the Hilt version is correctly set to 2.44
    echo 3. Make sure packagingOptions excludes all necessary META-INF files
    echo 4. Try running with JDK 17.0.2 if available instead of newer versions
    echo 5. Try running with additional memory: -Dorg.gradle.jvmargs=-Xmx4g
)

echo.
echo Press any key to exit...
pause > nul

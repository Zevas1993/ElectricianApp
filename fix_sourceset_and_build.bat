@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp SourceSet Fix and Build
echo ===================================
echo.

:: Set Java environment
echo Setting up JDK 17...
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Using JDK: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

:: Make config directory if it doesn't exist
echo Checking for config directory...
if not exist "config" (
    echo Creating config directory
    mkdir config
)

:: Create fix_sourceset.gradle file
echo Creating source set fix...
echo // Fix for source set issues in Gradle 8.0+ and AGP 8.0+ > "config/fix_sourceset.gradle"
echo android { >> "config/fix_sourceset.gradle"
echo     sourceSets { >> "config/fix_sourceset.gradle"
echo         main { >> "config/fix_sourceset.gradle"
echo             java.srcDirs = ['src/main/java'] >> "config/fix_sourceset.gradle"
echo         } >> "config/fix_sourceset.gradle"
echo         test { >> "config/fix_sourceset.gradle"
echo             java.srcDirs = ['src/test/java'] >> "config/fix_sourceset.gradle"
echo         } >> "config/fix_sourceset.gradle"
echo         androidTest { >> "config/fix_sourceset.gradle"
echo             java.srcDirs = ['src/androidTest/java'] >> "config/fix_sourceset.gradle"
echo         } >> "config/fix_sourceset.gradle"
echo     } >> "config/fix_sourceset.gradle"
echo } >> "config/fix_sourceset.gradle"
echo. >> "config/fix_sourceset.gradle"
echo // Fix duplicate resources warning >> "config/fix_sourceset.gradle"
echo android.applicationVariants.all { variant -> >> "config/fix_sourceset.gradle"
echo     variant.sourceSets.each { sourceSet -> >> "config/fix_sourceset.gradle"
echo         if (sourceSet.name == "main") { >> "config/fix_sourceset.gradle"
echo             sourceSet.java.srcDirs = ['src/main/java'] >> "config/fix_sourceset.gradle"
echo         } >> "config/fix_sourceset.gradle"
echo     } >> "config/fix_sourceset.gradle"
echo } >> "config/fix_sourceset.gradle"

:: Apply JDK 17 module system fixes
echo Setting JDK module system fixes...
set "JVM_ARGS=--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"

:: Stop the Gradle daemon
echo Stopping Gradle daemons...
call gradlew.bat --stop
timeout /t 2 /nobreak > nul

:: Clean Gradle cache and build directories
echo Cleaning build directories...
rmdir /s /q "build" 2>nul
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
rmdir /s /q "feature\box\build" 2>nul
rmdir /s /q "feature\conduit\build" 2>nul
rmdir /s /q "feature\dwelling\build" 2>nul
echo.

echo Building app with source set fixes and JDK module system fixes...
echo This may take several minutes to download dependencies and compile.
echo.
echo If you see "Terminate batch job (Y/N)?", type N to continue the build.
echo.
echo Press any key to start the build process...
pause > nul

:: Clean first
call gradlew.bat clean --no-daemon
echo.
echo Clean completed. Starting build...
echo.

:: Build with all fixes
echo Building with all fixes applied...
call gradlew.bat assembleDebug --no-daemon -Dkotlin.incremental=false -Dkapt.use.worker.api=false -Dkapt.incremental.apt=false -Dorg.gradle.java.home="%JDK_PATH%" %JVM_ARGS% --stacktrace

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build completed successfully!
    echo ===================================
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo All fixes have been applied and the app built successfully.
    echo - Source set configuration has been fixed
    echo - JDK 17 module system fixes have been applied
    echo - KAPT/KSP configuration has been optimized
) else (
    echo.
    echo ===================================
    echo Build failed. See output above for details.
    echo ===================================
    echo.
    echo Troubleshooting tips:
    echo 1. Try using jdk_module_fix.bat for KAPT-only builds
    echo 2. Try using clean_kapt_and_build.bat to resolve duplicate class issues
    echo 3. Examine the full error message for specific class conflicts
)

echo.
echo Press any key to exit...
pause > nul

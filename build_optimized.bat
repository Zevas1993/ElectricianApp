@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp Build Script with Optimizations
echo =============================================================
echo.

:: Set JAVA_HOME if not already set
if not defined JAVA_HOME (
    echo JAVA_HOME is not set. Attempting to locate Java...
    
    :: Check for JDK installations in Program Files
    for /d %%i in ("C:\Program Files\Java\jdk*") do (
        set "POSSIBLE_JAVA=%%i"
        echo Found Java at !POSSIBLE_JAVA!
        
        set "JAVA_HOME=!POSSIBLE_JAVA!"
        echo Setting JAVA_HOME to: !JAVA_HOME!
        goto :java_found
    )
    
    :: Check for JDK installations in Program Files (x86)
    for /d %%i in ("C:\Program Files (x86)\Java\jdk*") do (
        set "POSSIBLE_JAVA=%%i"
        echo Found Java at !POSSIBLE_JAVA!
        
        set "JAVA_HOME=!POSSIBLE_JAVA!"
        echo Setting JAVA_HOME to: !JAVA_HOME!
        goto :java_found
    )
    
    echo Could not find Java installation. Please set JAVA_HOME manually.
    goto :error
)

:java_found
echo.
echo Using JAVA_HOME: %JAVA_HOME%
echo.

:: Check Java version
echo ===== Java Version Check =====
"%JAVA_HOME%\bin\java" -version
echo.

echo ===== Gradle Version Check =====
call gradlew.bat --version
echo.

echo ===== Cleaning project =====
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo Error during cleaning. Please check for errors above.
    goto :error
)

echo.
echo ===== Assembling optimized debug APK =====
call gradlew.bat --info assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo Error during build. Please check for errors above.
    goto :error
)

echo.
echo ===== Build SUCCESSFUL =====
echo.
echo APK file is available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

echo ===== Installation Instructions =====
echo.
echo To install directly to a connected device, run:
echo.
echo    adb install app\build\outputs\apk\debug\app-debug.apk
echo.
echo If adb is not in your PATH, you can find it at:
echo    [Android SDK location]\platform-tools\adb.exe
echo.

echo ===== Optional: Build release APK =====
echo.
echo To build a release APK with full optimizations, run:
echo.
echo    gradlew.bat assembleRelease
echo.

goto :end

:error
echo.
echo ===== BUILD FAILED =====
echo.

:end
echo.
echo Press any key to exit...
pause > nul

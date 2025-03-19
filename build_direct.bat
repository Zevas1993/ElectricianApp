@echo off
setlocal enabledelayedexpansion

echo =====================================================================
echo ElectricianApp Direct Build Script - Simplified for Java 21 + Gradle 8
echo =====================================================================

:: Skip Gradle daemon to avoid caching issues
set GRADLE_OPTS=-Dorg.gradle.daemon=false

:: Clear any potentially incorrect JAVA_HOME before we begin
set JAVA_HOME=

:: Check for Java
echo Checking Java installation...
java -version >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Java is not available in PATH!
    exit /b 1
)

:: Display Java version
java -version
echo.

:: Use a more reliable method to find JAVA_HOME
for /f "tokens=*" %%i in ('where java') do (
    set "JAVA_EXE=%%i"
    goto :found_java
)

:found_java
echo Found Java executable at: !JAVA_EXE!

:: Parse the path to extract JAVA_HOME
set "JAVA_BIN_DIR=!JAVA_EXE:~0,-9!"
set "JAVA_HOME=!JAVA_BIN_DIR:~0,-4!"

:: Verify JAVA_HOME looks valid
if not exist "!JAVA_HOME!\bin\java.exe" (
    :: Try an alternative approach
    for /f "tokens=*" %%j in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr "java.home"') do (
        set "JAVA_HOME_LINE=%%j"
        set "JAVA_HOME=!JAVA_HOME_LINE:*= !"
        goto :java_home_found
    )
)

:java_home_found
echo Using JAVA_HOME: !JAVA_HOME!

:: Verify JAVA_HOME is usable
if not exist "!JAVA_HOME!" (
    echo WARNING: Detected JAVA_HOME path doesn't exist
    echo Continuing without setting JAVA_HOME...
    set "JAVA_HOME="
)

:: Add Gradle parameters for better debugging
set PARAMS=--no-daemon --stacktrace --info

echo.
echo === STEP 1: Cleaning build directory ===
rmdir /s /q "app\build" 2>nul
mkdir "app\build\outputs\apk\debug" 2>nul

echo.
echo === STEP 2: Running Clean Task ===
call gradlew.bat %PARAMS% clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean task failed
    goto :error
)

echo.
echo === STEP 3: Building Debug APK with only app module ===
call gradlew.bat %PARAMS% :app:assembleDebug -x lint -x test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build task failed
    goto :error
)

echo.
echo === STEP 4: Verifying Build Result ===
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo WARNING: APK not found at expected location
    
    :: Try finding it elsewhere
    for /r %%i in (*.apk) do (
        echo Found APK at: %%i
        copy "%%i" "app-debug.apk" >nul 2>&1
        if exist "app-debug.apk" (
            echo Copied APK to project root directory
            goto :success
        )
    )
    
    goto :error
) else (
    copy "app\build\outputs\apk\debug\app-debug.apk" "app-debug.apk" >nul 2>&1
    echo Successfully copied APK to project root directory
    goto :success
)

:error
echo.
echo =====================================================================
echo BUILD FAILED - See error messages above
echo =====================================================================
exit /b 1

:success
echo.
echo =====================================================================
echo BUILD SUCCESSFUL - APK created successfully
echo =====================================================================
echo.
echo APK location: %CD%\app-debug.apk
echo.
echo To install to a connected device:
echo adb install app-debug.apk
echo.
pause
exit /b 0

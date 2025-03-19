@echo off
setlocal enabledelayedexpansion

echo ===================================
echo Fixed JDK Configuration for Gradle
echo ===================================
echo.

:: Define potential JDK locations with proper quoting
set "JAVA_LOCATIONS=C:\Program Files\Java C:\Program Files (x86)\Java C:\Program Files\Eclipse Adoptium C:\Program Files\Amazon Corretto"

:: Variable to store found JDK path
set "FOUND_JDK="

:: Search for JDK in common locations
for %%L in (%JAVA_LOCATIONS%) do (
    if exist "%%L" (
        echo Checking "%%L" for JDK installations...
        
        :: Look for directories with 'jdk' in the name
        for /d %%J in ("%%L\jdk*") do (
            if exist "%%J\bin\java.exe" (
                echo Found JDK: "%%J"
                set "FOUND_JDK=%%J"
                goto :configure_jdk
            )
        )
    )
)

:: If we haven't found a JDK yet, try some specific paths
for %%P in (
    "C:\Program Files\Java\jdk-17"
    "C:\Program Files\Java\jdk-21"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
) do (
    if exist "%%~P\bin\java.exe" (
        echo Found JDK at %%~P
        set "FOUND_JDK=%%~P"
        goto :configure_jdk
    )
)

:configure_jdk
if not defined FOUND_JDK (
    echo No JDK installation found.
    echo Please install Java JDK manually.
    goto :end
)

echo.
echo Setting JAVA_HOME to: "!FOUND_JDK!"
setx JAVA_HOME "!FOUND_JDK!" /m
echo JAVA_HOME environment variable set.

echo.
echo Updating Gradle config properties...
if not exist "gradle" mkdir gradle

:: Create the config.properties file with proper quoting
(
    echo # Gradle JDK Configuration
    echo # This file specifies the JDK to use for Gradle builds
    echo.
    echo # Updated by fix_jdk.bat
    echo org.gradle.java.home=!FOUND_JDK!
) > "gradle\config.properties"

echo Updated gradle/config.properties with Java path.

:: Set for current session
set "JAVA_HOME=!FOUND_JDK!"
set "PATH=!FOUND_JDK!\bin;%PATH%"

echo.
echo Testing Java installation:
"!FOUND_JDK!\bin\java.exe" -version
echo.

echo Now running a quick Gradle clean to test configuration:
call gradlew.bat clean --quiet

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Gradle clean task failed. Please check the error messages above.
) else (
    echo.
    echo Gradle configuration verified successfully!
    echo You can now build the ElectricianApp using the fixed-gradle-build.bat script.
)

:end
pause

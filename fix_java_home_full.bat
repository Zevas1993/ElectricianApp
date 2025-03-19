@echo off
echo ===================================
echo Complete JAVA_HOME Fix for ElectricianApp
echo ===================================
echo.

REM Check common Java installation locations
set "JAVA_LOCATIONS=C:\Program Files\Java C:\Program Files (x86)\Java C:\Program Files\Eclipse Adoptium C:\Program Files\Amazon Corretto"

set "JAVA_HOME="
for %%L in (%JAVA_LOCATIONS%) do (
    if exist "%%L" (
        echo Checking %%L for JDK installations...
        for /d %%J in ("%%L\jdk*") do (
            if exist "%%J\bin\java.exe" (
                if exist "%%J\bin\javac.exe" (
                    set "JAVA_HOME=%%J"
                    echo Found potential JDK: %%J
                )
            )
        )
        for /d %%J in ("%%L\*jdk*") do (
            if exist "%%J\bin\java.exe" (
                if exist "%%J\bin\javac.exe" (
                    set "JAVA_HOME=%%J"
                    echo Found potential JDK: %%J
                )
            )
        )
    )
)

if not defined JAVA_HOME (
    echo No JDK installation found in common locations.
    echo.
    echo You may need to manually install Java JDK 17.
    goto :error
)

echo.
echo Setting JAVA_HOME to: %JAVA_HOME%
setx JAVA_HOME "%JAVA_HOME%" /m
echo JAVA_HOME environment variable set.

echo.
echo Updating Gradle config properties...
if not exist "gradle" mkdir gradle

REM Update gradle/config.properties with actual Java path
echo # Gradle JDK Configuration> gradle\config.properties
echo # This file specifies the JDK to use for Gradle builds>> gradle\config.properties
echo.>> gradle\config.properties
echo # Updated automatically by fix_java_home_full.bat>> gradle\config.properties
echo org.gradle.java.home=%JAVA_HOME%>> gradle\config.properties

echo Updated gradle/config.properties with Java path.

echo.
echo Please restart your command prompt for changes to take effect.
echo.

REM Add a temporary local environment variable for the current session
set "JAVA_HOME=%JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Testing Java installation:
"%JAVA_HOME%\bin\java.exe" -version
echo.

echo Running a quick Gradle clean to verify configuration:
call gradlew.bat clean --info
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Gradle clean task failed. Configuration may still need adjustment.
) else (
    echo.
    echo Gradle configuration verified successfully!
)

echo.
echo You can now build the ElectricianApp using the fixed-gradle-build.bat script.
echo.
goto :end

:error
echo.
echo Failed to configure Java environment.
echo.

:end
pause

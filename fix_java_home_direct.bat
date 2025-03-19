@echo off
echo ===================================
echo Direct JAVA_HOME Fix for ElectricianApp
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
echo Please restart your command prompt for changes to take effect.
echo.

REM Add a temporary local environment variable for the current session
set "JAVA_HOME=%JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Testing Java installation:
"%JAVA_HOME%\bin\java.exe" -version
echo.

echo You can now build the ElectricianApp using the batch scripts.
echo.
goto :end

:error
echo.
echo Failed to configure Java environment.
echo.

:end
pause

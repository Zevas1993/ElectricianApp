@echo off
echo ===================================
echo Simple JDK Fix for ElectricianApp
echo ===================================
echo.

echo Detecting Java installations...

:: Try to find Java from common locations
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\java.exe" goto found_java

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if exist "%JAVA_HOME%\bin\java.exe" goto found_java

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto found_java

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto found_java

set "JAVA_HOME=C:\Program Files\Amazon Corretto\jdk17.0.9_8"
if exist "%JAVA_HOME%\bin\java.exe" goto found_java

:: If we get here, we didn't find Java in common locations
echo Could not find Java in common locations.
echo Please install Java JDK 17 and try again.
goto end

:found_java
echo Found JDK at: %JAVA_HOME%
echo.

echo Setting system JAVA_HOME...
setx JAVA_HOME "%JAVA_HOME%" /m

echo Setting current session JAVA_HOME...
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo Testing Java installation:
"%JAVA_HOME%\bin\java.exe" -version
echo.

:: Create gradle.properties file
echo Creating Gradle JDK configuration...
if not exist "gradle" mkdir gradle

echo # Gradle JDK Configuration> gradle\config.properties
echo # This file specifies the JDK to use for Gradle builds>> gradle\config.properties
echo.>> gradle\config.properties
echo # Updated by simple_fix.bat>> gradle\config.properties
echo org.gradle.java.home=%JAVA_HOME%>> gradle\config.properties

echo.
echo Java and Gradle configuration completed successfully.
echo.
echo You can now run gradlew build or use build_app.bat to build the project.

:end
pause

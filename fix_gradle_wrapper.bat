@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo Fix Gradle Wrapper for Android Studio
echo =============================================================
echo.

set WRAPPER_DIR=gradle\wrapper
set DEST_DIR=%~dp0%WRAPPER_DIR%

echo Checking Gradle wrapper directory...
if not exist "%DEST_DIR%" (
    echo Creating wrapper directory...
    mkdir "%DEST_DIR%"
)

echo.
echo Downloading Gradle wrapper files...

:: The URL for the Gradle distribution version you need
set GRADLE_VERSION=6.9.3
set WRAPPER_JAR_URL=https://github.com/gradle/gradle/raw/v%GRADLE_VERSION%/gradle/wrapper/gradle-wrapper.jar
set WRAPPER_PROPS_URL=https://raw.githubusercontent.com/gradle/gradle/v%GRADLE_VERSION%/gradle/wrapper/gradle-wrapper.properties

:: Download the wrapper JAR
echo Downloading gradle-wrapper.jar...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%WRAPPER_JAR_URL%' -OutFile '%DEST_DIR%\gradle-wrapper.jar'"

if not exist "%DEST_DIR%\gradle-wrapper.jar" (
    echo Failed to download gradle-wrapper.jar.
    echo Check your internet connection and try again.
    goto :error
)

:: Create wrapper properties file
echo Creating gradle-wrapper.properties file...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-6.9.3-bin.zip
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "%DEST_DIR%\gradle-wrapper.properties"

:: Check for batch files too
if not exist "gradlew.bat" (
    echo Creating gradlew.bat...
    (
    echo @rem
    echo @rem Copyright 2015 the original author or authors.
    echo @rem
    echo @rem Licensed under the Apache License, Version 2.0 ^(the "License"^);
    echo @rem you may not use this file except in compliance with the License.
    echo @rem You may obtain a copy of the License at
    echo @rem
    echo @rem      https://www.apache.org/licenses/LICENSE-2.0
    echo @rem
    echo @rem Unless required by applicable law or agreed to in writing, software
    echo @rem distributed under the License is distributed on an "AS IS" BASIS,
    echo @rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    echo @rem See the License for the specific language governing permissions and
    echo @rem limitations under the License.
    echo @rem
    echo.
    echo @if "%%DEBUG%%" == "" @echo off
    echo @rem ##########################################################################
    echo @rem
    echo @rem  Gradle startup script for Windows
    echo @rem
    echo @rem ##########################################################################
    echo.
    echo @rem Set local scope for the variables with windows NT shell
    echo if "%%OS%%"=="Windows_NT" setlocal
    echo.
    echo set DIRNAME=%%~dp0
    echo if "%%DIRNAME%%" == "" set DIRNAME=.
    echo set APP_BASE_NAME=%%~n0
    echo set APP_HOME=%%DIRNAME%%
    echo.
    echo @rem Resolve any "." and ".." in APP_HOME to make it shorter.
    echo for %%%%i in ^("%%APP_HOME%%"^) do set APP_HOME=%%%%~fi
    echo.
    echo @rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
    echo set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
    echo.
    echo @rem Find java.exe
    echo if defined JAVA_HOME goto findJavaFromJavaHome
    echo.
    echo set JAVA_EXE=java.exe
    echo %% JAVA_HOME not found in your environment. %%
    echo set JAVA_HOME=%%JAVA_HOME:"=%%
    echo.
    echo if not "%%JAVA_HOME%%" == "" ^(
    echo echo.
    echo echo Error: JAVA_HOME is set to an invalid directory: %%JAVA_HOME%%
    echo echo.
    echo echo Please set the JAVA_HOME variable in your environment to match the
    echo echo location of your Java installation.
    echo echo.
    echo echo Alternatively, try setting JAVA_HOME to a JDK 11 installation.
    echo echo.
    echo goto fail
    echo ^)
    echo.
    echo goto execute
    echo.
    echo :findJavaFromJavaHome
    echo set JAVA_HOME=%%JAVA_HOME:"=%%
    echo set JAVA_EXE=%%JAVA_HOME%%/bin/java.exe
    echo.
    echo if exist "%%JAVA_EXE%%" goto execute
    echo.
    echo echo.
    echo echo Error: JAVA_HOME is set to an invalid directory: %%JAVA_HOME%%
    echo echo.
    echo echo Please set the JAVA_HOME variable in your environment to match the
    echo echo location of your Java installation.
    echo echo.
    echo goto fail
    echo.
    echo :execute
    echo @rem Setup the command line
    echo.
    echo set CLASSPATH=%%APP_HOME%%\gradle\wrapper\gradle-wrapper.jar
    echo.
    echo.
    echo @rem Execute Gradle
    echo "%%JAVA_EXE%%" %%DEFAULT_JVM_OPTS%% %%JAVA_OPTS%% %%GRADLE_OPTS%% "-Dorg.gradle.appname=%%APP_BASE_NAME%%" -classpath "%%CLASSPATH%%" org.gradle.wrapper.GradleWrapperMain %%*
    echo.
    echo :end
    echo @rem End local scope for the variables with windows NT shell
    echo if "%%ERRORLEVEL%%"=="0" goto mainEnd
    echo.
    echo :fail
    echo rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
    echo rem the _cmd.exe /c_ return code!
    echo if not "" == "%%GRADLE_EXIT_CONSOLE%%" exit 1
    echo exit /b 1
    echo.
    echo :mainEnd
    echo if "%%OS%%"=="Windows_NT" endlocal
    echo.
    echo :omega
    ) > gradlew.bat
)

:: Test the Gradle wrapper
echo.
echo Testing Gradle wrapper...
call gradlew.bat --version

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Gradle wrapper test failed!
    echo Please check the error message above.
    goto :error
)

echo.
echo =============================================================
echo Gradle wrapper fixed successfully!
echo =============================================================
echo.
echo You should now be able to open the project in Android Studio.
echo.
echo If Android Studio still shows errors:
echo 1. Try the "File -^> Invalidate Caches / Restart..." option
echo 2. Close and reopen the project
echo 3. Try using a JDK 11 installation for Android Studio
echo.
goto :end

:error
echo.
echo =============================================================
echo Fix failed!
echo =============================================================
echo.
echo Please check the error messages above.
echo.

:end
echo.
echo Press any key to exit...
pause

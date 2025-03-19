@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp All-In-One Build Script
echo =============================================================
echo This script sets up everything and builds the APK in one step.
echo.

:: Find JDK installations
echo Looking for JDK installations...
set "FOUND_JDK="

:: Check common JDK locations
set "JDK_LOCATIONS=C:\Program Files\Java C:\Program Files (x86)\Java C:\Java C:\Program Files\Eclipse Adoptium C:\Program Files (x86)\Eclipse Adoptium"
for %%L in (%JDK_LOCATIONS%) do (
    if exist "%%L" (
        for /d %%J in ("%%L\jdk*") do (
            if exist "%%J\bin\javac.exe" (
                set "FOUND_JDK=%%J"
                echo Found JDK: !FOUND_JDK!
                goto :jdk_found
            )
        )
    )
)

:jdk_found
if not defined FOUND_JDK (
    echo No JDK found in common locations.
    echo Using current JAVA_HOME if available: %JAVA_HOME%
    
    if not defined JAVA_HOME (
        echo ERROR: No JDK found and JAVA_HOME is not set.
        echo Please install JDK 11 or 17 or set JAVA_HOME manually.
        goto :error
    )
    
    if not exist "%JAVA_HOME%\bin\javac.exe" (
        echo ERROR: JAVA_HOME is set but javac.exe not found.
        echo Please install JDK 11 or 17 or set JAVA_HOME correctly.
        goto :error
    )
    
    set "FOUND_JDK=%JAVA_HOME%"
)

:: Set JAVA_HOME for current session
echo.
echo Setting up environment for current session...
set "JAVA_HOME=%FOUND_JDK%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME: %JAVA_HOME%

:: Verify Java version
echo.
echo Checking Java version...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to run Java. Check installation.
    goto :error
)

:: Create directory for Gradle wrapper if it doesn't exist
if not exist "gradle\wrapper" (
    echo.
    echo Creating Gradle wrapper directory...
    mkdir "gradle\wrapper" 2>nul
)

:: Create Gradle wrapper properties with compatible version
echo.
echo Setting up Gradle wrapper...
(
    echo distributionBase=GRADLE_USER_HOME
    echo distributionPath=wrapper/dists
    echo distributionUrl=https\://services.gradle.org/distributions/gradle-6.9.3-bin.zip
    echo zipStoreBase=GRADLE_USER_HOME
    echo zipStorePath=wrapper/dists
) > "gradle\wrapper\gradle-wrapper.properties"

:: Download Gradle wrapper JAR if needed
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading Gradle wrapper JAR...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v6.9/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        echo Error downloading Gradle wrapper JAR.
        echo Creating a placeholder instead...
        copy NUL "gradle\wrapper\gradle-wrapper.jar" >nul
    )
)

:: Create gradlew.bat if it doesn't exist
if not exist "gradlew.bat" (
    echo Creating gradlew.bat...
    (
        echo @rem
        echo @rem Gradle startup script for Windows
        echo @rem
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
        echo @rem Add default JVM options here. You can also use JAVA_OPTS to pass JVM options to this script.
        echo set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
        echo.
        echo @rem Find java.exe
        echo if defined JAVA_HOME goto findJavaFromJavaHome
        echo.
        echo set JAVA_EXE=java.exe
        echo %% JAVA_HOME not found in your environment. %%
        echo.
        echo goto execute
        echo.
        echo :findJavaFromJavaHome
        echo set JAVA_HOME=%%JAVA_HOME:"=%%
        echo set JAVA_EXE=%%JAVA_HOME%%/bin/java.exe
        echo.
        echo if exist "%%JAVA_EXE%%" goto execute
        echo.
        echo echo ERROR: JAVA_HOME is set to an invalid directory: %%JAVA_HOME%%
        echo echo.
        echo goto fail
        echo.
        echo :execute
        echo @rem Setup the command line
        echo.
        echo set CLASSPATH=%%APP_HOME%%\gradle\wrapper\gradle-wrapper.jar
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
    ) > "gradlew.bat"
)

:: Backup important files
echo.
echo Creating backups of build files...
set BACKUP_DIR=backup_%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_DIR=%BACKUP_DIR: =0%
mkdir "%BACKUP_DIR%" 2>nul

if exist "app\build.gradle" copy "app\build.gradle" "%BACKUP_DIR%\app-build.gradle" >nul
if exist "build.gradle" copy "build.gradle" "%BACKUP_DIR%\build.gradle" >nul
if exist "settings.gradle" copy "settings.gradle" "%BACKUP_DIR%\settings.gradle" >nul
if exist "gradle.properties" copy "gradle.properties" "%BACKUP_DIR%\gradle.properties" >nul

:: Create gradle.properties with optimal settings
echo.
echo Setting up gradle.properties with optimal settings...
(
    echo # Optimized Gradle properties for ElectricianApp
    echo org.gradle.jvmargs=-Xmx1536m -Dfile.encoding=UTF-8
    echo android.useAndroidX=true
    echo android.enableJetifier=true
    echo org.gradle.configureondemand=true
    echo org.gradle.parallel=true
    echo org.gradle.caching=true
    echo kotlin.stdlib.default.dependency=false
) > "gradle.properties"

:: Update settings.gradle if needed
echo.
echo Updating settings.gradle...
(
    echo pluginManagement {
    echo     repositories {
    echo         google()
    echo         mavenCentral()
    echo         gradlePluginPortal()
    echo     }
    echo }
    echo.
    echo dependencyResolutionManagement {
    echo     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    echo     repositories {
    echo         google()
    echo         mavenCentral()
    echo     }
    echo }
    echo.
    echo rootProject.name = "ElectricianApp"
    echo include ':app'
    echo include ':data'
    echo include ':domain'
    echo include ':feature:box'
    echo include ':feature:conduit'
    echo include ':feature:dwelling'
) > "settings.gradle"

:: Run clean first to avoid potential issues
echo.
echo Cleaning project...
call gradlew.bat clean --no-daemon --info
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Clean operation failed, but continuing with build...
)

:: Build the debug APK
echo.
echo =============================================================
echo Building debug APK...
echo =============================================================
echo.
call gradlew.bat assembleDebug --no-daemon --info --stacktrace
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed. See output above for details.
    echo.
    goto :error
)

:: Check if APK was created
echo.
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo =============================================================
    echo BUILD SUCCESSFUL!
    echo =============================================================
    echo.
    echo APK file is available at:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on a connected device:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    goto :end
) else (
    echo ERROR: APK file not found after build.
    goto :error
)

:error
echo.
echo =============================================================
echo BUILD FAILED
echo =============================================================
echo.
echo Troubleshooting steps:
echo 1. Install JDK 11 or 17 if you don't have it
echo 2. Open the project in Android Studio
echo 3. Check detailed error messages in the build output
echo.

:end
echo.
echo Press any key to exit...
pause

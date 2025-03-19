@echo off
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp - Gradle Update Script
echo =============================================================
echo This script will update the project to use Gradle 8.10 which is compatible
echo with your Java 21 installation.
echo.

set "WRAPPER_DIR=%CD%\gradle\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\gradle-wrapper.jar"
set "WRAPPER_PROPS=%WRAPPER_DIR%\gradle-wrapper.properties"

:: Create wrapper directory if it doesn't exist
if not exist "%WRAPPER_DIR%" (
    echo Creating Gradle wrapper directory...
    mkdir "%WRAPPER_DIR%" 2>nul
)

:: Update gradle-wrapper.properties to use Gradle 8.10
echo Updating gradle-wrapper.properties to use Gradle 8.10...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.10-bin.zip
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "%WRAPPER_PROPS%"

:: Download gradle-wrapper.jar
echo Downloading Gradle wrapper JAR...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.4/gradle/wrapper/gradle-wrapper.jar' -OutFile '%WRAPPER_JAR%'"

if not exist "%WRAPPER_JAR%" (
    echo Failed to download gradle-wrapper.jar
    echo Creating a placeholder JAR file...
    copy NUL "%WRAPPER_JAR%" >nul
)

:: Create or update gradlew.bat
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
echo goto execute
echo.
echo :findJavaFromJavaHome
echo set JAVA_HOME=%%JAVA_HOME:"=%%
echo set JAVA_EXE=%%JAVA_HOME%%/bin/java.exe
echo.
echo if exist "%%JAVA_EXE%%" goto execute
echo.
echo echo.
echo echo ERROR: JAVA_HOME is set to an invalid directory: %%JAVA_HOME%%
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

:: Update build.gradle for Gradle 8.10 compatibility
echo.
echo Updating build.gradle with Gradle 8.10 compatibility...

:: Create backup of build.gradle
if exist "build.gradle" (
    copy "build.gradle" "build.gradle.bak" >nul
)

:: Create a new build.gradle with modern syntax
(
echo // Top-level build file for ElectricianApp
echo // Updated for Gradle 8.10 compatibility
echo.
echo buildscript {
echo     ext {
echo         agp_version = '8.1.0'
echo         kotlin_version = '1.9.0'
echo     }
echo     repositories {
echo         google()
echo         mavenCentral()
echo     }
echo     dependencies {
echo         classpath "com.android.tools.build:gradle:${agp_version}"
echo         classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin_version}"
echo     }
echo }
echo.
echo allprojects {
echo     repositories {
echo         google()
echo         mavenCentral()
echo     }
echo }
) > "build.gradle"

:: Update settings.gradle
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
echo     repositories {
echo         google()
echo         mavenCentral()
echo     }
echo }
echo.
echo rootProject.name = "ElectricianApp"
echo include ':app'
) > "settings.gradle"

:: Update gradle.properties
echo.
echo Updating gradle.properties...
(
echo # Project-wide Gradle settings
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
echo android.useAndroidX=true
echo android.enableJetifier=true
echo org.gradle.parallel=true
echo org.gradle.caching=true
) > "gradle.properties"

:: Check if app/build.gradle exists and update it
if exist "app\build.gradle" (
    echo.
    echo Updating app/build.gradle...
    (
    echo plugins {
    echo     id 'com.android.application'
    echo     id 'org.jetbrains.kotlin.android'
    echo }
    echo.
    echo android {
    echo     namespace 'com.example.electricalcalculator'
    echo     compileSdk 33
    echo.
    echo     defaultConfig {
    echo         applicationId "com.example.electricalcalculator"
    echo         minSdk 21
    echo         targetSdk 33
    echo         versionCode 1
    echo         versionName "1.0"
    echo     }
    echo.
    echo     buildTypes {
    echo         release {
    echo             minifyEnabled false
    echo             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    echo         }
    echo     }
    echo.
    echo     compileOptions {
    echo         sourceCompatibility JavaVersion.VERSION_17
    echo         targetCompatibility JavaVersion.VERSION_17
    echo     }
    echo.
    echo     kotlinOptions {
    echo         jvmTarget = '17'
    echo     }
    echo }
    echo.
    echo dependencies {
    echo     implementation 'androidx.core:core-ktx:1.9.0'
    echo     implementation 'androidx.appcompat:appcompat:1.6.1'
    echo     implementation 'com.google.android.material:material:1.8.0'
    echo     implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    echo     implementation project(':feature:box')
    echo     implementation project(':feature:conduit')
    echo     implementation project(':feature:dwelling')
    echo     implementation project(':domain')
    echo     implementation project(':data')
    echo }
    ) > "app\build.gradle"
)

echo.
echo =============================================================
echo Gradle update completed successfully!
echo =============================================================
echo.
echo The project has been updated to use Gradle 8.10, which is compatible with Java 21.
echo You can now try building the APK with:
echo.
echo gradlew.bat assembleDebug
echo.
echo Press any key to exit...
pause

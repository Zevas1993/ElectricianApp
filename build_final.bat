@echo off
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp Final Build Script
echo =============================================================
echo This script sets up everything and builds the APK in one step.
echo.

:: First check current JAVA_HOME
echo Checking current JAVA_HOME setting...
if not defined JAVA_HOME (
    echo JAVA_HOME is not set.
) else (
    echo Current JAVA_HOME: %JAVA_HOME%
    if exist "%JAVA_HOME%\bin\javac.exe" (
        echo JAVA_HOME appears to be valid.
    ) else (
        echo JAVA_HOME is set but does not point to a valid JDK.
    )
)

:: Check if java is in PATH
echo.
echo Checking for Java in PATH...
where java >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Java found in PATH.
) else (
    echo Java not found in PATH.
)

:: Set up environment even if JAVA_HOME is already set
echo.
echo Setting up compilation environment...

:: Try to auto-detect JDK
set "JDK_PATH="

:: Common JDK locations - checked one by one to avoid spacing issues
if exist "C:\Program Files\Java" (
    for /d %%j in ("C:\Program Files\Java\jdk*") do (
        if exist "%%j\bin\javac.exe" (
            set "JDK_PATH=%%j"
            echo Found JDK: !JDK_PATH!
            goto :jdk_found
        )
    )
)

if exist "C:\Program Files (x86)\Java" (
    for /d %%j in ("C:\Program Files (x86)\Java\jdk*") do (
        if exist "%%j\bin\javac.exe" (
            set "JDK_PATH=%%j"
            echo Found JDK: !JDK_PATH!
            goto :jdk_found
        )
    )
)

:: Continue with more locations if needed
if exist "C:\Program Files\Eclipse Adoptium" (
    for /d %%j in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
        if exist "%%j\bin\javac.exe" (
            set "JDK_PATH=%%j"
            echo Found JDK: !JDK_PATH!
            goto :jdk_found
        )
    )
)

:jdk_found
:: If no JDK found, use current JAVA_HOME if valid
if not defined JDK_PATH (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "JDK_PATH=%JAVA_HOME%"
            echo Using current JAVA_HOME as JDK: %JDK_PATH%
        )
    )
)

:: Verify we have a JDK now
if not defined JDK_PATH (
    echo No JDK found! Please install JDK 11 or 17.
    goto :error
)

:: Use the detected/specified JDK for this session
echo.
echo Using JDK: %JDK_PATH%
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

:: Test Java version
echo.
echo Testing Java...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo Failed to run Java! Check JDK installation.
    goto :error
)

:: Set up Gradle wrapper
echo.
echo Setting up Gradle wrapper...

:: Ensure wrapper directory exists
if not exist "gradle\wrapper" (
    mkdir "gradle\wrapper" 2>nul
)

:: Write gradle-wrapper.properties
echo Writing gradle-wrapper.properties...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-6.9.3-bin.zip
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > "gradle\wrapper\gradle-wrapper.properties"

:: Create wrapper JAR if it doesn't exist
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper JAR not found.
    echo Creating a placeholder file. This will need to be replaced with a real wrapper JAR.
    copy NUL "gradle\wrapper\gradle-wrapper.jar" >nul
)

:: Generate very simple build files to produce an APK
echo.
echo Setting up build environment...

:: Backup original files
echo Creating backups...
set "BACKUP_DIR=backup_%date:~10,4%%date:~4,2%%date:~7,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "BACKUP_DIR=%BACKUP_DIR: =0%"
mkdir "%BACKUP_DIR%" 2>nul

if exist "build.gradle" copy "build.gradle" "%BACKUP_DIR%\" >nul
if exist "settings.gradle" copy "settings.gradle" "%BACKUP_DIR%\" >nul
if exist "gradle.properties" copy "gradle.properties" "%BACKUP_DIR%\" >nul

:: Update build.gradle (only core components)
echo Updating build.gradle...
(
echo // Top-level build file
echo buildscript {
echo     ext {
echo         agp_version = '4.2.2'
echo         kotlin_version = '1.6.21'
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

:: Create simple settings.gradle
echo Updating settings.gradle...
(
echo include ':app'
) > "settings.gradle"

:: Update gradle.properties
echo Updating gradle.properties...
(
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
echo android.useAndroidX=true
echo org.gradle.parallel=true
echo org.gradle.caching=true
) > "gradle.properties"

:: Create simple app/build.gradle if it doesn't exist
if not exist "app\build.gradle" (
    echo app/build.gradle missing, creating a simplified version...
    (
    echo apply plugin: 'com.android.application'
    echo apply plugin: 'kotlin-android'
    echo.
    echo android {
    echo     compileSdkVersion 31
    echo     defaultConfig {
    echo         applicationId "com.example.electricalcalculator"
    echo         minSdkVersion 21
    echo         targetSdkVersion 31
    echo         versionCode 1
    echo         versionName "1.0"
    echo     }
    echo     buildTypes {
    echo         release {
    echo             minifyEnabled false
    echo             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    echo         }
    echo     }
    echo     compileOptions {
    echo         sourceCompatibility JavaVersion.VERSION_1_8
    echo         targetCompatibility JavaVersion.VERSION_1_8
    echo     }
    echo     kotlinOptions {
    echo         jvmTarget = '1.8'
    echo     }
    echo }
    echo.
    echo dependencies {
    echo     implementation 'androidx.core:core-ktx:1.7.0'
    echo     implementation 'androidx.appcompat:appcompat:1.4.1'
    echo     implementation 'com.google.android.material:material:1.5.0'
    echo     implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    echo }
    ) > "app\build.gradle"
)

:: Try to build
echo.
echo =============================================================
echo Building APK...
echo =============================================================

echo.
echo Step 1: Clean project...
call gradlew.bat clean --stacktrace --info
if %ERRORLEVEL% NEQ 0 (
    echo Clean failed but continuing with build...
)

echo.
echo Step 2: Build debug APK...
call gradlew.bat assembleDebug --stacktrace --info
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! See errors above.
    goto :error
)

:: Check if APK was created
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo =============================================================
    echo BUILD SUCCESSFUL!
    echo =============================================================
    echo.
    echo Debug APK created at:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on a device:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    goto :success
) else (
    echo.
    echo Build completed but APK not found in expected location.
    echo.
    goto :error
)

:error
echo.
echo =============================================================
echo BUILD PROCESS FAILED
echo =============================================================
echo.
echo Troubleshooting steps:
echo 1. Ensure you have JDK 11 or 17 installed
echo 2. Check that Android SDK is properly installed
echo 3. Try building with Android Studio
echo.
goto :end

:success
echo.
echo Process completed successfully.

:end
echo.
echo Press any key to exit...
pause

@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp Basic APK Creator
echo =============================================================
echo.

echo This script will create a simple APK, avoiding compatibility issues.
echo.

:: Create a backup directory
set BACKUP_DIR=%~dp0backup_%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_DIR=%BACKUP_DIR: =0%
mkdir "%BACKUP_DIR%"

echo Creating backups in: %BACKUP_DIR%
echo.

:: Backup important files
copy "gradle\wrapper\gradle-wrapper.properties" "%BACKUP_DIR%\gradle-wrapper.properties"
copy "app\build.gradle" "%BACKUP_DIR%\app-build.gradle"
copy "build.gradle" "%BACKUP_DIR%\build.gradle"
copy "settings.gradle" "%BACKUP_DIR%\settings.gradle"

echo.
echo ===== Creating simplified build files =====
echo.

:: Create simple top-level build.gradle
echo // Top-level build file > build.gradle
echo buildscript {>> build.gradle
echo     repositories {>> build.gradle
echo         google()>> build.gradle
echo         mavenCentral()>> build.gradle
echo     }>> build.gradle
echo     dependencies {>> build.gradle
echo         classpath 'com.android.tools.build:gradle:4.2.2'>> build.gradle
echo         classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31">> build.gradle
echo     }>> build.gradle
echo }>> build.gradle
echo.>> build.gradle
echo allprojects {>> build.gradle
echo     repositories {>> build.gradle
echo         google()>> build.gradle
echo         mavenCentral()>> build.gradle
echo     }>> build.gradle
echo }>> build.gradle

:: Create simplified settings.gradle
echo include ':app' > settings.gradle

:: Create simplified app build.gradle
echo // App module build file > app\build.gradle
echo apply plugin: 'com.android.application'>> app\build.gradle
echo apply plugin: 'kotlin-android'>> app\build.gradle
echo.>> app\build.gradle
echo android {>> app\build.gradle
echo     compileSdkVersion 31>> app\build.gradle
echo.>> app\build.gradle
echo     defaultConfig {>> app\build.gradle
echo         applicationId "com.example.electricalcalculator">> app\build.gradle
echo         minSdkVersion 21>> app\build.gradle
echo         targetSdkVersion 31>> app\build.gradle
echo         versionCode 1>> app\build.gradle
echo         versionName "1.0">> app\build.gradle
echo     }>> app\build.gradle
echo.>> app\build.gradle
echo     buildTypes {>> app\build.gradle
echo         release {>> app\build.gradle
echo             minifyEnabled false>> app\build.gradle
echo             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'>> app\build.gradle
echo         }>> app\build.gradle
echo     }>> app\build.gradle
echo.>> app\build.gradle
echo     compileOptions {>> app\build.gradle
echo         sourceCompatibility JavaVersion.VERSION_1_8>> app\build.gradle
echo         targetCompatibility JavaVersion.VERSION_1_8>> app\build.gradle
echo     }>> app\build.gradle
echo.>> app\build.gradle
echo     kotlinOptions {>> app\build.gradle
echo         jvmTarget = '1.8'>> app\build.gradle
echo     }>> app\build.gradle
echo }>> app\build.gradle
echo.>> app\build.gradle
echo dependencies {>> app\build.gradle
echo     implementation 'androidx.core:core-ktx:1.7.0'>> app\build.gradle
echo     implementation 'androidx.appcompat:appcompat:1.4.0'>> app\build.gradle
echo     implementation 'com.google.android.material:material:1.4.0'>> app\build.gradle
echo     implementation 'androidx.constraintlayout:constraintlayout:2.1.2'>> app\build.gradle
echo }>> app\build.gradle

:: Update wrapper properties to use Gradle 6.9.3
echo distributionBase=GRADLE_USER_HOME > gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists >> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-6.9.3-bin.zip >> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME >> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists >> gradle\wrapper\gradle-wrapper.properties

echo.
echo ===== Creating basic activity =====
echo.

:: Create a simple main activity
mkdir "app\src\main\java\com\example\electricalcalculator" 2>NUL

echo package com.example.electricalcalculator; > app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo. >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo import android.os.Bundle; >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo import androidx.appcompat.app.AppCompatActivity; >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo. >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo public class MainActivity extends AppCompatActivity { >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo     @Override >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo     protected void onCreate(Bundle savedInstanceState) { >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo         super.onCreate(savedInstanceState); >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo         setContentView(R.layout.activity_main); >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo     } >> app\src\main\java\com\example\electricalcalculator\MainActivity.java
echo } >> app\src\main\java\com\example\electricalcalculator\MainActivity.java

:: Create simple layout
mkdir "app\src\main\res\layout" 2>NUL

echo ^<?xml version="1.0" encoding="utf-8"?^> > app\src\main\res\layout\activity_main.xml
echo ^<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android" >> app\src\main\res\layout\activity_main.xml
echo     android:layout_width="match_parent" >> app\src\main\res\layout\activity_main.xml
echo     android:layout_height="match_parent"^> >> app\src\main\res\layout\activity_main.xml
echo. >> app\src\main\res\layout\activity_main.xml
echo     ^<TextView >> app\src\main\res\layout\activity_main.xml
echo         android:layout_width="wrap_content" >> app\src\main\res\layout\activity_main.xml
echo         android:layout_height="wrap_content" >> app\src\main\res\layout\activity_main.xml
echo         android:layout_centerInParent="true" >> app\src\main\res\layout\activity_main.xml
echo         android:text="Electrician App" >> app\src\main\res\layout\activity_main.xml
echo         android:textSize="24sp" /^> >> app\src\main\res\layout\activity_main.xml
echo. >> app\src\main\res\layout\activity_main.xml
echo ^</RelativeLayout^> >> app\src\main\res\layout\activity_main.xml

:: Simple AndroidManifest
echo ^<?xml version="1.0" encoding="utf-8"?^> > app\src\main\AndroidManifest.xml
echo ^<manifest xmlns:android="http://schemas.android.com/apk/res/android" >> app\src\main\AndroidManifest.xml
echo     package="com.example.electricalcalculator"^> >> app\src\main\AndroidManifest.xml
echo. >> app\src\main\AndroidManifest.xml
echo     ^<application >> app\src\main\AndroidManifest.xml
echo         android:allowBackup="true" >> app\src\main\AndroidManifest.xml
echo         android:icon="@mipmap/ic_launcher" >> app\src\main\AndroidManifest.xml
echo         android:label="Electrician App" >> app\src\main\AndroidManifest.xml
echo         android:roundIcon="@mipmap/ic_launcher_round" >> app\src\main\AndroidManifest.xml
echo         android:supportsRtl="true" >> app\src\main\AndroidManifest.xml
echo         android:theme="@style/Theme.AppCompat"^> >> app\src\main\AndroidManifest.xml
echo. >> app\src\main\AndroidManifest.xml
echo         ^<activity android:name=".MainActivity" android:exported="true"^> >> app\src\main\AndroidManifest.xml
echo             ^<intent-filter^> >> app\src\main\AndroidManifest.xml
echo                 ^<action android:name="android.intent.action.MAIN" /^> >> app\src\main\AndroidManifest.xml
echo                 ^<category android:name="android.intent.category.LAUNCHER" /^> >> app\src\main\AndroidManifest.xml
echo             ^</intent-filter^> >> app\src\main\AndroidManifest.xml
echo         ^</activity^> >> app\src\main\AndroidManifest.xml
echo     ^</application^> >> app\src\main\AndroidManifest.xml
echo ^</manifest^> >> app\src\main\AndroidManifest.xml

echo.
echo ===== Building APK =====
echo.

call gradlew.bat --no-daemon --stacktrace assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed!
    echo.
    echo Would you like to restore the backup? (Y/N)
    set /p RESTORE=
    if /i "!RESTORE!"=="Y" (
        copy "%BACKUP_DIR%\gradle-wrapper.properties" "gradle\wrapper\gradle-wrapper.properties"
        copy "%BACKUP_DIR%\app-build.gradle" "app\build.gradle"
        copy "%BACKUP_DIR%\build.gradle" "build.gradle"
        copy "%BACKUP_DIR%\settings.gradle" "settings.gradle"
        echo Restored from backup.
    )
    goto :end
)

echo.
echo ===== Build Successful! =====
echo.
echo Basic APK created at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo Note: This is a minimal APK that may not include all features.
echo The backup of your original files is in: %BACKUP_DIR%
echo.

:end
echo.
echo Press any key to exit...
pause

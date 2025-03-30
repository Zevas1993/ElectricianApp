@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Direct Build.gradle Fix
echo ===================================
echo.

:: Navigate to the project directory
cd /d "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

echo Step 1: Setting up JDK 17
echo -----------------------
set "JDK_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "JAVA_HOME=%JDK_PATH%"
set "PATH=%JDK_PATH%\bin;%PATH%"

echo Using JDK from: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
echo.

echo Step 2: Fixing the build.gradle file
echo ---------------------------------

echo // Fixed root build.gradle > build.gradle.temp
echo.>> build.gradle.temp
echo // Top-level build file >> build.gradle.temp
echo buildscript {>> build.gradle.temp
echo     repositories {>> build.gradle.temp
echo         google()>> build.gradle.temp
echo         mavenCentral()>> build.gradle.temp
echo         gradlePluginPortal()>> build.gradle.temp
echo     }>> build.gradle.temp
echo     >> build.gradle.temp
echo     dependencies {>> build.gradle.temp
echo         classpath 'com.android.tools.build:gradle:8.9.0'>> build.gradle.temp
echo         classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22">> build.gradle.temp
echo         classpath "androidx.navigation:navigation-safe-args-gradle-plugin:2.8.9">> build.gradle.temp
echo         classpath 'com.google.dagger:hilt-android-gradle-plugin:2.48'>> build.gradle.temp
echo     }>> build.gradle.temp
echo }>> build.gradle.temp
echo.>> build.gradle.temp
echo // Set common Java options across all modules>> build.gradle.temp
echo subprojects {>> build.gradle.temp
echo     afterEvaluate {>> build.gradle.temp
echo         if (project.hasProperty('android')) {>> build.gradle.temp
echo             android {>> build.gradle.temp
echo                 // Handle API level 34+ visibility issues>> build.gradle.temp
echo                 buildFeatures {>> build.gradle.temp
echo                     buildConfig true>> build.gradle.temp
echo                 }>> build.gradle.temp
echo                 >> build.gradle.temp
echo                 compileOptions {>> build.gradle.temp
echo                     sourceCompatibility JavaVersion.VERSION_17>> build.gradle.temp
echo                     targetCompatibility JavaVersion.VERSION_17>> build.gradle.temp
echo                 }>> build.gradle.temp
echo                 >> build.gradle.temp
echo                 kotlinOptions {>> build.gradle.temp
echo                     jvmTarget = '17'>> build.gradle.temp
echo                 }>> build.gradle.temp
echo                 >> build.gradle.temp
echo                 // Configure source sets>> build.gradle.temp
echo                 sourceSets {>> build.gradle.temp
echo                     main {>> build.gradle.temp
echo                         java.srcDirs = ['src/main/java']>> build.gradle.temp
echo                     }>> build.gradle.temp
echo                 }>> build.gradle.temp
echo             }>> build.gradle.temp
echo         }>> build.gradle.temp
echo     }>> build.gradle.temp
echo }>> build.gradle.temp

move /y build.gradle.temp build.gradle > nul
echo.

echo Step 3: Updating gradle.properties with correct JDK path
echo ----------------------------------------------------
echo # Project-wide Gradle settings > gradle.properties.temp
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 >> gradle.properties.temp
echo. >> gradle.properties.temp
echo # Use JDK 17 (Temurin/AdoptOpenJDK) >> gradle.properties.temp
echo org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.14.7-hotspot >> gradle.properties.temp
echo. >> gradle.properties.temp
echo # When configured, Gradle will run in incubating parallel mode. >> gradle.properties.temp
echo org.gradle.parallel=true >> gradle.properties.temp
echo. >> gradle.properties.temp
echo # AndroidX package structure to make it clearer which packages are bundled with the >> gradle.properties.temp
echo # Android operating system, and which are packaged with your app's APK >> gradle.properties.temp
echo android.useAndroidX=true >> gradle.properties.temp
echo. >> gradle.properties.temp
echo # Enable R8 full mode >> gradle.properties.temp
echo android.enableR8.fullMode=true >> gradle.properties.temp
echo. >> gradle.properties.temp
echo # Kotlin code style for this project: "official" or "obsolete": >> gradle.properties.temp
echo kotlin.code.style=official >> gradle.properties.temp

move /y gradle.properties.temp gradle.properties > nul
echo.

echo Step 4: Cleaning build cache
echo -----------------------
call gradlew.bat --stop
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "build" 2>nul
rmdir /s /q "app\build" 2>nul
rmdir /s /q "data\build" 2>nul
rmdir /s /q "domain\build" 2>nul
for /d %%d in (feature\*) do (
    if exist "%%d\build" rmdir /s /q "%%d\build" 2>nul
)
echo.

echo Step 5: Running build with fixed build.gradle
echo -------------------------------------------
echo This might take several minutes to download dependencies and compile...
echo.

:: Run with explicit Java home argument
call gradlew.bat clean assembleDebug -Dorg.gradle.java.home="%JDK_PATH%" --stacktrace

echo.
echo ===================================
echo Build process completed!
echo ===================================
echo.
echo If you still see any errors in the build log, please check:
echo 1. JDK_TROUBLESHOOTING.md for more details
echo 2. build.gradle for any other issues
echo.

echo Press any key to exit...
pause > nul
exit /b 0

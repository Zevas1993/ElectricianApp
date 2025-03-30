@echo off
echo ===================================
echo ElectricianApp Comprehensive Fix Script
echo ===================================
echo.

set "PROJECT_DIR=C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
cd /d "%PROJECT_DIR%"

echo Step 1: Fixing duplicate class definitions
echo -----------------------------------------
echo Checking for and removing duplicate files...

:: Remove duplicate database files
if exist "data\build\tmp\kapt3\stubs\debug\com\example\electricalcalculator\data\db\ElectricianDatabase.java" (
    del /q "data\build\tmp\kapt3\stubs\debug\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
    echo Removed duplicate ElectricianDatabase.java from kapt stubs
)

echo.
echo Step 2: Stopping any running Gradle daemons
echo ------------------------------------------
call gradlew.bat --stop

echo.
echo Step 3: Cleaning build directories
echo -------------------------------
if exist ".gradle" (
    echo Cleaning .gradle directory...
    rd /s /q ".gradle" 2>nul
)
if exist "build" (
    echo Cleaning build directory...
    rd /s /q "build" 2>nul
)
if exist "app\build" (
    echo Cleaning app module build...
    rd /s /q "app\build" 2>nul
)
if exist "domain\build" (
    echo Cleaning domain module build...
    rd /s /q "domain\build" 2>nul
)
if exist "data\build" (
    echo Cleaning data module build...
    rd /s /q "data\build" 2>nul
)
if exist "feature\box\build" (
    echo Cleaning feature:box module build...
    rd /s /q "feature\box\build" 2>nul
)
if exist "feature\conduit\build" (
    echo Cleaning feature:conduit module build...
    rd /s /q "feature\conduit\build" 2>nul
)
if exist "feature\dwelling\build" (
    echo Cleaning feature:dwelling module build...
    rd /s /q "feature\dwelling\build" 2>nul
)

echo.
echo Step 4: Updating build.gradle.kts to fix buildConfig issue
echo ---------------------------------------------------------

:: Update app/build.gradle to fix buildConfig deprecation
echo Updating app/build.gradle for buildConfig...
echo // App module build file > temp.gradle
echo plugins { >> temp.gradle
echo     id 'com.android.application' >> temp.gradle
echo     id 'org.jetbrains.kotlin.android' >> temp.gradle
echo     id 'kotlin-kapt' >> temp.gradle
echo     id 'dagger.hilt.android.plugin' >> temp.gradle
echo } >> temp.gradle
echo. >> temp.gradle
echo android { >> temp.gradle
echo     namespace 'com.example.electricalcalculator' >> temp

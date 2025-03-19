@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Build Cache Cleanup
echo ===================================
echo.

cd /d "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

echo Step 1: Stopping all Gradle daemons
echo ----------------------------------
call gradlew.bat --stop
echo.

echo Step 2: Removing Kapt generated files
echo -----------------------------------
if exist "data\build\tmp\kapt3" (
    echo Removing data/build/tmp/kapt3...
    rmdir /s /q "data\build\tmp\kapt3"
)

if exist "app\build\tmp\kapt3" (
    echo Removing app/build/tmp/kapt3...
    rmdir /s /q "app\build\tmp\kapt3"
)

if exist "domain\build\tmp\kapt3" (
    echo Removing domain/build/tmp/kapt3...
    rmdir /s /q "domain\build\tmp\kapt3"
)

echo.

echo Step 3: Removing build directories
echo --------------------------------
echo Removing data/build...
rmdir /s /q "data\build"

echo Removing app/build...
rmdir /s /q "app\build"

echo Removing domain/build...
rmdir /s /q "domain\build"

echo Removing build directory...
rmdir /s /q "build"

echo Removing .gradle directory...
rmdir /s /q ".gradle"
echo.

echo Step 4: Cleaning Gradle caches
echo ----------------------------
call gradlew.bat cleanBuildCache
echo.

echo ===================================
echo Cache cleanup completed successfully
echo ===================================
echo.
echo You can now run isolated_build.bat to rebuild the project with a clean environment.
echo.

pause

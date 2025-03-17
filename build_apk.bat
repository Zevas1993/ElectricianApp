@echo on
echo Starting APK build process for ElectricianApp...
echo.

echo ===== Cleaning project =====
call gradlew.bat clean

echo.
echo ===== Assembling debug APK =====
call gradlew.bat --info assembleDebug

echo.
echo ===== Building complete =====
echo.

echo APK file should be available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

echo ===== Optional: Install directly to connected device =====
echo To install directly to a connected device, run:
echo gradlew.bat installDebug
echo.

echo ===== Optional: Build release APK =====
echo To build a release APK (requires signing configuration), run:
echo gradlew.bat assembleRelease
echo.

pause

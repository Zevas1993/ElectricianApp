@echo on
echo =============================================================
echo Simple ElectricianApp Build Script
echo =============================================================
echo.

echo ===== Java Version Check =====
java -version
echo.

echo ===== Setting Environment Variables =====
set JAVA_HOME=C:\Program Files\Java\jdk-23
echo JAVA_HOME=%JAVA_HOME%
echo.

echo ===== Cleaning project =====
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo Error during cleaning. Please check the output above.
    goto :end
)

echo.
echo ===== Assembling debug APK =====
call gradlew.bat --stacktrace --info assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo Error during build. Please check the output above.
    goto :end
)

echo.
echo ===== Build completed =====
echo APK file should be available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

:end
echo.
echo Press any key to exit...
pause

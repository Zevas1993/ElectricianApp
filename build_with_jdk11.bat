@echo on
echo =============================================================
echo ElectricianApp Build with JDK 11 (Fallback)
echo =============================================================
echo.

echo ===== Setting Basic Build Environment =====
set "JAVA_OPTS=-Xmx1024m"
set "GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx1024m"

echo.
echo ===== Forcing Gradle to use JDK 11 Compatibility =====
echo.

echo ===== Running build with explicit JVM compatibility =====
call gradlew.bat --warning-mode all --stacktrace --no-daemon assembleDebug -Dorg.gradle.java.home="%JAVA_HOME%" -Dkotlin.compiler.execution.strategy="in-process"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! 
    echo Please check the error messages above.
    goto :end
)

echo.
echo ===== Build completed successfully! =====
echo.
echo APK should be available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

:end
echo.
echo Press any key to exit...
pause

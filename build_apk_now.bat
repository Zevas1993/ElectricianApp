@echo on
echo =============================================================
echo ElectricianApp Direct APK Build
echo =============================================================
echo.

echo ===== Setting up build environment =====
:: Limit memory usage
set "JAVA_OPTS=-Xmx512m"
set "GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx512m"

:: Force Kotlin to compile with in-process
set "KOTLIN_OPTS=-Dkotlin.compiler.execution.strategy=in-process"

:: Disable Gradle daemon to prevent JVM version issues
set "GRADLE_ARGS=--no-daemon --warning-mode all"

echo.
echo ===== Updating Gradle settings for compatibility =====
echo.

:: Create a backup of gradle.properties
copy "gradle.properties" "gradle.properties.bak"

:: Append compatibility flags to gradle.properties
echo. >> gradle.properties
echo # Added by build_apk_now.bat for compatibility >> gradle.properties
echo org.gradle.java.home=%JAVA_HOME% >> gradle.properties
echo kotlin.stdlib.default.dependency=false >> gradle.properties

:: Update the wrapper properties to use older Gradle version
echo distributionBase=GRADLE_USER_HOME > gradle\wrapper\gradle-wrapper.properties
echo distributionPath=wrapper/dists >> gradle\wrapper\gradle-wrapper.properties
echo distributionUrl=https\://services.gradle.org/distributions/gradle-7.3.3-bin.zip >> gradle\wrapper\gradle-wrapper.properties
echo zipStoreBase=GRADLE_USER_HOME >> gradle\wrapper\gradle-wrapper.properties
echo zipStorePath=wrapper/dists >> gradle\wrapper\gradle-wrapper.properties

echo.
echo ===== Building APK =====
echo.

:: Clean and build without using daemon
call gradlew.bat %GRADLE_ARGS% clean
call gradlew.bat %GRADLE_ARGS% assembleDebug %KOTLIN_OPTS%

:: Check if the build was successful
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed!
    echo Please check the error messages above.
    goto :restore
)

echo.
echo ===== Build completed successfully! =====
echo.
echo APK should be available at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.

:restore
:: Restore the original gradle.properties
copy "gradle.properties.bak" "gradle.properties"
del "gradle.properties.bak"

echo.
echo Press any key to exit...
pause

@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp Compatible APK Builder
echo =============================================================
echo.

:: Check if JAVA_HOME is set correctly
if not defined JAVA_HOME (
    echo ERROR: JAVA_HOME is not set.
    echo Please run the run_fix_java_home.bat script first.
    goto :error
)

echo Using JAVA_HOME: %JAVA_HOME%
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Invalid JAVA_HOME path. Java executable not found at %JAVA_HOME%\bin\java.exe
    echo Please run the run_fix_java_home.bat script to fix JAVA_HOME.
    goto :error
)

:: Check Java version
echo.
echo Checking Java version...
"%JAVA_HOME%\bin\java" -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to get Java version.
    goto :error
)

:: Verify Gradle wrapper exists
echo.
echo Checking Gradle wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper not found. Running fix_gradle_wrapper.bat...
    call fix_gradle_wrapper.bat
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: Failed to fix Gradle wrapper.
        goto :error
    )
)

:: Set up simplified build environment
echo.
echo Setting up build environment...

:: Create a backup of important files
echo Creating backups...
set BACKUP_DIR=backup_%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_DIR=%BACKUP_DIR: =0%
mkdir "%BACKUP_DIR%" 2>nul
copy "gradle\wrapper\gradle-wrapper.properties" "%BACKUP_DIR%\gradle-wrapper.properties" >nul
copy "app\build.gradle" "%BACKUP_DIR%\app-build.gradle" >nul
copy "build.gradle" "%BACKUP_DIR%\build.gradle" >nul
copy "settings.gradle" "%BACKUP_DIR%\settings.gradle" >nul
copy "gradle.properties" "%BACKUP_DIR%\gradle.properties" >nul

:: Update gradle.properties for compatibility
echo.
echo Configuring Gradle properties for compatibility...
(
    echo # Adding compatibility settings
    echo org.gradle.jvmargs=-Xmx512m -Dfile.encoding=UTF-8
    echo android.useAndroidX=true
    echo android.enableJetifier=true
    echo org.gradle.configureondemand=true
    echo org.gradle.parallel=true
    echo org.gradle.caching=true
    echo kotlin.stdlib.default.dependency=false
) > gradle.properties

:: Set JAVA_HOME in the environment for this session
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Clean the project first
echo.
echo Cleaning project...
call gradlew.bat clean --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Clean failed, but continuing with build...
)

:: Build the APK
echo.
echo Building APK...
call gradlew.bat assembleDebug --no-daemon --stacktrace
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed.
    echo Please check the output above for more details.
    goto :restore_backup
)

:: Check if APK was created
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo =============================================
    echo APK built successfully!
    echo =============================================
    echo.
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on a connected device:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
    goto :end
) else (
    echo.
    echo ERROR: APK file not found after build.
    goto :restore_backup
)

:restore_backup
echo.
echo Do you want to restore the backup files? (Y/N)
set /p RESTORE_CHOICE=
if /i "%RESTORE_CHOICE%"=="Y" (
    echo Restoring backup files...
    copy "%BACKUP_DIR%\gradle-wrapper.properties" "gradle\wrapper\gradle-wrapper.properties" >nul
    copy "%BACKUP_DIR%\app-build.gradle" "app\build.gradle" >nul
    copy "%BACKUP_DIR%\build.gradle" "build.gradle" >nul
    copy "%BACKUP_DIR%\settings.gradle" "settings.gradle" >nul
    copy "%BACKUP_DIR%\gradle.properties" "gradle.properties" >nul
    echo Backup files restored.
)
goto :error

:error
echo.
echo Build process failed.
echo.
echo Troubleshooting suggestions:
echo 1. Check if JAVA_HOME is set correctly (run_fix_java_home.bat)
echo 2. Try using Android Studio directly
echo 3. Check the ElectricianApp documentation for more details
echo.
goto :end

:end
echo.
echo Press any key to exit...
pause > nul

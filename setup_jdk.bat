@echo on
setlocal enabledelayedexpansion

echo =============================================================
echo ElectricianApp JDK Setup Script
echo =============================================================
echo.

echo This script will download and set up AdoptOpenJDK 17 for building the ElectricianApp.
echo.

set DOWNLOAD_DIR=%TEMP%\jdk_download
set JDK_VERSION=17.0.7+7
set JDK_ZIP=OpenJDK17U-jdk_x64_windows_hotspot_%JDK_VERSION:+=_%.zip
set JDK_URL=https://github.com/adoptium/temurin17-binaries/releases/download/jdk-%JDK_VERSION%/%JDK_ZIP%
set JDK_DIR=%~dp0jdk17

echo Creating download directory...
mkdir "%DOWNLOAD_DIR%" 2>nul

echo Downloading AdoptOpenJDK 17...
echo URL: %JDK_URL%
echo Destination: %DOWNLOAD_DIR%\%JDK_ZIP%

powershell -Command "(New-Object System.Net.WebClient).DownloadFile('%JDK_URL%', '%DOWNLOAD_DIR%\%JDK_ZIP%')"

if not exist "%DOWNLOAD_DIR%\%JDK_ZIP%" (
    echo Failed to download JDK. Please check your internet connection and try again.
    goto :error
)

echo Download completed. Extracting JDK...
echo From: %DOWNLOAD_DIR%\%JDK_ZIP%
echo To: %JDK_DIR%

if exist "%JDK_DIR%" (
    echo JDK directory already exists. Removing...
    rmdir /s /q "%JDK_DIR%"
)

mkdir "%JDK_DIR%"

powershell -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\%JDK_ZIP%' -DestinationPath '%DOWNLOAD_DIR%'"

for /d %%i in (%DOWNLOAD_DIR%\jdk-*) do (
    echo Moving %%i to %JDK_DIR%
    xcopy "%%i" "%JDK_DIR%" /s /e /i
)

echo Cleaning up...
rmdir /s /q "%DOWNLOAD_DIR%"

echo Testing JDK installation...
set JAVA_HOME=%JDK_DIR%
echo JAVA_HOME=%JAVA_HOME%

"%JDK_DIR%\bin\java" -version
if %ERRORLEVEL% NEQ 0 (
    echo JDK installation test failed.
    goto :error
)

echo.
echo JDK 17 has been successfully installed to:
echo %JDK_DIR%
echo.
echo To use this JDK, run the build_with_jdk17.bat script.
echo.

echo Creating build script with JDK 17...
(
    echo @echo on
    echo echo =============================================================
    echo echo ElectricianApp Build with JDK 17
    echo echo =============================================================
    echo echo.
    echo.
    echo echo ===== Setting Environment Variables =====
    echo set JAVA_HOME=%JDK_DIR:\=\\%
    echo set PATH=%%JAVA_HOME%%\bin;%%PATH%%
    echo echo JAVA_HOME=%%JAVA_HOME%%
    echo echo.
    echo.
    echo echo ===== Java Version Check =====
    echo java -version
    echo echo.
    echo.
    echo echo ===== Cleaning project =====
    echo call gradlew.bat clean
    echo if %%ERRORLEVEL%% NEQ 0 ^(
    echo     echo Error during cleaning. Please check the output above.
    echo     goto :end
    echo ^)
    echo.
    echo echo.
    echo echo ===== Assembling debug APK =====
    echo call gradlew.bat --info assembleDebug
    echo if %%ERRORLEVEL%% NEQ 0 ^(
    echo     echo Error during build. Please check the output above.
    echo     goto :end
    echo ^)
    echo.
    echo echo.
    echo echo ===== Build completed =====
    echo echo APK file should be available at:
    echo echo app\build\outputs\apk\debug\app-debug.apk
    echo echo.
    echo.
    echo :end
    echo echo.
    echo echo Press any key to exit...
    echo pause
) > "%~dp0build_with_jdk17.bat"

echo.
echo Created build_with_jdk17.bat script.
echo.
echo Setup completed successfully!
goto :end

:error
echo Setup failed!

:end
echo.
echo Press any key to exit...
pause

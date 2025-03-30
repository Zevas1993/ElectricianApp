@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Minimal JDK Fix
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

echo Step 2: Cleaning all build artifacts
echo ---------------------------------
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

echo Step 4: Running build with direct JDK path
echo -------------------------------------------
echo This might take several minutes to download dependencies and compile...
echo.

:: Run with explicit Java home argument and skip any sourceset modifications
call gradlew.bat clean assembleDebug -Dorg.gradle.java.home="%JDK_PATH%" --stacktrace

echo.
echo ===================================
echo Build process completed!
echo ===================================
echo.
echo For detailed troubleshooting info: JDK_TROUBLESHOOTING.md
echo.

echo Press any key to exit...
pause > nul
exit /b 0

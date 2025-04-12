@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo ElectricianApp Build Script
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
echo # Use JDK 17 (Temurin/AdoptOpenJDK)>> gradle.properties.temp
echo org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.14.7-hotspot>> gradle.properties.temp
echo.>> gradle.properties.temp
echo # When configured, Gradle will run in incubating parallel mode.>> gradle.properties.temp
echo org.gradle.parallel=true>> gradle.properties.temp
echo.>> gradle.properties.temp
echo # AndroidX package structure to make it clearer which packages are bundled with the>> gradle.properties.temp
echo # Android operating system, and which are packaged with your app's APK>> gradle.properties.temp
echo android.useAndroidX=true>> gradle.properties.temp
echo.>> gradle.properties.temp
echo # Enable R8 full mode>> gradle.properties.temp
echo android.enableR8.fullMode=true>> gradle.properties.temp
echo.>> gradle.properties.temp
echo # Kotlin code style for this project: "official" or "obsolete":>> gradle.properties.temp
echo kotlin.code.style=official>> gradle.properties.temp

move /y gradle.properties.temp gradle.properties > nul
echo.

echo Step 4: Creating source set configuration file
echo -------------------------------------------
if not exist config mkdir config 2>nul
echo // Fix for package name mismatch between declared package and expected package > config\fix_sourceset.gradle
echo.>> config\fix_sourceset.gradle
echo android.sourceSets {>> config\fix_sourceset.gradle
echo     main.java.srcDirs += 'src/main/java'>> config\fix_sourceset.gradle
echo     main.java.srcDirs -= 'src/main/kotlin'>> config\fix_sourceset.gradle
echo     main.java.includes = ['**/*.java', '**/*.kt']>> config\fix_sourceset.gradle
echo }>> config\fix_sourceset.gradle
echo.>> config\fix_sourceset.gradle
:: echo tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {>> config\fix_sourceset.gradle
:: echo     kotlinOptions {>> config\fix_sourceset.gradle
:: echo         jvmTarget = "17">> config\fix_sourceset.gradle
:: echo     }>> config\fix_sourceset.gradle
:: echo }>> config\fix_sourceset.gradle
:: echo.>> config\fix_sourceset.gradle
echo kapt {>> config\fix_sourceset.gradle
echo     correctErrorTypes true>> config\fix_sourceset.gradle
echo     useBuildCache false>> config\fix_sourceset.gradle
echo     arguments {>> config\fix_sourceset.gradle
echo         arg("room.schemaLocation", "$projectDir/schemas")>> config\fix_sourceset.gradle
echo         arg("room.incremental", "true")>> config\fix_sourceset.gradle
echo         arg("room.expandProjection", "true")>> config\fix_sourceset.gradle
echo     }>> config\fix_sourceset.gradle
echo }>> config\fix_sourceset.gradle
echo.

echo Step 5: Updating build.gradle files to use the configuration
echo ---------------------------------------------------------

:: Update app build.gradle
echo Updating app build.gradle
call :append_apply_from "app\build.gradle"

:: Update data build.gradle (Module removed)
:: echo Updating data build.gradle
:: call :append_apply_from "data\build.gradle"

:: Update domain build.gradle (Module removed)
:: echo Updating domain build.gradle
:: call :append_apply_from "domain\build.gradle"

:: Update feature modules build.gradle
for /d %%d in (feature\*) do (
    if exist "%%d\build.gradle" (
        echo Updating %%d\build.gradle
        call :append_apply_from "%%d\build.gradle"
    )
)
echo.

echo Step 6: Fixing package structure in build.gradle
echo ---------------------------------------------
echo // Updated main build.gradle > build.gradle.temp
echo.>> build.gradle.temp
type build.gradle >> build.gradle.temp

echo.>> build.gradle.temp
echo // Fix for sourceset issues>> build.gradle.temp
echo allprojects {>> build.gradle.temp
echo     // Make sure to use full package names>> build.gradle.temp
echo     afterEvaluate {>> build.gradle.temp
echo         if (project.hasProperty('android')) {>> build.gradle.temp
echo             android {>> build.gradle.temp
echo                 sourceSets {>> build.gradle.temp
echo                     main {>> build.gradle.temp
echo                         java.srcDirs = ['src/main/java']>> build.gradle.temp
echo                     }>> build.gradle.temp
echo                 }>> build.gradle.temp
echo             }>> build.gradle.temp
echo         }>> build.gradle.temp
echo     }>> build.gradle.temp
echo }>> build.gradle.temp

move /y build.gradle.temp build.gradle >nul
echo.

echo Step 7: Running build with fixed configuration
echo -------------------------------------------
echo This might take several minutes to download dependencies and compile...
echo.

call gradlew.bat clean
call gradlew.bat assembleDebug --no-daemon --info -Dorg.gradle.java.home="%JDK_PATH%"

echo.
echo ===================================
echo Build process completed!
echo ===================================
echo.
echo If you still see package path issues:
echo 1. Open the project in Android Studio
echo 2. Right-click on the 'data/src/main/java' folder
echo 3. Select "Mark Directory as" > "Sources Root"
echo.
echo For detailed troubleshooting info: JDK_TROUBLESHOOTING.md
echo.

echo Press any key to exit...
pause > nul
exit /b 0

:append_apply_from
set "file=%~1"
set "temp=%file%.temp"
type "%file%" > "%temp%"

:: Check if the file already contains the apply from line
findstr /C:"apply from: '../config/fix_sourceset.gradle'" "%file%" > nul
if errorlevel 1 (
    echo.>> "%temp%"
    echo // Apply source set configuration fix>> "%temp%"
    echo apply from: '../config/fix_sourceset.gradle'>> "%temp%"
    move /y "%temp%" "%file%" >nul
) else (
    del "%temp%"
    echo - Configuration already applied in %file% - skipping
)
exit /b 0

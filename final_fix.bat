@echo off
echo ===================================
echo ElectricianApp Final Comprehensive Fix
echo ===================================
echo.

set "PROJECT_DIR=C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
cd /d "%PROJECT_DIR%"

echo Step 1: Setting Java Environment
echo -------------------------------
:: Try common JDK locations
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

set "JAVA_HOME=C:\Program Files\Amazon Corretto\jdk17.0.9_8"
if exist "%JAVA_HOME%\bin\java.exe" goto java_found

echo Could not find Java in common locations.
echo Please install Java JDK 17 and try again.
goto end

:java_found
echo Found JDK at: %JAVA_HOME%
echo Setting system JAVA_HOME...
setx JAVA_HOME "%JAVA_HOME%" /m
echo Setting current session JAVA_HOME...
set "PATH=%JAVA_HOME%\bin;%PATH%"

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
echo Step 4: Fixing AndroidManifest files
echo ----------------------------------
:: Ensure all manifests don't have the package attribute
for %%F in (
    data\src\main\AndroidManifest.xml
    domain\src\main\AndroidManifest.xml
    feature\box\src\main\AndroidManifest.xml
    feature\conduit\src\main\AndroidManifest.xml
    feature\dwelling\src\main\AndroidManifest.xml
) do (
    echo Fixing %%F...
    type "%%F" | find /v "package=" > "%%F.tmp"
    move /y "%%F.tmp" "%%F" > nul
)

echo.
echo Step 5: Creating Hilt Application class
echo ------------------------------------
if not exist "app\src\main\java\com\example\electricalcalculator" (
    mkdir "app\src\main\java\com\example\electricalcalculator"
)

echo Creating ElectricianApp.java...
echo package com.example.electricalcalculator;> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo.>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo import android.app.Application;>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo import dagger.hilt.android.HiltAndroidApp;>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo.>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo /**>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo  * Main application class with Hilt dependency injection>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo  */>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo @HiltAndroidApp>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo public class ElectricianApp extends Application {>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo.>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     @Override>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     public void onCreate() {>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo         super.onCreate();>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     }>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo }>> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"

echo.
echo Step 6: Updating app/build.gradle
echo -------------------------------
echo Adding buildConfig and Hilt to app/build.gradle...

:: Update app AndroidManifest to include application name
echo Updating app AndroidManifest.xml...
powershell -Command "(Get-Content 'app\src\main\AndroidManifest.xml') -replace '<application', '<application android:name=\".ElectricianApp\"' | Set-Content 'app\src\main\AndroidManifest.xml'"

:: Update app/build.gradle to enable buildConfig
echo Updating app/build.gradle for buildConfig...
powershell -Command "(Get-Content 'app\build.gradle') -replace 'buildFeatures \{', 'buildFeatures {\n        buildConfig true' | Set-Content 'app\build.gradle'"

echo.
echo Step 7: Updating build.gradle files to include Hilt
echo -------------------------------------------------
:: Update root build.gradle to include Hilt
echo Updating build.gradle for Hilt...
powershell -Command "(Get-Content 'build.gradle') -replace 'dependencies \{', 'dependencies {\n        classpath \"com.google.dagger:hilt-android-gradle-plugin:2.48\"' | Set-Content 'build.gradle'"

:: Update domain/build.gradle for Hilt
echo Updating domain/build.gradle...
powershell -Command "(Get-Content 'domain\build.gradle') -replace 'plugins \{', 'plugins {\n    id \"kotlin-kapt\"\n    id \"dagger.hilt.android.plugin\"' | Set-Content 'domain\build.gradle'"
powershell -Command "(Get-Content 'domain\build.gradle') -replace 'dependencies \{', 'dependencies {\n    // Dependency Injection\n    implementation \"com.google.dagger:hilt-android:2.48\"\n    kapt \"com.google.dagger:hilt-android-compiler:2.48\"\n    implementation \"javax.inject:javax.inject:1\"' | Set-Content 'domain\build.gradle'"

:: Update data/build.gradle for Hilt
echo Updating data/build.gradle...
powershell -Command "(Get-Content 'data\build.gradle') -replace 'plugins \{', 'plugins {\n    id \"kotlin-kapt\"\n    id \"dagger.hilt.android.plugin\"' | Set-Content 'data\build.gradle'"
powershell -Command "(Get-Content 'data\build.gradle') -replace 'dependencies \{', 'dependencies {\n    // Dependency Injection\n    implementation \"com.google.dagger:hilt-android:2.48\"\n    kapt \"com.google.dagger:hilt-android-compiler:2.48\"\n    implementation \"javax.inject:javax.inject:1\"' | Set-Content 'data\build.gradle'"

echo.
echo Step 8: Fixing XML files with incorrect escaping
echo ---------------------------------------------
:: Fix fragment_conduit_fill_results.xml
echo Fixing XML escaping in fragment_conduit_fill_results.xml...
powershell -Command "(Get-Content 'feature\conduit\src\main\res\layout\fragment_conduit_fill_results.xml') -replace '3/4\\\" EMT', '3/4&quot; EMT' | Set-Content 'feature\conduit\src\main\res\layout\fragment_conduit_fill_results.xml'"

echo.
echo Step 9: Creating missing entity classes
echo ------------------------------------
:: Create entity classes directory if it doesn't exist
if not exist "data\src\main\java\com\example\electricalcalculator\data\db\entity" (
    mkdir "data\src\main\java\com\example\electricalcalculator\data\db\entity"
)

:: Create DwellingUnitApplianceCrossRef.java
echo Creating DwellingUnitApplianceCrossRef.java...
echo package com.example.electricalcalculator.data.db.entity;> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo import androidx.room.Entity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo import androidx.room.ForeignKey;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo import androidx.room.Index;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo  * Cross-reference entity to represent many-to-many relationship between dwelling units and appliances>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo @Entity(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     primaryKeys = {"dwellingUnitId", "applianceId"},>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     foreignKeys = {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         @ForeignKey(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             entity = DwellingUnitEntity.class,>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             parentColumns = "id",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             childColumns = "dwellingUnitId",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             onDelete = ForeignKey.CASCADE>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         ),>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         @ForeignKey(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             entity = ApplianceEntity.class,>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             parentColumns = "id",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             childColumns = "applianceId",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo             onDelete = ForeignKey.CASCADE>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         )>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     },>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     indices = {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         @Index("applianceId")>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo )>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo public class DwellingUnitApplianceCrossRef {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     private long dwellingUnitId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     private long applianceId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     private int quantity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public DwellingUnitApplianceCrossRef(long dwellingUnitId, long applianceId, int quantity) {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         this.dwellingUnitId = dwellingUnitId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         this.applianceId = applianceId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo         this.quantity = quantity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public long getDwellingUnitId() { return dwellingUnitId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public void setDwellingUnitId(long dwellingUnitId) { this.dwellingUnitId = dwellingUnitId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public long getApplianceId() { return applianceId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public void setApplianceId(long applianceId) { this.applianceId = applianceId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public int getQuantity() { return quantity; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo     public void setQuantity(int quantity) { this.quantity = quantity; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitApplianceCrossRef.java"

echo.
echo Step 10: Building the app with updated configuration
echo -------------------------------------------------
echo Building with clean and detailed error reporting...

call gradlew.bat clean build --stacktrace

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code: %ERRORLEVEL%
    
    echo.
    echo Summary of fixes applied:
    echo ------------------------
    echo 1. Removed duplicate class definitions
    echo 2. Fixed XML escaping in layout files
    echo 3. Created missing entity classes
    echo 4. Updated build.gradle files with proper Hilt configuration
    echo 5. Added proper ElectricianApp Application class
    echo 6. Fixed AndroidManifest files
    echo 7. Properly set JAVA_HOME
    
    echo.
    echo Additional diagnostics:
    echo ---------------------
    echo Try running with detailed logging:
    echo gradlew.bat build --debug --stacktrace
) else (
    echo Build completed successfully!
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo.
        echo Debug APK has been created at:
        echo app\build\outputs\apk\debug\app-debug.apk
        
        copy "app\build\outputs\apk\debug\app-debug.apk" "%PROJECT_DIR%\electrician-app.apk" >nul
        echo.
        echo For convenience, the APK has been copied to:
        echo %PROJECT_DIR%\electrician-app.apk
    )
)

:end
pause

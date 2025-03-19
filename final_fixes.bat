@echo off
echo ===================================
echo ElectricianApp Final Fixes Script
echo ===================================
echo.

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
echo Step 2: Updating build.gradle files for domain/data modules
echo ---------------------------------------------------------

:: Update app/build.gradle to add buildConfig and Hilt
echo Adding buildConfig and Hilt to app/build.gradle...
call :replace_in_file "app\build.gradle" "plugins {" "plugins {\n    id 'com.android.application'\n    id 'org.jetbrains.kotlin.android'\n    id 'kotlin-kapt'\n    id 'dagger.hilt.android.plugin'"
call :replace_in_file "app\build.gradle" "dependencies {" "dependencies {\n    // Hilt\n    implementation 'com.google.dagger:hilt-android:2.48'\n    kapt 'com.google.dagger:hilt-android-compiler:2.48'\n    implementation 'javax.inject:javax.inject:1'\n    "

:: Update app/src/main/AndroidManifest.xml to reference ElectricianApp
echo Updating main AndroidManifest.xml...
call :replace_in_file "app\src\main\AndroidManifest.xml" "application" "application\n        android:name=\".ElectricianApp\""

echo.
echo Step 3: Fixing Room database issues
echo ---------------------------------

echo Creating ConduitFillEntity.java...
echo package com.example.electricalcalculator.data.db.entity;> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo import androidx.room.Entity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo import androidx.room.PrimaryKey;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java" 
echo  * Entity representation of a conduit fill calculation>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo @Entity(tableName = "conduit_fill")>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo public class ConduitFillEntity {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     @PrimaryKey(autoGenerate = true)>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private long id;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private String conduitType;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private String conduitSize;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private double totalFillPercentage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private boolean isWithinLimits;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     private long timestamp;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public ConduitFillEntity(String conduitType, String conduitSize, double totalFillPercentage,>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo                           boolean isWithinLimits, long timestamp) {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo         this.conduitType = conduitType;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo         this.conduitSize = conduitSize;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo         this.totalFillPercentage = totalFillPercentage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo         this.isWithinLimits = isWithinLimits;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo         this.timestamp = timestamp;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     // Getters and setters>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public long getId() { return id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setId(long id) { this.id = id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public String getConduitType() { return conduitType; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setConduitType(String conduitType) { this.conduitType = conduitType; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public String getConduitSize() { return conduitSize; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setConduitSize(String conduitSize) { this.conduitSize = conduitSize; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public double getTotalFillPercentage() { return totalFillPercentage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setTotalFillPercentage(double totalFillPercentage) { this.totalFillPercentage = totalFillPercentage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public boolean isWithinLimits() { return isWithinLimits; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setWithinLimits(boolean withinLimits) { isWithinLimits = withinLimits; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public long getTimestamp() { return timestamp; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo     public void setTimestamp(long timestamp) { this.timestamp = timestamp; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillEntity.java"

echo Creating ConduitFillWireCrossRef.java...
echo package com.example.electricalcalculator.data.db.entity;> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo import androidx.room.Entity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo import androidx.room.ForeignKey;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo import androidx.room.Index;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo  * Cross-reference entity to represent many-to-many relationship between conduit fills and wires>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo @Entity(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     primaryKeys = {"conduitFillId", "wireId"},>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     foreignKeys = {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         @ForeignKey(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             entity = ConduitFillEntity.class,>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             parentColumns = "id",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             childColumns = "conduitFillId",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             onDelete = ForeignKey.CASCADE>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         ),>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         @ForeignKey(>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             entity = WireEntity.class,>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             parentColumns = "id",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             childColumns = "wireId",>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo             onDelete = ForeignKey.CASCADE>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         )>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     },>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     indices = {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         @Index("wireId")>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo )>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo public class ConduitFillWireCrossRef {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     private long conduitFillId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     private long wireId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     private int quantity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public ConduitFillWireCrossRef(long conduitFillId, long wireId, int quantity) {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         this.conduitFillId = conduitFillId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         this.wireId = wireId;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo         this.quantity = quantity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public long getConduitFillId() { return conduitFillId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public void setConduitFillId(long conduitFillId) { this.conduitFillId = conduitFillId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public long getWireId() { return wireId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public void setWireId(long wireId) { this.wireId = wireId; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public int getQuantity() { return quantity; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo     public void setQuantity(int quantity) { this.quantity = quantity; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitFillWireCrossRef.java"

echo Creating ApplianceEntity.java...
echo package com.example.electricalcalculator.data.db.entity;> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo import androidx.room.Entity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo import androidx.room.PrimaryKey;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo  * Entity representation of an appliance in the database>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo @Entity(tableName = "appliances")>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo public class ApplianceEntity {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     @PrimaryKey(autoGenerate = true)>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     private long id;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     private String name;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     private double wattage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     private double voltageRating;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     private boolean isMotorLoad;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public ApplianceEntity(String name, double wattage, double voltageRating, boolean isMotorLoad) {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo         this.name = name;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo         this.wattage = wattage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo         this.voltageRating = voltageRating;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo         this.isMotorLoad = isMotorLoad;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     // Getters and setters>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public long getId() { return id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public void setId(long id) { this.id = id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public String getName() { return name; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public void setName(String name) { this.name = name; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public double getWattage() { return wattage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public void setWattage(double wattage) { this.wattage = wattage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public double getVoltageRating() { return voltageRating; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public void setVoltageRating(double voltageRating) { this.voltageRating = voltageRating; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public boolean isMotorLoad() { return isMotorLoad; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo     public void setMotorLoad(boolean motorLoad) { isMotorLoad = motorLoad; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ApplianceEntity.java"

echo Creating DwellingUnitEntity.java...
echo package com.example.electricalcalculator.data.db.entity;> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo import androidx.room.Entity;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo import androidx.room.PrimaryKey;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo  * Entity representation of a dwelling unit in the database>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo @Entity(tableName = "dwelling_units")>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo public class DwellingUnitEntity {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     @PrimaryKey(autoGenerate = true)>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     private long id;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     private String name;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     private double squareFootage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     private int voltageRating;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     private long timestamp;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public DwellingUnitEntity(String name, double squareFootage, int voltageRating, long timestamp) {>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo         this.name = name;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo         this.squareFootage = squareFootage;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo         this.voltageRating = voltageRating;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"

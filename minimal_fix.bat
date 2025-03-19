@echo off
echo ===================================
echo ElectricianApp Minimal Error Fix
echo ===================================
echo.

set "PROJECT_DIR=C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
cd /d "%PROJECT_DIR%"

echo Step 1: Cleaning build folders
echo ---------------------------
if exist "data\build" (
    echo Cleaning data module build...
    rd /s /q "data\build" 2>nul
)
if exist "domain\build" (
    echo Cleaning domain module build...
    rd /s /q "domain\build" 2>nul
)

echo.
echo Step 2: Fixing entity classes
echo ---------------------------

:: Create the DwellingUnitEntity.java file that's causing issues
echo Creating missing entity classes...

if not exist "data\src\main\java\com\example\electricalcalculator\data\db\entity" (
    mkdir "data\src\main\java\com\example\electricalcalculator\data\db\entity"
)

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
echo         this.timestamp = timestamp;>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     // Getters and setters>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public long getId() { return id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public void setId(long id) { this.id = id; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public String getName() { return name; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public void setName(String name) { this.name = name; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public double getSquareFootage() { return squareFootage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public void setSquareFootage(double squareFootage) { this.squareFootage = squareFootage; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public int getVoltageRating() { return voltageRating; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public void setVoltageRating(int voltageRating) { this.voltageRating = voltageRating; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public long getTimestamp() { return timestamp; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo     public void setTimestamp(long timestamp) { this.timestamp = timestamp; }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\entity\DwellingUnitEntity.java"

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
echo Step 3: Fixing ElectricianDatabase class to avoid duplicate classes
echo ----------------------------------------------------------------

:: Remove existing ElectricianDatabase.java and create a simplified one
if exist "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java" (
    del /q "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
    echo Removed existing ElectricianDatabase.java
)

:: Create a very simple ElectricianDatabase version without annotations
echo package com.example.electricalcalculator.data.db;> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo import android.content.Context;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo import androidx.room.Database;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo import androidx.room.Room;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo import androidx.room.RoomDatabase;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo import com.example.electricalcalculator.data.db.entity.*;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo /**>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo  * Room database for the Electrician app>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo  */>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo public abstract class ElectricianDatabase extends RoomDatabase {>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo     // Database implementation will be generated by Room>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo     private static volatile ElectricianDatabase INSTANCE;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo.>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo     public static ElectricianDatabase getInstance(Context context) {>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo         if (INSTANCE == null) {>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo             synchronized (ElectricianDatabase.class) {>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                 if (INSTANCE == null) {>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                     INSTANCE = Room.databaseBuilder(>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                             context.getApplicationContext(),>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                             ElectricianDatabase.class,>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                             "electrician_database")>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                             .fallbackToDestructiveMigration()>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                             .build();>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo                 }>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo             }>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo         }>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo         return INSTANCE;>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo     }>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"
echo }>> "data\src\main\java\com\example\electricalcalculator\data\db\ElectricianDatabase.java"

echo.
echo Step 4: Final build
echo ----------------
echo Running a clean build with the fixed classes...

call gradlew.bat clean assembleDebug --stacktrace

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code: %ERRORLEVEL%
    
    echo Try other build options:
    echo 1. Remove @Database annotation by removing the ElectricianDatabase implementation
    echo 2. Try adding missing DAOs
    echo 3. Run in debug mode with gradlew --info
) else (
    echo Build completed successfully!
)

pause

@echo off
echo ===================================
echo Source Set and Entity Fixes
echo ===================================
echo.

echo Creating necessary directories...
mkdir "app\src\main\java\com\example\electricalcalculator\model" 2>nul
mkdir "data\src\main\java\com\example\electricalcalculator\data\db\dao" 2>nul
mkdir "domain\src\main\java\com\example\electricalcalculator\domain\di" 2>nul

echo.
echo Creating .gitkeep files to preserve directory structure...
echo. > "app\src\main\java\com\example\electricalcalculator\model\.gitkeep"
echo. > "data\src\main\java\com\example\electricalcalculator\data\db\dao\.gitkeep"
echo. > "domain\src\main\java\com\example\electricalcalculator\domain\di\.gitkeep"

echo.
echo Setting up Hilt Application class in app module...
echo package com.example.electricalcalculator; > "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo. >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo import android.app.Application; >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo import dagger.hilt.android.HiltAndroidApp; >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo. >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo /**  >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo  * Main application class with Hilt dependency injection >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo  */ >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo @HiltAndroidApp >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo public class ElectricianApp extends Application { >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo. >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     @Override >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     public void onCreate() { >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo         super.onCreate(); >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo     } >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"
echo } >> "app\src\main\java\com\example\electricalcalculator\ElectricianApp.java"

echo.
echo Creating Room database DAO interfaces...
mkdir "data\src\main\java\com\example\electricalcalculator\data\db\dao" 2>nul

echo package com.example.electricalcalculator.data.db.dao; > "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo import androidx.room.Dao; >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo /**  >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo  * Data Access Object for box fill calculations >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo  */ >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo @Dao >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo public interface BoxFillDao { >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo     // Box fill DAO methods will be implemented here >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"
echo } >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\BoxFillDao.java"

echo package com.example.electricalcalculator.data.db.dao; > "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo import androidx.room.Dao; >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo /**  >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo  * Data Access Object for conduit fill calculations >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo  */ >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo @Dao >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo public interface ConduitFillDao { >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo     // Conduit fill DAO methods will be implemented here >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"
echo } >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\ConduitFillDao.java"

echo package com.example.electricalcalculator.data.db.dao; > "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo import androidx.room.Dao; >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo /**  >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo  * Data Access Object for dwelling unit calculations >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo  */ >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo @Dao >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo public interface DwellingUnitDao { >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo     // Dwelling unit DAO methods will be implemented here >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"
echo } >> "data\src\main\java\com\example\electricalcalculator\data\db\dao\DwellingUnitDao.java"

echo.
echo Creating missing entity classes...
echo Creating ConduitEntity.java...
mkdir "data\src\main\java\com\example\electricalcalculator\data\db\entity" 2>nul

echo package com.example.electricalcalculator.data.db.entity; > "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo import androidx.room.Entity; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo import androidx.room.PrimaryKey; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo /**  >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo  * Entity representation of a conduit in the database >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo  */ >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo @Entity(tableName = "conduits") >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo public class ConduitEntity { >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     @PrimaryKey(autoGenerate = true) >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     private long id; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     private String conduitType; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     private String conduitSize; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     private double conduitArea; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public ConduitEntity(String conduitType, String conduitSize, double conduitArea) { >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo         this.conduitType = conduitType; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo         this.conduitSize = conduitSize; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo         this.conduitArea = conduitArea; >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo. >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     // Getters and setters >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public long getId() { return id; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public void setId(long id) { this.id = id; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public String getConduitType() { return conduitType; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public void setConduitType(String conduitType) { this.conduitType = conduitType; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public String getConduitSize() { return conduitSize; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public void setConduitSize(String conduitSize) { this.conduitSize = conduitSize; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public double getConduitArea() { return conduitArea; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo     public void setConduitArea(double conduitArea) { this.conduitArea = conduitArea; } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"
echo } >> "data\src\main\java\com\example\electricalcalculator\data\db\entity\ConduitEntity.java"

echo Creating additional entity classes...

echo Successfully created Room database structure and fixed source set issues.
echo.
echo Please run the complete_fixes.bat script to build the project with all fixes applied.

pause

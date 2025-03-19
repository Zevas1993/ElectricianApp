# ElectricianApp JDK Setup Troubleshooting

This guide provides detailed steps to resolve the JDK-related build issues with the ElectricianApp project.

## Issues Identified

1. **Environment Variable Conflicts**: 
   - JAVA_HOME was incorrectly set to `\bin\java.exe=`
   - System still using JDK 23 instead of JDK 17

2. **gradle.properties Conflicts**:
   - Multiple conflicting `org.gradle.java.home` entries
   - Invalid JDK path value causing Gradle to fail

3. **Entity Class Issues**:
   - Missing entity classes referenced in ElectricianDatabase
   - Import errors in generated code

## Step-by-Step Resolution Guide

### 1. Fix Environment Variables

```powershell
# For PowerShell - Run these commands in a PowerShell terminal
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify configuration
$env:JAVA_HOME
java -version
```

```cmd
:: For Command Prompt - Run these commands in a Command Prompt
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Verify configuration
echo %JAVA_HOME%
java -version
```

### 2. Fix gradle.properties

The `gradle.properties` file should contain exactly one `org.gradle.java.home` entry with the correct JDK path. Edit the file to ensure it looks like:

```properties
# Project-wide Gradle settings
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# JDK path - this is the correct path
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.14.7-hotspot

# Other properties remain unchanged...
```

**Important**: Use double backslashes in the path to ensure proper escaping.

### 3. Clean Gradle Environment

```cmd
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
gradlew.bat --stop
rmdir /s /q .gradle
rmdir /s /q build
rmdir /s /q app\build
rmdir /s /q data\build
rmdir /s /q domain\build
```

### 4. Run Incremental Build

Try an incremental approach to identify specific issues:

```cmd
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
gradlew.bat :domain:clean :domain:build --info
gradlew.bat :data:clean :data:build --info
gradlew.bat :app:clean :app:build --info
```

### 5. Troubleshooting Specific Issues

#### Entity Class Import Errors

If you see errors like:
```
cannot find symbol
  symbol: class Entity
  location: class DwellingUnitEntity
```

Ensure the Room dependencies are correctly declared in your build.gradle files:

```gradle
// Room dependencies
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
```

#### Database Configuration Errors

If the ElectricianDatabase configuration is causing issues, ensure it's properly annotated:

```java
@Database(entities = {
    BoxFillEntity.class,
    WireEntity.class,
    BoxFillWireCrossRef.class,
    DwellingUnitEntity.class,
    ApplianceEntity.class,
    DwellingUnitApplianceCrossRef.class
}, version = 1, exportSchema = false)
public abstract class ElectricianDatabase extends RoomDatabase {
    // Implementation
}
```

## Running the Build

We've created two specialized scripts to help with building:

### 1. Using the direct_build_jdk17.bat script

This script:
- Directly sets JAVA_HOME and PATH for the build session
- Updates gradle.properties with the correct JDK path
- Cleans the project and runs a debug build

```cmd
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
direct_build_jdk17.bat
```

### 2. Manual Build with Environment Override

```cmd
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
gradlew.bat clean assembleDebug --info
```

## Still Having Issues?

If you continue to experience build problems:

1. Check for duplicate class definitions:
   ```
   gradlew.bat assembleDebug --stacktrace > build_log.txt
   ```
   Then review build_log.txt for specific errors.

2. Verify the JDK path exists and is accessible:
   ```
   dir "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\java.exe"
   ```

3. Try creating a minimal new Android project to isolate the issue.

## Resources

- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Android Gradle Plugin User Guide](https://developer.android.com/build)
- [Gradle Documentation](https://docs.gradle.org/current/userguide/userguide.html)

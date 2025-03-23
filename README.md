# ElectricianApp Project

## Overview
The ElectricianApp is an Android application designed to assist electricians with various calculations and references related to electrical work, including box fill calculations, conduit fill, and dwelling unit load calculations.

## Recent Fixes

### Gradle Configuration Fixes
1. **Removed Repository Duplication**
   - Eliminated duplicate repository declarations in build.gradle
   - Removed the redundant `allprojects` block that was causing conflicts

2. **Fixed Build File Issues**
   - Removed "ECHO is off" statements in build.gradle
   - Updated Gradle plugin version to 7.4.2 for better compatibility

3. **Module Structure Updates**
   - Corrected plugin syntax with modern Gradle plugin approach
   - Fixed sourceset configuration paths in all modules

### Kotlin Compatibility Fixes
1. **Kotlin Version Downgrade**
   - First downgraded from 1.9.22 to 1.8.0
   - Further downgraded to 1.7.0 for maximum compatibility with Android build tools
   - Added explicit kotlin_version variable in root build.gradle:
     ```gradle
     ext.kotlin_version = '1.7.0'
     ```

2. **Dependency Resolution Strategy**
   - Added forced resolution for Kotlin dependencies to ensure consistent versions:
     ```gradle
     configurations.all {
         resolutionStrategy {
             force "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
             force "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
             force "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
             force "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
         }
     }
     ```

3. **Expanded META-INF Exclusions**
   - Comprehensive META-INF exclusions in app/build.gradle:
     ```gradle
     packagingOptions {
         exclude 'META-INF/LICENSE.md'
         exclude 'META-INF/LICENSE-notice.md'
         exclude 'META-INF/*.kotlin_module'
         exclude 'META-INF/AL2.0'
         exclude 'META-INF/LGPL2.1'
     }
     ```

4. **Dexing Configuration**
   - Added dexOptions to increase heap size and disable pre-dexing:
     ```gradle
     dexOptions {
         javaMaxHeapSize "4g"
         preDexLibraries = false
     }
     ```

5. **Hilt Downgrade**
   - Downgraded Hilt from 2.48 to 2.44 for better compatibility with Kotlin 1.7.0
   - Updated both implementation and kapt dependencies
   - Updated the hilt-android-gradle-plugin in the root build.gradle

6. **CompileSdk Compatibility**
   - Added `android.suppressUnsupportedCompileSdk=34` to gradle.properties

### Configuration Files Updates
1. **Gradle Properties**
   - Added dependency version variables
   - Commented out hardcoded JDK path to avoid trailing space issues
   - Added proper parallel execution configuration

2. **Sourceset Configuration**
   - Created a centralized fix_sourceset.gradle approach
   - Applied it correctly to all modules with the proper relative paths

## Build Instructions

### Using the Enhanced Build Script
The easiest way to build the app is by using the provided build script with Kotlin fixes:

1. Open a command prompt in the project directory
2. Run `fix_sourceset_and_build.bat`
3. The script will:
   - Set up JDK 17
   - Clean Gradle caches and build directories
   - Clean Kotlin-specific caches
   - Set up proper Gradle options for Kotlin compatibility
   - Build the app with enhanced configuration

### Manual Build
If you prefer to build manually:

1. Make sure JDK 17 is installed and set as JAVA_HOME
2. Run `gradlew clean` to clear existing build files
3. Run `gradlew assembleDebug --refresh-dependencies --no-daemon` with the following options:
   - `-Dkotlin.incremental=false`
   - `-Dkotlin.compiler.execution.strategy=in-process`
   - `-Dkotlin.incremental.useClasspathSnapshot=false`

## Project Structure
- **app**: Main application module
- **domain**: Business logic and models
- **data**: Data sources, repositories, and database
- **feature/box**: Box fill calculation feature
- **feature/conduit**: Conduit fill calculation feature
- **feature/dwelling**: Dwelling unit load calculation feature

## Troubleshooting
If you encounter build issues:

1. **Kotlin Version Conflicts**
   - Check for conflicts with `gradlew app:dependencies`
   - Verify the Kotlin version is consistently 1.7.0 across dependencies

2. **Memory Issues**
   - Try increasing heap size: `-Dorg.gradle.jvmargs=-Xmx4g`
   - Disable daemon: `--no-daemon`

3. **JDK Compatibility**
   - Ensure you're using JDK 17
   - If possible, try JDK 17.0.2 which may have better compatibility than newer versions

4. **META-INF Conflicts**
   - Make sure all required exclusions are in packagingOptions

5. **Build Cache Issues**
   - Clear all Gradle caches in `%USERPROFILE%\.gradle\caches`
   - Remove all `build` directories

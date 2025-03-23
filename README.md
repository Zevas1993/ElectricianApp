# ElectricianApp Project

## Overview
The ElectricianApp is an Android application designed to assist electricians with various calculations and references related to electrical work, including box fill calculations, conduit fill, and dwelling unit load calculations.

## Recent Fixes

### Repository Configuration Fix
- Fixed conflict between `settings.gradle` and `build.gradle` repositories
- Error: "Build was configured to prefer settings repositories over project repositories but repository 'Google' was added by build file 'build.gradle'"
- Solution: Removed repository declarations from the `allprojects` block in `build.gradle` and moved Kotlin resolution strategy to `subprojects`

### Navigation Fragment Implementation
- Added stub implementations for missing fragments:
  - `PipeBendingFragment`
  - `LightingLayoutFragment`
  - `ArViewFragment`
- Created corresponding layout files with "Under Construction" messages
- This maintains the full navigation structure for future development

### Kotlin Compatibility Fixes
1. **Kotlin Version Downgrade**
   - Using version 1.7.0 for maximum compatibility with Android build tools
   - Explicitly declaring Kotlin version in root build.gradle

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

3. **META-INF Exclusions**
   - Comprehensive META-INF exclusions in app/build.gradle to prevent conflicts

4. **Dexing Configuration**
   - Added dexOptions to increase heap size and disable pre-dexing

5. **Hilt Downgrade**
   - Downgraded Hilt from 2.48 to 2.44 for better compatibility with Kotlin 1.7.0

## Build Instructions

### Using the Build Script
The easiest way to build the app is by using the provided build script:

1. Open a command prompt in the project directory
2. Run `fix_sourceset_and_build.bat`
3. The script will:
   - Set up JDK 17
   - Clean Gradle caches and build directories
   - Validate stub fragments
   - Build the app with proper configuration

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

## Future Development
The app now has stub implementations for planned features:
- Pipe bending calculations
- Lighting layout planning
- AR visualization

When ready to implement these features, you can expand the stub fragments with the actual functionality.

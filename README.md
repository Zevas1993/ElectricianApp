# ElectricianApp Build Guide

This guide explains the changes made to fix APK build issues and how to build the application successfully.

## Issues Fixed

1. **Gradle/AGP Version Compatibility**
   - **Issue**: Android Studio requires Gradle 8.10 to work with Java 21
   - **Fix**: Updated Gradle to 8.10 and Android Gradle Plugin to 8.2.2

2. **Java Compatibility**
   - **Issue**: Java 21 requires configuration in build files
   - **Fix**: Updated Java settings to target Java 17 compatibility (works with Java 21)

3. **Missing Version Code**
   - **Issue**: The app/build.gradle was missing a versionCode in defaultConfig
   - **Fix**: Added versionCode 1 to complete the configuration

4. **App Label Consistency**
   - **Issue**: The AndroidManifest.xml had a hardcoded app label
   - **Fix**: Updated to use the @string/app_name resource for consistency

5. **Enhanced App Description**
   - **Addition**: Added comprehensive app descriptions for all features
   - **Result**: App now has proper descriptions for Play Store listings and in-app documentation

6. **Dependency Variable Consistency**
   - **Issue**: Variable name mismatch for constraintlayout in feature modules
   - **Fix**: Fixed the variable name to match gradle.properties

## How to Build the APK

### Option 1: Using the Java 21 Build Script (Recommended)

1. Navigate to the project root directory
2. Run the new build script:
   ```
   .\build_java21.bat
   ```

This script will:
- Find Java 21 installation
- Set up the correct environment variables
- Clean and build the project with Gradle 8.10
- Provide detailed error information if anything fails

### Option 2: Building in Android Studio

1. Open the project in Android Studio
2. Ensure you have Java 21 installed and configured in Android Studio
3. Let Gradle sync complete successfully
4. Build the APK using Build > Build Bundle(s) / APK(s) > Build APK(s)

### Option 3: Manual Build

1. Ensure you have Java 21 installed
2. Set your JAVA_HOME environment variable:
   ```
   set JAVA_HOME=C:\path\to\your\java21
   ```
3. Run the Gradle wrapper commands:
   ```
   gradlew.bat clean
   gradlew.bat assembleDebug
   ```

## App Features

The ElectricianApp includes the following features:

- **Box Fill Calculator**: Calculate box volume and fill capacity according to NEC requirements
- **Conduit Fill Calculator**: Determine appropriate conduit sizes based on conductor requirements
- **Dwelling Load Calculator**: Complete dwelling unit load calculations per NEC Article 220
- **Pipe Bending Calculator**: Calculate precise measurements for conduit bends
- **Lighting Layout Tools**: Design efficient lighting layouts with calculation tools
- **AR Visualization**: Visualize electrical installations in real-time using augmented reality
- **Tap Counter**: Utility tool for counting taps when locating wires or breakers
- **Fraction Calculator**: Specialized calculator for working with fractions and mixed numbers
- **Job Checklists**: Customizable checklists for installation, inspection, and maintenance tasks
- **Inventory Management**: Track tools, materials, and equipment for projects
- **Job Scheduling**: Plan and schedule jobs, track time, and coordinate with team members

## Troubleshooting

If you encounter build issues:

1. **Java Problems**: Make sure you have Java 8 or 11 installed and JAVA_HOME correctly set
2. **Gradle Sync Issues**: Delete the .gradle directory and try again
3. **Missing Dependencies**: Check that you're connected to the internet for dependency downloads
4. **APK Not Found**: Look in app/build/outputs/apk/debug/ for the generated APK

For more detailed help, see the error messages in the build output.

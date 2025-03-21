# ElectricianApp Build Guide

This guide provides instructions for building the ElectricianApp Android project.

## Quick Start

The simplest way to build the app is to run the desktop shortcut:

```cmd
build_electrician_app.cmd
```

This script will automatically:
1. Fix JDK configuration issues
2. Fix Gradle repository configuration 
3. Clean build caches
4. Run a complete build

## Build Issues Addressed

The build script fixes several common issues:

### 1. JDK Configuration

- Properly configures JDK 17 paths without trailing spaces
- Sets Java environment variables correctly
- Ensures compatibility with Android Gradle Plugin

### 2. Repository Configuration

- Configures repositories in settings.gradle only (not in build.gradle)
- Resolves the "Build was configured to prefer settings repositories over project repositories" error
- Sets proper repository mode in settings.gradle

### 3. Gradle Cache Cleanup

- Cleans problematic build artifacts
- Removes corrupted transform caches
- Ensures a clean build environment

## Manual Build (Advanced)

If you need to run the script directly, you can access it at:

```
C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp\scripts\fix_build.bat
```

## Project Structure

The project follows a clean architecture approach:

- `/app` - Android application module
- `/domain` - Business logic and models
- `/data` - Data sources and repositories
- `/feature` - Feature modules (box, conduit, dwelling)

## Requirements

- JDK 17 (Eclipse Temurin/AdoptOpenJDK)
- Android SDK
- Gradle 8.x+

## Android Studio Setup

After running the build script, you can open the project in Android Studio:

1. Open Android Studio
2. File > Open > Select the ElectricianApp directory
3. Wait for Gradle sync to complete
4. Run the app on an emulator or physical device

## Troubleshooting

If you encounter build issues:

1. Verify JDK 17 is installed at:
   `C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot`

2. Delete the `.gradle` directory in your project and try again

3. Ensure Android SDK is properly installed and configured

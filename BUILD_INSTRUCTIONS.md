# ElectricianApp Build Instructions

This document provides instructions for building the ElectricianApp project using the provided scripts. These scripts have been created to address various issues with the project's JDK configuration, source sets, and build process.

## Available Build Scripts

### Option 1: CMD Scripts (Recommended for Command Prompt)

#### 1. `run_ultimate_fix.cmd` (MOST RECOMMENDED - COMPLETE SOLUTION)

The ultimate, complete solution that addresses all build issues:
- Fixes both settings.gradle AND build.gradle files correctly
- Resolves the repository configuration conflict ("prefer settings repositories over project repositories")
- Sets up proper dependency resolution management
- Sets up JDK 17 correctly and thoroughly cleans the build environment
- Ensures all Java/Kotlin configurations are properly applied
- Completely modular approach that preserves project structure

**Usage:**
```cmd
run_ultimate_fix.cmd
```

#### 2. `run_final_fix.cmd`

A previous solution that addresses many but not all issues:
- Fixes the syntax in build.gradle but encounters repository conflicts
- Properly applies Android plugin only to subprojects
- Sets up JDK 17 correctly and thoroughly cleans the build environment

**Usage:**
```cmd
run_final_fix.cmd
```

#### 2. `run_gradle_fix.cmd`

An earlier attempt that had a syntax error in the build.gradle file:
- The approach was correct but had a typo in the buildscript section
- May still encounter errors during build

**Usage:**
```cmd
run_gradle_fix.cmd
```

#### 2. `run_minimal_fix.cmd`

An alternative solution that attempts a minimal approach:
- Focuses only on the JDK configuration without extensive modifications
- Properly sets up JDK 17 and cleans the environment first
- Uses direct JDK path specification for Gradle
- May still encounter the "Could not find method android()" error

**Usage:**
```cmd
run_minimal_fix.cmd
```

#### 2. `run_fixed_build.cmd`

An alternative solution that attempts to fix the build.gradle files:
- Uses an improved build script that fixes the critical issues with the original
- Attempts to handle Android configuration in the build.gradle file
- Correctly navigates to your project directory

**Usage:**
```cmd
run_fixed_build.cmd
```

#### 2. `fix_electrician_app.cmd`

The original simple solution - this CMD script automatically:
- Navigates to your project directory
- Runs the original fix_sourceset_and_build.bat script

**Usage:**
```cmd
fix_electrician_app.cmd
```

#### 3. `build_electrician_app.bat` 

A wrapper script for the custom run_build.bat implementation.

**Usage:**
```cmd
build_electrician_app.bat
```

### Option 2: PowerShell Scripts (Recommended for PowerShell)

#### 1. `run_electrician_app_build.ps1`

A PowerShell script that:
- Sets up JDK 17 environment variables
- Navigates to your project directory 
- Directly runs the original fix_sourceset_and_build.bat script

**Usage:**
```powershell
.\run_electrician_app_build.ps1
```

#### 2. `Build-ElectricianApp.ps1`

An alternative PowerShell implementation.

**Usage:**
```powershell
.\Build-ElectricianApp.ps1
```

### Option 3: Original Build Script

The original script can be run directly from the project directory:

**Usage:**
```cmd
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
fix_sourceset_and_build.bat
```

## Issues Fixed by These Scripts

1. **JDK Configuration**
   - Sets JAVA_HOME to the correct JDK 17 path
   - Updates PATH to include JDK 17 bin directory
   - Updates gradle.properties with the correct JDK path

2. **Source Set Issues**
   - Fixes package name mismatches between declared packages and expected packages
   - Properly configures source directories
   - Ensures Java and Kotlin files are correctly included

3. **Build Configuration**
   - Applies source set fixes to all modules
   - Updates build.gradle files with necessary configurations
   - Properly sets up kapt for Room annotation processing

## Troubleshooting

If you encounter issues after running these scripts:

1. **Verify JDK 17 Installation**
   - Ensure JDK 17 is installed at: `C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot`
   - If installed elsewhere, update the JDK_PATH variable in the scripts

2. **Clean Build Issues**
   - Run the scripts again to perform a fresh clean build
   - Check the logs for specific error messages

3. **Android Studio Integration**
   - After running the build scripts, you may need to:
     - Restart Android Studio
     - Refresh the Gradle project
     - Mark appropriate directories as source roots

For more detailed troubleshooting, refer to the `JDK_TROUBLESHOOTING.md` file in the project.

# Java Configuration Guide for ElectricianApp

This guide provides step-by-step instructions for fixing Java environment issues to successfully build the ElectricianApp.

## Quick Fix: Run the Provided Scripts

For the quickest fix, use the provided scripts in this order:

1. **Fix JAVA_HOME** - First, run the JAVA_HOME fixer:
   ```
   .\run_fix_java_home.bat
   ```
   This will:
   - Automatically find JDK installations on your system
   - Select an appropriate version (preferring JDK 11 or 17)
   - Set JAVA_HOME system environment variable correctly
   - Update your PATH to include the Java bin directory

2. **Build the APK** - After fixing JAVA_HOME, build the APK:
   ```
   .\build_compatible_apk.bat
   ```
   This will:
   - Verify your JAVA_HOME is correct
   - Ensure Gradle wrapper files are present
   - Configure compatible Gradle settings
   - Build a debug APK

## Understanding JAVA_HOME Error

The error `JAVA_HOME is set to an invalid directory: \bin\java.exe=` indicates your JAVA_HOME environment variable is pointing to the Java executable instead of the JDK root directory.

### Correct vs. Incorrect JAVA_HOME

- **Correct**: `C:\Program Files\Java\jdk-17.0.2`
- **Incorrect**: `C:\Program Files\Java\jdk-17.0.2\bin\java.exe=`

## Manual Fix Options

### Option 1: Using Windows Settings

1. Search for "environment variables" in Windows
2. Select "Edit the system environment variables"
3. Click "Environment Variables"
4. Under "System variables," find JAVA_HOME
5. Edit it to point to your JDK root folder

### Option 2: PowerShell Command

Run this PowerShell command as Administrator:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17.0.2", "Machine")
```

(Replace the path with your actual JDK location)

### Option 3: Command Prompt

Run this Command Prompt command as Administrator:

```cmd
SETX JAVA_HOME "C:\Program Files\Java\jdk-17.0.2" /M
```

(Replace the path with your actual JDK location)

## Verifying Your Fix

After fixing JAVA_HOME, open a **new** command prompt and run:

```cmd
echo %JAVA_HOME%
```

It should display the correct JDK path.

## Troubleshooting

- **"JDK not found"**: You may need to install a compatible JDK (version 11 or 17 recommended)
- **Permission issues**: Make sure you run the scripts as Administrator
- **Gradle wrapper errors**: If you see Gradle wrapper errors after fixing JAVA_HOME, run `fix_gradle_wrapper.bat`

## Additional Information

### Compatible JDK Versions

For ElectricianApp development:
- **Recommended**: JDK 11 or JDK 17
- **Not Compatible**: JDK 23 (too new for the current Gradle version)

### Download Links

If you need to install a compatible JDK:

- **AdoptOpenJDK 11**: [Download](https://adoptium.net/temurin/releases?version=11)
- **AdoptOpenJDK 17**: [Download](https://adoptium.net/temurin/releases?version=17)

### Important Notes

1. **Restart Required**: After changing environment variables, restart any open terminals and applications
2. **Android Studio**: If using Android Studio, you may need to set the JDK in Settings > Build, Execution, Deployment > Build Tools > Gradle
3. **Path Variable**: If you're still having issues, check that the JDK's `bin` directory is in your PATH

# Android Studio Setup Guide for ElectricianApp

This guide will help you resolve common issues when opening and building the ElectricianApp in Android Studio.

## Fixing "Could not load wrapper properties" Error

If you're seeing the error:
> "Could not load wrapper properties from 'C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp\gradle\wrapper\gradle-wrapper.properties'."

This has been fixed by running the `fix_gradle_wrapper.bat` script which:
1. Downloads the necessary gradle-wrapper.jar file
2. Creates a proper gradle-wrapper.properties file
3. Creates the gradlew.bat script

## Additional Android Studio Configuration

Follow these steps to ensure a smooth setup in Android Studio:

### 1. Configure JDK Settings

Android Studio needs to use a compatible JDK version:

1. Open Android Studio Settings (File > Settings)
2. Go to Build, Execution, Deployment > Build Tools > Gradle
3. Set "Gradle JDK" to JDK 11 or JDK 17 (not JDK 23)
4. Apply changes

### 2. Sync Project

After the JDK configuration:

1. Click "Sync Project with Gradle Files" button (elephant icon with arrow in toolbar)
2. Wait for sync to complete
3. If errors persist, try "File > Invalidate Caches / Restart..."

### 3. Update build.gradle Files (if needed)

If you still see compatibility issues, manually update these settings in each module's build.gradle:

```gradle
android {
    // Other settings...
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = '11'
    }
}
```

### 4. Install Missing SDK Components

Android Studio might prompt you to install missing SDK components. Always accept these installations as they're required to build the project.

## Building the APK

Once the project opens successfully:

1. Select "Build > Build Bundle(s) / APK(s) > Build APK(s)"
2. Wait for the build to complete
3. Click the notification "APK(s) generated successfully" to locate the APK file
4. The APK will be at: `/app/build/outputs/apk/debug/app-debug.apk`

## Installing on a Device

To install on a connected device:

1. Enable USB debugging on your Android device
2. Connect device via USB
3. In Android Studio, select "Run > Run 'app'" (or click the green play button)
4. Alternatively, use the command: `adb install app/build/outputs/apk/debug/app-debug.apk`

## Troubleshooting Common Issues

### Issue: Java version conflicts

**Solution**: Ensure you're using JDK 11 or JDK 17, not JDK 23. You can download JDK 11 from:
- [AdoptOpenJDK](https://adoptopenjdk.net/)
- [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

### Issue: Gradle sync fails with errors

**Solution**:
1. Check Gradle log output in the "Build" window
2. Verify Internet connection (to download dependencies)
3. Try "File > Invalidate Caches / Restart..."
4. Run the fix_gradle_wrapper.bat script again

### Issue: Resource not found errors

**Solution**: 
1. Verify resources exist in correct directories
2. Run "Build > Clean Project" followed by "Build > Rebuild Project"

## Next Steps

After building a successful APK:

1. Test all features for functionality
2. Make any necessary code changes using Android Studio's editor
3. Continue development following the modular architecture described in PROJECT_STRUCTURE.md

---

For further assistance, refer to the official [Android Studio documentation](https://developer.android.com/studio/intro).

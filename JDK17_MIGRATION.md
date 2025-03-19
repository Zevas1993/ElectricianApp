# ElectricianApp JDK 17 Migration

This document outlines the changes made to fix the ElectricianApp project and enable it to build properly with JDK 17.

## Environment Setup

The project has been configured to use JDK 17 (Temurin/AdoptOpenJDK) with the following environment variables:

- **JAVA_HOME**: `C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot`
- **PATH**: Includes `%JAVA_HOME%\bin` at the top of the path entries

## Key Changes Applied

### 1. Entity Classes

Added missing entity classes that were referenced in the database but were not implemented:

- `DwellingUnitEntity.java`
- `ApplianceEntity.java`
- `DwellingUnitApplianceCrossRef.java`

These entity classes provide the necessary Room database definitions for the app's data layer.

### 2. ElectricianDatabase Configuration

Updated the ElectricianDatabase class to:

- Include all entity classes in the @Database annotation
- Set `exportSchema = false` to avoid schema export generation issues
- Properly configure the singleton pattern for database instance creation

### 3. Gradle Configurations

#### Room Dependencies

Updated Room dependencies to use explicit version numbers in data/build.gradle:

```gradle
// Room
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
```

#### Hilt Dependencies

Added Hilt dependency injection to multiple modules:

- Added to app/build.gradle:
  ```gradle
  plugins {
      id 'kotlin-kapt'
      id 'dagger.hilt.android.plugin'
  }
  
  dependencies {
      implementation 'com.google.dagger:hilt-android:2.48'
      kapt 'com.google.dagger:hilt-android-compiler:2.48'
      implementation 'javax.inject:javax.inject:1'
  }
  ```

#### BuildConfig Configuration

Added `buildConfig true` to Android build features in app/build.gradle to fix deprecation warnings:

```gradle
buildFeatures {
    viewBinding true
    buildConfig true
}
```

### 4. XML Layout Fixes

Fixed XML escaping in layout files, notably:

- Properly escaped quotation marks in `fragment_conduit_fill_results.xml` using `&quot;` instead of backslash escaping

### 5. Java Version Configuration

Explicitly set Java compatibility to JDK 17 in all modules:

```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = '17'
}
```

Added JDK configuration to gradle.properties:

```
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.14.7-hotspot
```

## Build Scripts

Created a specialized build script `build_with_jdk17.bat` that:

1. Verifies the correct JDK is being used
2. Adds the JDK path to gradle.properties
3. Performs a clean build
4. Runs assembleDebug with detailed logging

## Known Issues

1. When running the build for the first time, you may see KAPT errors. These are typically resolved by running a clean build again, as the generated code needs to be processed.

2. If you encounter "duplicate class" errors, they may be due to:
   - Multiple modules trying to define the same class
   - Generated code conflicts

3. AndroidManifest.xml issues can arise if the namespace and package attributes are not aligned across modules.

## Troubleshooting

If you encounter build issues:

1. Run with detailed logging: `gradlew assembleDebug --stacktrace`
2. Check for proper Hilt annotations throughout the project
3. Verify all AndroidManifest.xml files are properly configured
4. Use Android Studio's build tools to get more detailed error messages

## Resources

- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Android Gradle Plugin Migration Guide](https://developer.android.com/build/releases/gradle-plugin)

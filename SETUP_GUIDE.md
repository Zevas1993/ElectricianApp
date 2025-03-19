# ElectricianApp Setup Guide

This guide provides detailed instructions for building, installing, and running the ElectricianApp with the modernized architecture and optimizations.

## Java Version Compatibility Issue

The primary build error you may encounter is related to Java version incompatibility:

```
BUG! exception in phase 'semantic analysis' in source unit '_BuildScript_' Unsupported class file major version 67
```

This occurs because:
- Gradle 8.7 is not fully compatible with JDK 23 (version 67)
- The project needs JDK 17 for proper compatibility

## Solution: Using the JDK Setup Script

To fix this issue, a `setup_jdk.bat` script has been created that will:
1. Download and install AdoptOpenJDK 17 locally in the project directory
2. Create a `build_with_jdk17.bat` script that uses this JDK for building

### Quick Start

1. Run the setup script to install JDK 17:
   ```
   .\setup_jdk.bat
   ```

2. After installation completes, use the generated build script:
   ```
   .\build_with_jdk17.bat
   ```

3. The APK will be generated at:
   ```
   app\build\outputs\apk\debug\app-debug.apk
   ```

## Manual Alternative

If you already have JDK 17 installed, you can create your own build script with:

```batch
@echo on
set JAVA_HOME=C:\path\to\your\jdk17
set PATH=%JAVA_HOME%\bin;%PATH%
call gradlew.bat --info assembleDebug
```

## Installing the APK

To install the generated APK on a connected device:

```
adb install app\build\outputs\apk\debug\app-debug.apk
```

If ADB is not in your PATH, you can find it at:
`[Android SDK location]\platform-tools\adb.exe`

## Project Organization

The project has been reorganized with a clean modular architecture:

```
ElectricianApp/
├── app/                  # Main application module
├── domain/               # Domain layer (models, repositories interfaces, use cases)
├── data/                 # Data layer (repository implementations, database)
└── feature/              # Feature modules
    ├── box/              # Box fill calculations
    ├── conduit/          # Conduit fill calculations
    └── dwelling/         # Dwelling load calculations
```

## Dependencies and Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 1.9.22 | Programming language |
| Room | 2.6.1 | Local database for calculations |
| Navigation | 2.7.7 | Fragment navigation management |
| Lifecycle | 2.7.0 | Android lifecycle handling |
| Material | 1.11.0 | UI components |
| Core KTX | 1.12.0 | Kotlin extensions for Android |

## Troubleshooting

### Common Issues

1. **Build Fails with Java Version Error**:
   - Use the provided `setup_jdk.bat` script
   - Ensure you're using JDK 17, not newer versions

2. **Missing Dependencies**:
   - Run `gradlew --refresh-dependencies` to force dependency refresh
   - Check your network connection for downloading dependencies

3. **Installation Fails**:
   - Ensure USB debugging is enabled on your device
   - Check if a previous version needs to be uninstalled first
   - Verify adb connection with `adb devices`

## Next Steps

1. **Add New Features**: Follow the existing architecture for adding new features
2. **Enhance UI**: Implement Material 3 design components
3. **Implement Testing**: Add unit and instrumentation tests
4. **Optimize Images**: Convert images to WebP format for better compression

## Support

If you encounter any issues or have questions about the app, please open an issue on the GitHub repository.

---

Created with the latest Android development best practices and optimizations.

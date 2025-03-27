# ElectricianApp Project

## Overview
The ElectricianApp is an Android application designed to assist electricians with various calculations and references related to electrical work, including box fill calculations, conduit fill, and dwelling unit load calculations.

## Recent Fixes

### Gradle Configuration Updates
- Updated Android Gradle Plugin (AGP) to version 8.2.2 for compatibility with the latest tools.
- Updated Kotlin Gradle Plugin to version 1.9.22.
- Updated dependencies in `build.gradle`:
  - `androidx.core:core-ktx:1.12.0`
  - `androidx.appcompat:appcompat:1.6.1`
  - `com.google.android.material:material:1.11.0`
  - `androidx.constraintlayout:constraintlayout:2.1.4`
- Enabled ViewBinding in the app module.

### XML Layout Fixes
- Added unique `android:id` attributes to components in `activity_main.xml` to prevent runtime errors.
- Introduced a `BottomNavigationView` for navigation between screens.

### Code Logic Enhancements
- Improved the voltage drop calculation logic in `MainActivity.kt`:
  - Added input validation to ensure all fields are filled.
  - Used a more realistic formula for voltage drop calculation.
  - Displayed results with proper formatting.

### New Features
#### Navigation
- Added a `BottomNavigationView` for seamless navigation between fragments.
- Created a `bottom_nav_menu.xml` for navigation items.

#### Data Persistence
- Implemented `SharedPreferences` to save and retrieve the last calculation result.

### Build Instructions

#### Standard Build
1. Open the project in Android Studio.
2. Sync the Gradle files.
3. Run the app on a connected device or emulator.

#### Manual Build
1. Ensure JDK 17 is installed and set as `JAVA_HOME`.
2. Run the following commands:
   - `gradlew clean`
   - `gradlew assembleDebug --no-daemon`

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

## Recommended Improvements
1. Add unit tests for voltage drop calculations.
2. Enhance UI/UX with loading spinners and `Snackbar` for error messages.
3. Gradually migrate from KAPT to KSP for annotation processing.

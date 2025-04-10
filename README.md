# ElectricianApp Project

## Overview
The ElectricianApp is an all-in-one Android application designed to assist electricians with various calculations, code references, and project management tools. It includes features for box fill calculations, conduit fill, dwelling unit load calculations, voltage drop calculations, NEC code lookup, and more.

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

## Implemented Features

### Field Documentation Tools
- **Photo Documentation**: Take and organize photos, add annotations, create before/after comparisons
  - Photo capture and import functionality
  - Photo organization by job, date, and tags
  - Annotation tools for marking up photos
  - Before/after comparison views

### Material Management
- **Material Inventory**: Track materials, quantities, and locations
  - Inventory tracking with minimum quantity alerts
  - Transaction history for all inventory movements
  - Location tracking for materials
  - Low stock alerts and filtering
- **Material Lists**: Generate and manage material lists for jobs
  - Create lists from calculations or manually
  - Export lists for ordering
  - Track material usage by job
- **Supplier Management**: Manage supplier information and price quotes
  - Store supplier contact information
  - Track pricing information
  - Preferred supplier designation

### Calculation Tools
- **Box Fill Calculator**: Calculate box fill requirements per NEC code
  - Support for different box types and sizes
  - Automatic calculation based on NEC requirements
  - Validation against code requirements
- **Conduit Fill Calculator**: Determine conduit fill percentages
  - Support for different conduit types and sizes
  - Wire combination calculations
  - NEC Chapter 9, Table 1 compliance
- **Dwelling Load Calculator**: Calculate electrical loads for residential buildings
  - General lighting load calculations
  - Appliance load calculations
  - Demand factor application
- **Voltage Drop Calculator**: Calculate voltage drop in electrical circuits
  - Support for different conductor types and sizes
  - Single-phase and three-phase calculations
  - NEC recommended limits validation
- **Luminaire Calculator**: Calculate required number of luminaires for a space
  - Room size and reflectance factors
  - Illuminance requirements by space type
  - Fixture selection assistance
- **Pipe Bending Calculator**: Calculate measurements for conduit bends
  - Offset, saddle, and 90-degree bends
  - Take-up calculations
  - Multiplier reference

### Reference Tools
- **NEC Code Lookup**: Search and browse NEC articles, check code violations, and track updates
  - Searchable code database
  - Bookmarking important sections
  - Code update tracking

### Project Management
- **Job Management**: Create and manage job information, track status and details
  - Job creation and tracking
  - Status updates and history
  - Client information management
  - Task assignment and tracking

## Future Development
The app has plans for additional features:
- Advanced calculation tools (Wire Pulling, Fault Current, Transformer Sizing, etc.)
- Field tools (Panel Schedule Creator, Circuit Tracer Companion, etc.)
- Advanced technology integration (AR Visualization, Voice Commands, Cloud Sync, etc.)
- Client communication tools
- Training and education modules

See the FEATURES.md file for a complete list of implemented and planned features.

## Recommended Improvements
1. Add comprehensive unit tests for all calculation features.
2. Enhance UI/UX with loading spinners and `Snackbar` for error messages.
3. Gradually migrate from KAPT to KSP for annotation processing.
4. Implement offline data synchronization for the NEC code database.
5. Add user authentication and cloud backup for calculations and job data.
6. Improve accessibility features for users with disabilities.
7. Add multi-language support for international users.

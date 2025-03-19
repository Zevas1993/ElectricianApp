# ElectricianApp Project Structure

This document describes the modular architecture of the ElectricianApp project after reorganization.

## Project Overview

ElectricianApp is a comprehensive tool for electricians that includes various calculation features:

- Box Fill Calculator
- Conduit Fill Calculator 
- Dwelling Load Calculator
- (More features planned)

## Module Architecture

The project follows a clean architecture with a modular approach:

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

## Module Details

### App Module

The app module is the main application that ties together all other modules:

- Contains the `MainActivity` and application class
- Navigation graph that connects all features
- Dependency injection setup
- Shared resources

### Domain Module

The domain module contains pure business logic organized by feature:

- **Models**: `boxfill/`, `conduitfill/`, `dwellingload/`
- **Repository Interfaces**: `repository/boxfill/`, `repository/conduitfill/`, `repository/dwellingload/`
- **Use Cases**: `usecase/boxfill/`, `usecase/conduitfill/`, `usecase/dwellingload/`

### Data Module

The data module implements the repository interfaces from the domain module:

- Database implementation with Room
- Entity definitions and DAOs
- Repository implementations

### Feature Modules

Each feature is contained in its own module with:

- UI components (fragments, adapters)
- ViewModels
- Feature-specific resources (layouts, strings)

## Building the Project

To build the APK:

1. Use the `build_compatible.bat` script which automatically sets up JAVA_HOME and uses the right Gradle version
2. The APK file will be generated at `app/build/outputs/apk/debug/app-debug.apk`

### Java Compatibility

The project has been updated to work with modern Java versions:
- Gradle updated to 8.7
- Android Gradle Plugin updated to 8.2.0
- Clean task updated for Gradle 8.x compatibility

If you encounter Java compatibility issues, check:
1. Your JAVA_HOME environment variable
2. That you're using the build_compatible.bat script (not the original build_apk.bat)

## Development Workflow

When adding new features:

1. Add domain models to the domain module
2. Add repository interfaces to the domain module
3. Implement repositories in the data module
4. Create a new feature module or extend an existing one
5. Update the navigation graph to include the new feature

## Package Naming Convention

- Main package: `com.example.electricalcalculator`
- Feature packages: `com.example.electricalcalculator.feature.<feature_name>`
- Domain packages: `com.example.electricalcalculator.domain.<layer>.<feature_name>`
- Data packages: `com.example.electricalcalculator.data.<component>.<feature_name>`

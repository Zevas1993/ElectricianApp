# ElectricianApp

An all-round app for electricians to perform common electrical calculations according to NEC standards.

## Features

- **Box Fill Calculations**: Calculate box fill requirements for electrical boxes
- **Conduit Fill Calculations**: Determine appropriate conduit sizes based on wire configurations
- **Dwelling Load Calculations**: Calculate residential electrical load requirements
- **AR Visualization**: Visualize electrical components in augmented reality

## Project Structure

The application follows a modular, clean architecture approach:

```
.
├── app/                 # Main application module
├── data/                # Data layer (Room DB, Repositories)
├── domain/              # Domain layer (Business logic, Models, UseCases)
└── feature/             # Feature modules
    ├── box/             # Box fill calculation feature
    ├── conduit/         # Conduit fill calculation feature
    └── dwelling/        # Dwelling load calculation feature
```

## Requirements

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or higher
- Android 5.0+ (API level 21+)
- Android device with AR support for AR features

## Building and Running

### Build Debug APK

1. Clone the repository:
   ```
   git clone https://github.com/Zevas1993/ElectricianApp.git
   cd ElectricianApp
   ```

2. Run the build script:
   ```
   # Windows
   .\build_apk.bat
   
   # Linux/macOS
   ./gradlew clean assembleDebug
   ```

3. The APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Install Directly to Device

Ensure you have a device connected with USB debugging enabled:

```
./gradlew installDebug
```

## Architecture

The application follows Clean Architecture principles with the following layers:

- **Presentation Layer**: MVVM pattern with ViewModels, Fragments, and Activities
- **Domain Layer**: Contains business logic, use cases, and domain models
- **Data Layer**: Implements repositories and data sources (Room database)

## Dependencies

- **AndroidX**: Core, AppCompat, ConstraintLayout
- **Navigation Component**: For navigation between screens
- **Room**: For local database storage
- **ViewModel & LiveData**: For UI state management
- **Hilt**: For dependency injection
- **ARCore**: For augmented reality features
- **Material Design Components**: For UI

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

# ElectricianApp
An all round app for electricians

Here’s how you can begin restructuring your Android app into a modular format:

### 1. **Create the Modules**:

In your root project directory, create the following modules:

```bash
./app/             # Main app module (UI and navigation)
./data/            # Data module (Room DB, Repositories)
./domain/          # Domain module (Business logic, UseCases, Models)
./feature/conduit  # Conduit fill feature module
./feature/dwelling # Dwelling load feature module
./feature/box      # Box fill feature module
```

### 2. **Modify `settings.gradle`**:
Include these modules in `settings.gradle`:

```gradle
include ':app', ':data', ':domain', ':feature:conduit', ':feature:dwelling', ':feature:box'
```

### 3. **Dependencies in `build.gradle`**:
Ensure proper dependency linking between modules in the `build.gradle` files.

#### **In `app/build.gradle`**:

```gradle
dependencies {
    implementation project(":data")
    implementation project(":domain")
    implementation project(":feature:conduit")
    implementation project(":feature:dwelling")
    implementation project(":feature:box")
}
```

#### **In `data/build.gradle`**:

```gradle
dependencies {
    implementation "androidx.room:room-runtime:2.4.3"
    implementation project(":domain")
}
```

#### **In `domain/build.gradle`**:

```gradle
dependencies {
    implementation project(":data")
}
```

### 4. **Structure the Code Inside Modules**:

#### **App Module**: Contains core resources and navigation.

```kotlin
// In AppModule (MainActivity.kt)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Navigation and common UI elements
    }
}
```

#### **Data Module**: Handles Room database and repositories.

```kotlin
// In DataModule (RoomDatabase.kt)
@Database(entities = [Wire::class], version = 1)
abstract class WireDatabase : RoomDatabase() {
    abstract fun wireDao(): WireDao
}
```

#### **Domain Module**: Holds business logic and use cases.

```kotlin
// In DomainModule (CalculateConduitFillUseCase.kt)
class CalculateConduitFillUseCase(private val wireRepository: WireRepository) {
    fun execute(wire: Wire): Double {
        // Conduit fill calculation logic
        return wire.diameter * Math.PI * wire.length
    }
}
```

#### **Feature Modules**: Contains feature-specific logic.

```kotlin
// In FeatureConduit (ConduitFillViewModel.kt)
class ConduitFillViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WireRepository = WireRepositoryImpl(application)
    val result = MutableLiveData<Double>()

    fun calculateConduitFill(wire: Wire) {
        val useCase = CalculateConduitFillUseCase(repository)
        result.value = useCase.execute(wire)
    }
}
```

### 5. **Sync and Build**:
After setting up, sync the project and make sure each module is properly referenced.

---

This setup will modularize your app, improve scalability, and allow for better separation of concerns.

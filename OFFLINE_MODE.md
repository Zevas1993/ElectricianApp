# Offline Mode Implementation Guide for ElectricianApp

This document provides guidance on implementing offline functionality for the ElectricianApp, allowing electricians to use the app in the field without a constant internet connection.

## Architecture Overview

The offline mode implementation follows a single source of truth pattern with the local database as the primary data source:

1. **Local Database**: Stores all app data and serves as the single source of truth
2. **Remote Data Source**: Fetches data from the server when online
3. **Repository**: Coordinates between local and remote data sources
4. **Sync Manager**: Handles synchronization of local changes with the server

## Database Setup

Use Room database with entities that support offline operations:

```kotlin
@Entity(tableName = "material_inventory")
data class MaterialInventoryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "material_id") val materialId: String,
    val quantity: Double,
    @ColumnInfo(name = "minimum_quantity") val minimumQuantity: Double,
    val location: String,
    val notes: String,
    @ColumnInfo(name = "last_updated") val lastUpdated: Date,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class SyncStatus {
    SYNCED,      // Data is in sync with the server
    PENDING,     // Local changes need to be synced to the server
    CONFLICT     // Local changes conflict with server changes
}
```

## Repository Implementation

Implement repositories that prioritize local data and handle synchronization:

```kotlin
class MaterialInventoryRepository @Inject constructor(
    private val localDataSource: MaterialInventoryLocalDataSource,
    private val remoteDataSource: MaterialInventoryRemoteDataSource,
    private val networkManager: NetworkManager,
    private val syncManager: SyncManager
) {
    // Get inventory items - always from local database
    fun getInventoryItems(): Flow<List<MaterialInventory>> {
        return localDataSource.getInventoryItems()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    // Save inventory item - save locally and mark for sync
    suspend fun saveInventoryItem(inventory: MaterialInventory) {
        val entity = inventory.toEntity().copy(syncStatus = SyncStatus.PENDING)
        localDataSource.saveInventoryItem(entity)
        
        // Try to sync immediately if online
        if (networkManager.isOnline()) {
            syncManager.syncInventoryItem(inventory.id)
        }
    }
    
    // Sync all pending inventory items with the server
    suspend fun syncPendingItems() {
        if (!networkManager.isOnline()) return
        
        val pendingItems = localDataSource.getPendingInventoryItems()
        pendingItems.forEach { entity ->
            try {
                val response = remoteDataSource.saveInventoryItem(entity.toDomain())
                // Update local item with server response and mark as synced
                localDataSource.saveInventoryItem(
                    entity.copy(
                        lastUpdated = response.lastUpdated,
                        syncStatus = SyncStatus.SYNCED
                    )
                )
            } catch (e: Exception) {
                // Handle sync errors
                if (e is ConflictException) {
                    // Mark as conflict for user resolution
                    localDataSource.saveInventoryItem(entity.copy(syncStatus = SyncStatus.CONFLICT))
                }
                // Other errors - keep as pending
            }
        }
    }
    
    // Refresh data from server (when online)
    suspend fun refreshData() {
        if (!networkManager.isOnline()) return
        
        try {
            val remoteItems = remoteDataSource.getInventoryItems()
            
            // Only update items that are not pending local changes
            val localPendingItems = localDataSource.getPendingInventoryItems()
                .map { it.id }
                .toSet()
                
            remoteItems.forEach { remoteItem ->
                if (remoteItem.id !in localPendingItems) {
                    localDataSource.saveInventoryItem(
                        remoteItem.toEntity().copy(syncStatus = SyncStatus.SYNCED)
                    )
                }
            }
        } catch (e: Exception) {
            // Handle refresh errors
        }
    }
}
```

## Network Connectivity Monitoring

Implement a NetworkManager to monitor connectivity changes:

```kotlin
class NetworkManager @Inject constructor(
    private val context: Context
) {
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _isOnline = MutableStateFlow(checkInitialConnectionState())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    init {
        registerNetworkCallback()
    }
    
    fun isOnline(): Boolean {
        return _isOnline.value
    }
    
    private fun checkInitialConnectionState(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        )
    }
    
    private fun registerNetworkCallback() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
            }
            
            override fun onLost(network: Network) {
                _isOnline.value = false
            }
        }
        
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }
}
```

## Synchronization Manager

Implement a SyncManager to handle data synchronization:

```kotlin
class SyncManager @Inject constructor(
    private val context: Context,
    private val networkManager: NetworkManager
) {
    private val workManager = WorkManager.getInstance(context)
    
    // Schedule periodic sync
    fun schedulePeriodic() {
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
    
    // Sync a specific inventory item
    fun syncInventoryItem(inventoryId: String) {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncInventoryItemWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf("inventory_id" to inventoryId)
            )
            .build()
            
        workManager.enqueue(syncWorkRequest)
    }
    
    // Sync all pending data
    fun syncAll() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncAllWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        workManager.enqueue(syncWorkRequest)
    }
}
```

## WorkManager Workers

Implement workers for background synchronization:

```kotlin
class SyncAllWorker(
    context: Context,
    params: WorkerParameters,
    private val materialInventoryRepository: MaterialInventoryRepository,
    private val materialRepository: MaterialRepository,
    // Add other repositories as needed
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Sync all repositories
            materialInventoryRepository.syncPendingItems()
            materialRepository.syncPendingItems()
            // Sync other repositories
            
            // Refresh data from server
            materialInventoryRepository.refreshData()
            materialRepository.refreshData()
            // Refresh other repositories
            
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

class SyncInventoryItemWorker(
    context: Context,
    params: WorkerParameters,
    private val materialInventoryRepository: MaterialInventoryRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val inventoryId = inputData.getString("inventory_id") ?: return Result.failure()
        
        return try {
            materialInventoryRepository.syncInventoryItem(inventoryId)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
```

## UI Implementation

Update the UI to show sync status and handle offline mode:

```kotlin
class MaterialInventoryFragment : Fragment() {
    
    private val viewModel: MaterialInventoryViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe network status
        viewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            binding.offlineBanner.isVisible = !isOnline
            binding.syncButton.isEnabled = isOnline
        }
        
        // Observe pending changes
        viewModel.hasPendingChanges.observe(viewLifecycleOwner) { hasPendingChanges ->
            binding.pendingChangesBadge.isVisible = hasPendingChanges
        }
        
        // Sync button
        binding.syncButton.setOnClickListener {
            viewModel.syncAll()
        }
        
        // Inventory list with sync status indicators
        viewModel.inventory.observe(viewLifecycleOwner) { inventoryItems ->
            inventoryAdapter.submitList(inventoryItems)
        }
    }
}

class MaterialInventoryViewModel @Inject constructor(
    private val repository: MaterialInventoryRepository,
    private val syncManager: SyncManager,
    networkManager: NetworkManager
) : ViewModel() {
    
    val isOnline = networkManager.isOnline.asLiveData()
    
    val inventory = repository.getInventoryItems()
        .map { items ->
            items.map { item ->
                InventoryItemUiModel(
                    id = item.id,
                    name = item.material?.name ?: "",
                    quantity = item.quantity,
                    minimumQuantity = item.minimumQuantity,
                    location = item.location,
                    syncStatus = item.syncStatus
                )
            }
        }
        .asLiveData()
        
    val hasPendingChanges = repository.getPendingItemsCount()
        .map { count -> count > 0 }
        .asLiveData()
        
    fun syncAll() {
        syncManager.syncAll()
    }
    
    // Other methods
}
```

## Conflict Resolution

Implement a UI for resolving sync conflicts:

```kotlin
class ConflictResolutionFragment : Fragment() {
    
    private val viewModel: ConflictResolutionViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load conflicts
        viewModel.loadConflicts()
        
        // Observe conflicts
        viewModel.conflicts.observe(viewLifecycleOwner) { conflicts ->
            conflictAdapter.submitList(conflicts)
        }
        
        // Setup conflict adapter with resolution options
        conflictAdapter.setOnResolveLocalListener { conflict ->
            viewModel.resolveWithLocalVersion(conflict.id)
        }
        
        conflictAdapter.setOnResolveRemoteListener { conflict ->
            viewModel.resolveWithRemoteVersion(conflict.id)
        }
        
        conflictAdapter.setOnMergeListener { conflict ->
            viewModel.mergeVersions(conflict.id)
        }
    }
}

class ConflictResolutionViewModel @Inject constructor(
    private val repository: MaterialInventoryRepository
) : ViewModel() {
    
    private val _conflicts = MutableLiveData<List<ConflictUiModel>>()
    val conflicts: LiveData<List<ConflictUiModel>> = _conflicts
    
    fun loadConflicts() {
        viewModelScope.launch {
            val conflictItems = repository.getConflictItems()
            _conflicts.value = conflictItems.map { item ->
                ConflictUiModel(
                    id = item.id,
                    name = item.material?.name ?: "",
                    localVersion = item.localVersion,
                    remoteVersion = item.remoteVersion
                )
            }
        }
    }
    
    fun resolveWithLocalVersion(id: String) {
        viewModelScope.launch {
            repository.resolveConflictWithLocalVersion(id)
            loadConflicts()
        }
    }
    
    fun resolveWithRemoteVersion(id: String) {
        viewModelScope.launch {
            repository.resolveConflictWithRemoteVersion(id)
            loadConflicts()
        }
    }
    
    fun mergeVersions(id: String) {
        viewModelScope.launch {
            repository.mergeConflictVersions(id)
            loadConflicts()
        }
    }
}
```

## Testing Offline Mode

Create tests to verify offline functionality:

```kotlin
@Test
fun testOfflineSaveAndSync() = runTest {
    // Setup
    val networkManager = FakeNetworkManager()
    val repository = MaterialInventoryRepository(
        localDataSource = FakeLocalDataSource(),
        remoteDataSource = FakeRemoteDataSource(),
        networkManager = networkManager,
        syncManager = FakeSyncManager()
    )
    
    // Set offline
    networkManager.setOffline()
    
    // Save item while offline
    val inventory = createTestInventory()
    repository.saveInventoryItem(inventory)
    
    // Verify item is saved locally with PENDING status
    val savedItem = repository.getInventoryItemById(inventory.id)
    assertEquals(SyncStatus.PENDING, savedItem.syncStatus)
    
    // Set online
    networkManager.setOnline()
    
    // Sync
    repository.syncPendingItems()
    
    // Verify item is now SYNCED
    val syncedItem = repository.getInventoryItemById(inventory.id)
    assertEquals(SyncStatus.SYNCED, syncedItem.syncStatus)
}
```

## Conclusion

Implementing offline mode requires careful consideration of data synchronization, conflict resolution, and user experience. By following this guide, you can create a robust offline experience for the ElectricianApp that allows electricians to work effectively in the field, even without a constant internet connection.

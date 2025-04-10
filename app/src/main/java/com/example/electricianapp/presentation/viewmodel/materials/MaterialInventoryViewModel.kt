package com.example.electricianapp.presentation.viewmodel.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.TransactionType
import com.example.electricianapp.domain.usecase.materials.GetAllInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.GetLowStockInventoryItemsUseCase
import com.example.electricianapp.domain.usecase.materials.GetTransactionHistoryUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import com.example.electricianapp.domain.usecase.materials.SaveTransactionUseCase
import com.example.electricianapp.domain.usecase.materials.SearchInventoryItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for the material inventory screen
 */
@HiltViewModel
class MaterialInventoryViewModel @Inject constructor(
    private val getAllInventoryItemsUseCase: GetAllInventoryItemsUseCase,
    private val getLowStockInventoryItemsUseCase: GetLowStockInventoryItemsUseCase,
    private val getInventoryItemByIdUseCase: GetInventoryItemByIdUseCase,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val saveInventoryItemUseCase: SaveInventoryItemUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val searchInventoryItemsUseCase: SearchInventoryItemsUseCase
) : ViewModel() {

    private val _inventory = MutableLiveData<List<MaterialInventory>>()
    val inventory: LiveData<List<MaterialInventory>> = _inventory

    private val _lowStockItems = MutableLiveData<List<MaterialInventory>>()
    val lowStockItems: LiveData<List<MaterialInventory>> = _lowStockItems

    private val _selectedInventory = MutableLiveData<MaterialInventory?>()
    val selectedInventory: LiveData<MaterialInventory?> = _selectedInventory

    private val _transactionHistory = MutableLiveData<List<MaterialTransaction>>()
    val transactionHistory: LiveData<List<MaterialTransaction>> = _transactionHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _transactionSuccess = MutableLiveData<Boolean>()
    val transactionSuccess: LiveData<Boolean> = _transactionSuccess

    /**
     * Load all inventory items
     */
    fun loadInventory() {
        _isLoading.value = true
        _error.value = ""

        getAllInventoryItemsUseCase()
            .onEach { items ->
                _inventory.value = items
                _isLoading.value = false

                // Also check for low stock items
                checkLowStockItems()
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    /**
     * Load low stock inventory items
     */
    private fun checkLowStockItems() {
        getLowStockInventoryItemsUseCase()
            .onEach { items ->
                _lowStockItems.value = items
            }
            .catch { e ->
                // Just log the error, don't update UI
                println("Error checking low stock items: ${e.message}")
            }
            .launchIn(viewModelScope)
    }

    /**
     * Show only low stock items
     */
    fun showLowStockItems() {
        _isLoading.value = true
        _error.value = ""

        getLowStockInventoryItemsUseCase()
            .onEach { items ->
                _inventory.value = items
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    /**
     * Load inventory item by ID
     */
    fun loadInventoryItem(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""

            try {
                val item = getInventoryItemByIdUseCase(id)
                _selectedInventory.value = item
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }

    /**
     * Search inventory items
     */
    fun searchInventory(query: String) {
        if (query.isBlank()) {
            loadInventory()
            return
        }

        _isLoading.value = true
        _error.value = ""

        searchInventoryItemsUseCase(query)
            .onEach { items ->
                _inventory.value = items
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    /**
     * Load transaction history for an inventory item
     */
    fun loadTransactionHistory(inventoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""

            try {
                // Get inventory item to get the material ID
                val inventory = getInventoryItemByIdUseCase(inventoryId)
                    ?: throw IllegalArgumentException("Inventory item not found")

                // Get transaction history for the material
                getTransactionHistoryUseCase(inventory.materialId)
                    .onEach { transactions ->
                        _transactionHistory.value = transactions
                        _isLoading.value = false
                    }
                    .catch { e ->
                        _error.value = e.message ?: "Unknown error occurred"
                        _isLoading.value = false
                    }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }

    /**
     * Adjust inventory quantity
     */
    fun adjustInventory(inventoryId: String, quantity: Double, notes: String, transactionType: TransactionType) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            _transactionSuccess.value = false

            try {
                // Get current inventory
                val currentInventory = getInventoryItemByIdUseCase(inventoryId)
                    ?: throw IllegalArgumentException("Inventory item not found")

                // Create transaction
                val transaction = MaterialTransaction(
                    materialId = currentInventory.materialId,
                    quantity = quantity,
                    transactionType = transactionType,
                    date = Date(),
                    notes = notes
                )

                // Save transaction
                saveTransactionUseCase(transaction)

                // Calculate new quantity
                val newQuantity = when (transactionType) {
                    TransactionType.PURCHASE, TransactionType.RETURN_TO_INVENTORY, TransactionType.ADJUSTMENT ->
                        currentInventory.quantity + quantity
                    TransactionType.USE, TransactionType.RETURN_TO_SUPPLIER ->
                        currentInventory.quantity - quantity
                    TransactionType.TRANSFER ->
                        currentInventory.quantity // Transfer doesn't change quantity directly
                }

                // Update inventory
                val updatedInventory = currentInventory.copy(
                    quantity = newQuantity,
                    lastUpdated = Date()
                )

                // Save updated inventory
                saveInventoryItemUseCase(updatedInventory)

                // Update UI
                _selectedInventory.value = updatedInventory
                _transactionSuccess.value = true
                _isLoading.value = false

                // Refresh inventory list and transaction history
                loadInventory()
                loadTransactionHistory(inventoryId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
}

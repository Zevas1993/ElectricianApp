package com.example.electricianapp.presentation.viewmodel.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.domain.usecase.materials.GetAllMaterialsUseCase
import com.example.electricianapp.domain.usecase.materials.GetInventoryItemByIdUseCase
import com.example.electricianapp.domain.usecase.materials.SaveInventoryItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for adding or editing a material inventory item
 */
@HiltViewModel
class AddEditInventoryViewModel @Inject constructor(
    private val getAllMaterialsUseCase: GetAllMaterialsUseCase,
    private val getInventoryItemByIdUseCase: GetInventoryItemByIdUseCase,
    private val saveInventoryItemUseCase: SaveInventoryItemUseCase
) : ViewModel() {
    
    private val _materials = MutableLiveData<List<Material>>()
    val materials: LiveData<List<Material>> = _materials
    
    private val _selectedMaterial = MutableLiveData<Material?>()
    val selectedMaterial: LiveData<Material?> = _selectedMaterial
    
    private val _selectedInventory = MutableLiveData<MaterialInventory?>()
    val selectedInventory: LiveData<MaterialInventory?> = _selectedInventory
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess
    
    /**
     * Load all materials for dropdown
     */
    fun loadMaterials() {
        _isLoading.value = true
        _error.value = ""
        
        getAllMaterialsUseCase()
            .onEach { materialsList ->
                _materials.value = materialsList
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
                // Load inventory item
                val item = getInventoryItemByIdUseCase(id)
                    ?: throw IllegalArgumentException("Inventory item not found")
                
                _selectedInventory.value = item
                _selectedMaterial.value = item.material
                
                // Also load all materials for reference
                loadMaterials()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Select a material from the dropdown
     */
    fun selectMaterial(material: Material) {
        _selectedMaterial.value = material
    }
    
    /**
     * Save a new inventory item
     */
    fun saveInventory(inventory: MaterialInventory) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            _saveSuccess.value = false
            
            try {
                saveInventoryItemUseCase(inventory)
                _saveSuccess.value = true
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update an existing inventory item
     */
    fun updateInventory(inventory: MaterialInventory) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            _saveSuccess.value = false
            
            try {
                saveInventoryItemUseCase(inventory)
                _saveSuccess.value = true
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
}

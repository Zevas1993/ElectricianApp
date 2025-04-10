package com.example.electricianapp.presentation.viewmodel.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.domain.usecase.materials.GetMaterialByIdUseCase
import com.example.electricianapp.domain.usecase.materials.SaveMaterialUseCase
import com.example.electricianapp.domain.usecase.materials.UpdateMaterialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for adding or editing a material
 */
@HiltViewModel
class AddEditMaterialViewModel @Inject constructor(
    private val getMaterialByIdUseCase: GetMaterialByIdUseCase,
    private val saveMaterialUseCase: SaveMaterialUseCase,
    private val updateMaterialUseCase: UpdateMaterialUseCase
) : ViewModel() {
    
    private val _material = MutableLiveData<Material?>()
    val material: LiveData<Material?> = _material
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _saveComplete = MutableLiveData<Boolean>()
    val saveComplete: LiveData<Boolean> = _saveComplete
    
    /**
     * Load material for editing
     */
    fun loadMaterial(materialId: String) {
        _isLoading.value = true
        _error.value = ""
        
        viewModelScope.launch {
            try {
                val material = getMaterialByIdUseCase(materialId)
                _material.value = material
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Save or update a material
     */
    fun saveMaterial(
        name: String,
        description: String,
        category: MaterialCategory,
        partNumber: String,
        manufacturer: String,
        unitOfMeasure: UnitOfMeasure,
        unitPrice: Double,
        imageUrl: String?
    ) {
        _isLoading.value = true
        _error.value = ""
        
        viewModelScope.launch {
            try {
                val existingMaterial = _material.value
                
                if (existingMaterial != null) {
                    // Update existing material
                    val updatedMaterial = existingMaterial.copy(
                        name = name,
                        description = description,
                        category = category,
                        partNumber = partNumber,
                        manufacturer = manufacturer,
                        unitOfMeasure = unitOfMeasure,
                        unitPrice = unitPrice,
                        imageUrl = imageUrl
                    )
                    updateMaterialUseCase(updatedMaterial)
                } else {
                    // Create new material
                    val newMaterial = Material(
                        name = name,
                        description = description,
                        category = category,
                        partNumber = partNumber,
                        manufacturer = manufacturer,
                        unitOfMeasure = unitOfMeasure,
                        unitPrice = unitPrice,
                        imageUrl = imageUrl
                    )
                    saveMaterialUseCase(newMaterial)
                }
                
                _isLoading.value = false
                _saveComplete.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }
}

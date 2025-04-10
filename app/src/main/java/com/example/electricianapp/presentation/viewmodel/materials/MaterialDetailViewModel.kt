package com.example.electricianapp.presentation.viewmodel.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.usecase.materials.GetMaterialByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the material detail screen
 */
@HiltViewModel
class MaterialDetailViewModel @Inject constructor(
    private val getMaterialByIdUseCase: GetMaterialByIdUseCase
) : ViewModel() {
    
    private val _material = MutableLiveData<Material?>()
    val material: LiveData<Material?> = _material
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Load material details by ID
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
}

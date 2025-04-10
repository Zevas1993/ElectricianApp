package com.example.electricianapp.presentation.viewmodel.materials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.usecase.materials.GetAllMaterialsUseCase
import com.example.electricianapp.domain.usecase.materials.GetMaterialsByCategoryUseCase
import com.example.electricianapp.domain.usecase.materials.SearchMaterialsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for the material list screen
 */
@HiltViewModel
class MaterialListViewModel @Inject constructor(
    private val getAllMaterialsUseCase: GetAllMaterialsUseCase,
    private val getMaterialsByCategoryUseCase: GetMaterialsByCategoryUseCase,
    private val searchMaterialsUseCase: SearchMaterialsUseCase
) : ViewModel() {
    
    private val _materials = MutableLiveData<List<Material>>()
    val materials: LiveData<List<Material>> = _materials
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private var currentCategory: MaterialCategory? = null
    private var currentSearchQuery: String? = null
    
    /**
     * Load all materials
     */
    fun loadMaterials() {
        _isLoading.value = true
        _error.value = ""
        currentCategory = null
        currentSearchQuery = null
        
        getAllMaterialsUseCase()
            .onEach { materials ->
                _materials.value = materials
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Load materials by category
     */
    fun loadMaterialsByCategory(category: MaterialCategory) {
        _isLoading.value = true
        _error.value = ""
        currentCategory = category
        currentSearchQuery = null
        
        getMaterialsByCategoryUseCase(category)
            .onEach { materials ->
                _materials.value = materials
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Search materials by query
     */
    fun searchMaterials(query: String) {
        if (query.isBlank()) {
            loadMaterials()
            return
        }
        
        _isLoading.value = true
        _error.value = ""
        currentSearchQuery = query
        
        searchMaterialsUseCase(query)
            .onEach { materials ->
                _materials.value = materials
                _isLoading.value = false
            }
            .catch { e ->
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Refresh the current data
     */
    fun refresh() {
        when {
            currentSearchQuery != null -> searchMaterials(currentSearchQuery!!)
            currentCategory != null -> loadMaterialsByCategory(currentCategory!!)
            else -> loadMaterials()
        }
    }
}

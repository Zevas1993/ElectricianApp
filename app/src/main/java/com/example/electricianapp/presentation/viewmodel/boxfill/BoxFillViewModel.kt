package com.example.electricianapp.presentation.viewmodel.boxfill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.boxfill.* // Import all box fill models
import com.example.electricianapp.domain.repository.boxfill.BoxFillRepository
import com.example.electricianapp.domain.usecase.boxfill.CalculateBoxFillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define UI State sealed class
sealed class BoxFillUiState {
    object Initial : BoxFillUiState()
    object Loading : BoxFillUiState()
    data class Success(val result: BoxFillResult) : BoxFillUiState()
    data class Error(val message: String) : BoxFillUiState()
}

@HiltViewModel
class BoxFillViewModel @Inject constructor(
    private val calculateBoxFillUseCase: CalculateBoxFillUseCase,
    private val boxFillRepository: BoxFillRepository // Assuming this will be created/provided
) : ViewModel() {

    private val _uiState = MutableStateFlow<BoxFillUiState>(BoxFillUiState.Initial)
    val uiState: StateFlow<BoxFillUiState> = _uiState

    // Input parameters
    private val _boxType = MutableLiveData<BoxType>(BoxType.DEVICE_BOX) // Default value
    val boxType: LiveData<BoxType> = _boxType

    // Store dimensions/volume string for now, might need parsing later
    private val _boxDimensionsOrVolume = MutableLiveData<String>("")
    val boxDimensionsOrVolume: LiveData<String> = _boxDimensionsOrVolume

    private val _components = MutableLiveData<List<BoxComponent>>(emptyList())
    val components: LiveData<List<BoxComponent>> = _components

    // TODO: Add data source for box volumes based on type/dimensions
    // TODO: Add data source for conductor volumes based on AWG size (NEC Table 314.16(B))

    fun setBoxType(type: BoxType) {
        _boxType.value = type
    }

    fun setBoxDimensionsOrVolume(value: String) {
        _boxDimensionsOrVolume.value = value
        // TODO: Add validation/parsing if needed
    }

    fun addComponent(component: BoxComponent) {
        val currentList = _components.value ?: emptyList()
        _components.value = currentList + component
    }

    fun updateComponent(index: Int, component: BoxComponent) {
        val currentList = _components.value ?: return
        if (index in currentList.indices) {
            _components.value = currentList.toMutableList().apply { set(index, component) }
        }
    }

    fun removeComponent(index: Int) {
        val currentList = _components.value ?: return
        if (index in currentList.indices) {
            _components.value = currentList.toMutableList().apply { removeAt(index) }
        }
    }

    fun calculateBoxFill() {
        viewModelScope.launch {
            _uiState.value = BoxFillUiState.Loading
            try {
                // TODO: Parse boxDimensionsOrVolume to get actual volume
                // This requires a lookup based on type and dimensions string, or direct volume input
                val boxVolume = parseBoxVolume(_boxType.value, _boxDimensionsOrVolume.value)
                if (boxVolume == null) {
                    _uiState.value = BoxFillUiState.Error("Invalid box dimensions or volume.")
                    return@launch
                }

                val input = BoxFillInput(
                    boxType = _boxType.value ?: BoxType.DEVICE_BOX,
                    boxDimensions = _boxDimensionsOrVolume.value ?: "",
                    boxVolumeInCubicInches = boxVolume,
                    components = _components.value ?: emptyList()
                )

                if (input.components.isEmpty()) {
                    _uiState.value = BoxFillUiState.Error("Add at least one component.")
                    return@launch
                }

                val result = calculateBoxFillUseCase(input)
                _uiState.value = BoxFillUiState.Success(result)

                // TODO: Implement saving calculation via repository if needed
                // boxFillRepository.saveCalculation(input, result)

            } catch (e: Exception) {
                _uiState.value = BoxFillUiState.Error(e.message ?: "Calculation failed")
            }
        }
    }

    // Placeholder - Needs actual implementation based on NEC tables or user input handling
    private fun parseBoxVolume(type: BoxType?, dimensionsOrVolume: String?): Double? {
        // Try parsing as direct volume first
        val directVolume = dimensionsOrVolume?.toDoubleOrNull()
        if (directVolume != null && directVolume > 0) {
            return directVolume
        }
        // TODO: Implement lookup based on type and dimensions string (e.g., "4x4x1.5")
        // This requires a data source mapping dimensions to standard volumes from NEC Table 314.16(A)
        println("Warning: Box volume lookup not implemented. Using placeholder volume 20.0")
        return 20.0 // Placeholder
    }

    // TODO: Expose necessary data for dropdowns (Box Types, Component Types, Wire Sizes)
    val availableBoxTypes: List<BoxType> = BoxType.values().toList()
    val availableComponentTypes: List<ComponentType> = ComponentType.values().toList()
    // TODO: Get wire sizes relevant for box fill (e.g., 14 to 6 AWG) from a central source
    val availableWireSizes: List<String> = listOf("14", "12", "10", "8", "6") // Placeholder
}

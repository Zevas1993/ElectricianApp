package com.example.electricalcalculator.presentation.viewmodel.conduitfill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricalcalculator.domain.model.conduitfill.ConduitFillInput
import com.example.electricalcalculator.domain.model.conduitfill.ConduitFillResult
import com.example.electricalcalculator.domain.model.conduitfill.ConduitType
import com.example.electricalcalculator.domain.model.conduitfill.Wire
import com.example.electricalcalculator.domain.model.conduitfill.WireType
import com.example.electricalcalculator.domain.repository.conduitfill.ConduitFillRepository
import com.example.electricalcalculator.domain.usecase.conduitfill.CalculateConduitFillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the conduit fill calculator screen
 */
@HiltViewModel
class ConduitFillViewModel @Inject constructor(
    private val calculateConduitFillUseCase: CalculateConduitFillUseCase,
    private val conduitFillRepository: ConduitFillRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<ConduitFillUiState>(ConduitFillUiState.Initial)
    val uiState: StateFlow<ConduitFillUiState> = _uiState

    // Input parameters
    private val _conduitType = MutableLiveData<ConduitType>(ConduitType.EMT)
    val conduitType: LiveData<ConduitType> = _conduitType

    private val _conduitSize = MutableLiveData<String>("1/2")
    val conduitSize: LiveData<String> = _conduitSize

    private val _wires = MutableLiveData<List<Wire>>(emptyList())
    val wires: LiveData<List<Wire>> = _wires

    // Calculation result
    private val _calculationResult = MutableLiveData<ConduitFillResult>()
    val calculationResult: LiveData<ConduitFillResult> = _calculationResult

    // Calculation history
    private val _calculationHistory = MutableLiveData<List<Pair<ConduitFillInput, ConduitFillResult>>>()
    val calculationHistory: LiveData<List<Pair<ConduitFillInput, ConduitFillResult>>> = _calculationHistory

    // Wire area lookup table (THHN wire sizes in AWG to area in square inches)
    private val wireAreaMap = mapOf(
        "14 AWG" to 0.0097,
        "12 AWG" to 0.0133,
        "10 AWG" to 0.0211,
        "8 AWG" to 0.0366,
        "6 AWG" to 0.0507,
        "4 AWG" to 0.0824,
        "3 AWG" to 0.0973,
        "2 AWG" to 0.1158,
        "1 AWG" to 0.1562,
        "1/0 AWG" to 0.1855,
        "2/0 AWG" to 0.2223,
        "3/0 AWG" to 0.2679,
        "4/0 AWG" to 0.3237,
        "250 kcmil" to 0.3970,
        "300 kcmil" to 0.4608,
        "350 kcmil" to 0.5242,
        "400 kcmil" to 0.5863,
        "500 kcmil" to 0.7073,
        "600 kcmil" to 0.8676,
        "700 kcmil" to 0.9887,
        "750 kcmil" to 1.0496,
        "800 kcmil" to 1.1085,
        "900 kcmil" to 1.2311,
        "1000 kcmil" to 1.3478
    )

    init {
        loadCalculationHistory()
    }

    /**
     * Set the conduit type
     */
    fun setConduitType(type: ConduitType) {
        _conduitType.value = type
    }

    /**
     * Set the conduit size
     */
    fun setConduitSize(size: String) {
        _conduitSize.value = size
    }

    /**
     * Add a wire to the list
     */
    fun addWire(wireType: WireType, wireSize: String, quantity: Int) {
        val areaKey = "$wireSize AWG"
        val area = wireAreaMap[areaKey] ?: 
                   wireAreaMap[wireSize] ?: // Try without AWG suffix
                   throw IllegalArgumentException("Unsupported wire size: $wireSize")
        
        val wire = Wire(
            type = wireType,
            size = wireSize,
            quantity = quantity,
            areaInSqInches = area
        )
        
        val currentList = _wires.value ?: emptyList()
        _wires.value = currentList + wire
    }

    /**
     * Remove a wire from the list
     */
    fun removeWire(index: Int) {
        val currentList = _wires.value ?: return
        if (index in currentList.indices) {
            _wires.value = currentList.toMutableList().apply { removeAt(index) }
        }
    }

    /**
     * Update an existing wire
     */
    fun updateWire(index: Int, wireType: WireType, wireSize: String, quantity: Int) {
        val currentList = _wires.value ?: return
        if (index in currentList.indices) {
            val areaKey = "$wireSize AWG"
            val area = wireAreaMap[areaKey] ?: 
                       wireAreaMap[wireSize] ?: // Try without AWG suffix
                       throw IllegalArgumentException("Unsupported wire size: $wireSize")
            
            val updatedWire = Wire(
                type = wireType,
                size = wireSize,
                quantity = quantity,
                areaInSqInches = area
            )
            
            _wires.value = currentList.toMutableList().apply { set(index, updatedWire) }
        }
    }

    /**
     * Perform the conduit fill calculation
     */
    fun calculateConduitFill() {
        viewModelScope.launch {
            try {
                _uiState.value = ConduitFillUiState.Loading

                val input = ConduitFillInput(
                    conduitType = _conduitType.value ?: ConduitType.EMT,
                    conduitSize = _conduitSize.value ?: "1/2",
                    wires = _wires.value ?: emptyList()
                )

                val result = calculateConduitFillUseCase(input)
                _calculationResult.value = result
                _uiState.value = ConduitFillUiState.Success(result)

                // Save calculation to history
                conduitFillRepository.saveCalculation(input, result)
                loadCalculationHistory()
            } catch (e: Exception) {
                _uiState.value = ConduitFillUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Load calculation history from repository
     */
    private fun loadCalculationHistory() {
        viewModelScope.launch {
            conduitFillRepository.getCalculationHistory().collect { history ->
                _calculationHistory.value = history
            }
        }
    }

    /**
     * Clear calculation history
     */
    fun clearHistory() {
        viewModelScope.launch {
            conduitFillRepository.clearHistory()
            loadCalculationHistory()
        }
    }

    /**
     * Delete a specific calculation from history
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            conduitFillRepository.deleteCalculation(id)
            loadCalculationHistory()
        }
    }

    /**
     * Load a saved calculation
     */
    fun loadCalculation(id: Long) {
        viewModelScope.launch {
            val calculation = conduitFillRepository.getCalculationById(id)
            calculation?.let { (input, result) ->
                _conduitType.value = input.conduitType
                _conduitSize.value = input.conduitSize
                _wires.value = input.wires
                _calculationResult.value = result
                _uiState.value = ConduitFillUiState.Success(result)
            }
        }
    }
}

/**
 * UI state for the conduit fill calculator screen
 */
sealed class ConduitFillUiState {
    object Initial : ConduitFillUiState()
    object Loading : ConduitFillUiState()
    data class Success(val result: ConduitFillResult) : ConduitFillUiState()
    data class Error(val message: String) : ConduitFillUiState()
}

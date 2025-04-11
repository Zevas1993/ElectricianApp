package com.example.electricianapp.presentation.viewmodel.conduitfill // Corrected package

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.conduitfill.ConduitFillInput // Corrected import
import com.example.electricianapp.domain.model.conduitfill.ConduitFillResult // Corrected import
import com.example.electricianapp.domain.model.conduitfill.ConduitType // Corrected import
import com.example.electricianapp.domain.model.conduitfill.Wire // Corrected import
import com.example.electricianapp.domain.model.conduitfill.WireType // Corrected import
import com.example.electricianapp.domain.repository.conduitfill.ConduitFillRepository // Corrected import
import com.example.electricianapp.domain.usecase.conduitfill.CalculateConduitFillUseCase // Corrected import
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

    // TODO: Move this data to a more appropriate layer (e.g., data source or constants file)
    // Wire area lookup table (Wire size string to area in square inches)
    // Using common NEC sizes for THHN/THWN-2. Ensure these match expected input formats.
    private val wireAreaMap = mapOf(
        "14" to 0.0097, // Assumes AWG if just number
        "12" to 0.0133,
        "10" to 0.0211,
        "8" to 0.0366,
        "6" to 0.0507,
        "4" to 0.0824,
        "3" to 0.0973,
        "2" to 0.1158,
        "1" to 0.1562,
        "1/0" to 0.1855,
        "2/0" to 0.2223,
        "3/0" to 0.2679,
        "4/0" to 0.3237,
        "250" to 0.3970, // Assumes kcmil if >= 250
        "300" to 0.4608,
        "350" to 0.5242,
        "400" to 0.5863,
        "500" to 0.7073,
        "600" to 0.8676,
        "700" to 0.9887,
        "750" to 1.0496,
        "800" to 1.1085,
        "900" to 1.2311,
        "1000" to 1.3478,
        // Explicit AWG/kcmil keys for robustness if needed, but keep primary keys simple
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

    // Expose the valid wire sizes for the UI dropdown
    val availableWireSizes: List<String> = wireAreaMap.keys.filterNot { it.contains(" ") }.sortedWith(
        // Custom sort: AWG descending, then kcmil ascending
        compareBy { size ->
            when {
                size.contains("/") -> -size.split("/")[0].toInt() // Handle 1/0, 2/0 etc.
                size.toIntOrNull() != null && size.toInt() < 250 -> -size.toInt() // AWG sizes (negative for descending)
                else -> size.toIntOrNull() ?: Int.MAX_VALUE // kcmil sizes or non-numeric last
            }
        }
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
        // Use the provided wireSize directly as the key
        val area = wireAreaMap[wireSize] ?: run {
            _uiState.value = ConduitFillUiState.Error("Unsupported wire size: $wireSize")
            return // Stop execution if area not found
                   }

        // Construct a name, e.g., "12 AWG THHN"
        val wireName = "$wireSize ${wireType.name}"
        val wire = Wire(
            name = wireName, // Add name parameter
            type = wireType,
            size = wireSize,
            quantity = quantity,
            areaInSqInches = area // Use the looked-up area
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
            // Use the provided wireSize directly as the key
            val area = wireAreaMap[wireSize] ?: run {
                _uiState.value = ConduitFillUiState.Error("Unsupported wire size: $wireSize")
                return
                       }

            // Construct a name, e.g., "12 AWG THHN"
            val wireName = "$wireSize ${wireType.name}"
            val updatedWire = Wire(
                name = wireName, // Add name parameter
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

                // Validate wires list
                if (input.wires.isEmpty()) {
                     _uiState.value = ConduitFillUiState.Error("Please add at least one wire.")
                     return@launch
                }

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
            try {
                conduitFillRepository.getCalculationHistory().collect { history ->
                    _calculationHistory.value = history
                }
            } catch (e: Exception) {
                 // Handle error loading history, maybe post to UI state
                 _uiState.value = ConduitFillUiState.Error("Failed to load history: ${e.message}")
            }
        }
    }


    /**
     * Clear calculation history
     */
    fun clearHistory() {
        viewModelScope.launch {
             try {
                conduitFillRepository.clearHistory()
                loadCalculationHistory() // Refresh the list
             } catch (e: Exception) {
                 _uiState.value = ConduitFillUiState.Error("Failed to clear history: ${e.message}")
             }
        }
    }

    /**
     * Delete a specific calculation from history
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
             try {
                conduitFillRepository.deleteCalculation(id)
                loadCalculationHistory() // Refresh the list
             } catch (e: Exception) {
                 _uiState.value = ConduitFillUiState.Error("Failed to delete calculation: ${e.message}")
             }
        }
    }

    /**
     * Load a saved calculation
     */
    fun loadCalculation(id: Long) {
        viewModelScope.launch {
             try {
                val calculation = conduitFillRepository.getCalculationById(id)
                calculation?.let { (input, result) ->
                    _conduitType.value = input.conduitType
                    _conduitSize.value = input.conduitSize
                    _wires.value = input.wires
                    _calculationResult.value = result
                    _uiState.value = ConduitFillUiState.Success(result) // Update UI state if needed
                } ?: run {
                     _uiState.value = ConduitFillUiState.Error("Calculation not found.")
                }
             } catch (e: Exception) {
                 _uiState.value = ConduitFillUiState.Error("Failed to load calculation: ${e.message}")
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

package com.example.electricianapp.presentation.viewmodel.dwellingload // Corrected package

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Remove duplicate Appliance import
import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity // Import Entity
import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingType // Corrected import
import com.example.electricianapp.domain.repository.dwellingload.DwellingLoadRepository // Corrected import
import com.example.electricianapp.domain.usecase.dwellingload.CalculateDwellingLoadUseCase // Corrected import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the dwelling load calculator screen
 */
@HiltViewModel
class DwellingLoadViewModel @Inject constructor(
    private val calculateDwellingLoadUseCase: CalculateDwellingLoadUseCase,
    private val dwellingLoadRepository: DwellingLoadRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<DwellingLoadUiState>(DwellingLoadUiState.Initial)
    val uiState: StateFlow<DwellingLoadUiState> = _uiState

    // Input parameters
    private val _dwellingType = MutableLiveData<DwellingType>(DwellingType.RESIDENTIAL)
    val dwellingType: LiveData<DwellingType> = _dwellingType

    private val _squareFootage = MutableLiveData<Double>(0.0)
    val squareFootage: LiveData<Double> = _squareFootage

    private val _smallApplianceCircuits = MutableLiveData<Int>(2)
    val smallApplianceCircuits: LiveData<Int> = _smallApplianceCircuits

    private val _laundryCircuits = MutableLiveData<Int>(1)
    val laundryCircuits: LiveData<Int> = _laundryCircuits

    private val _appliances = MutableLiveData<List<Appliance>>(emptyList())
    val appliances: LiveData<List<Appliance>> = _appliances

    // Calculation result
    private val _calculationResult = MutableLiveData<DwellingLoadResult>()
    val calculationResult: LiveData<DwellingLoadResult> = _calculationResult

    // Calculation history - Now holds entities directly
    private val _calculationHistory = MutableLiveData<List<DwellingLoadCalculationEntity>>()
    val calculationHistory: LiveData<List<DwellingLoadCalculationEntity>> = _calculationHistory

    init {
        // loadCalculationHistory() // Load history on init if desired
    }

    /**
     * Set the dwelling type
     */
    fun setDwellingType(type: DwellingType) {
        _dwellingType.value = type
    }

    /**
     * Set the square footage
     */
    fun setSquareFootage(footage: Double) {
        _squareFootage.value = footage
    }

    /**
     * Set the number of small appliance circuits
     */
    fun setSmallApplianceCircuits(count: Int) {
        _smallApplianceCircuits.value = count
    }

    /**
     * Set the number of laundry circuits
     */
    fun setLaundryCircuits(count: Int) {
        _laundryCircuits.value = count
    }

    /**
     * Add an appliance to the list
     */
    fun addAppliance(appliance: Appliance) {
        val currentList = _appliances.value ?: emptyList()
        _appliances.value = currentList + appliance
    }

    /**
     * Remove an appliance from the list
     */
    fun removeAppliance(index: Int) {
        val currentList = _appliances.value ?: return
        if (index in currentList.indices) {
            _appliances.value = currentList.toMutableList().apply { removeAt(index) }
        }
    }

    /**
     * Update an existing appliance
     */
    fun updateAppliance(index: Int, appliance: Appliance) {
        val currentList = _appliances.value ?: return
        if (index in currentList.indices) {
            _appliances.value = currentList.toMutableList().apply { set(index, appliance) }
        }
    }

    /**
     * Perform the dwelling load calculation
     */
    fun calculateDwellingLoad() {
        viewModelScope.launch {
            try {
                _uiState.value = DwellingLoadUiState.Loading

                val input = DwellingLoadInput(
                    dwellingType = _dwellingType.value ?: DwellingType.RESIDENTIAL,
                    squareFootage = _squareFootage.value ?: 0.0,
                    smallApplianceCircuits = _smallApplianceCircuits.value ?: 2,
                    laundryCircuits = _laundryCircuits.value ?: 1,
                    appliances = _appliances.value ?: emptyList()
                )

                val result = calculateDwellingLoadUseCase(input)
                _calculationResult.value = result // Keep updating the current result LiveData
                _uiState.value = DwellingLoadUiState.Success(result)

                // Don't save automatically here anymore
                // dwellingLoadRepository.saveCalculation(input, result)
                // loadCalculationHistory()
            } catch (e: Exception) {
                _uiState.value = DwellingLoadUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Load saved calculation history from repository
     */
    fun loadCalculationHistory() { // Make public if needed to refresh manually
        viewModelScope.launch {
            // dwellingLoadRepository.getCalculationHistory().collect { history -> // Old method call
            dwellingLoadRepository.getSavedCalculations().collect { history -> // Use updated repo method name
                _calculationHistory.value = history // Update LiveData with entities
            }
        }
    }

    /**
     * Clear calculation history
     */
    fun clearHistory() {
        viewModelScope.launch {
            dwellingLoadRepository.clearHistory()
            loadCalculationHistory()
        }
    }

    /**
     * Delete a specific calculation from history
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            // Fetch the entity first, then delete if found
            dwellingLoadRepository.getCalculationById(id)?.let { entityToDelete ->
                dwellingLoadRepository.deleteCalculation(entityToDelete) // Pass the entity to delete
                loadCalculationHistory() // Refresh history
            }
        }
    }

    /**
     * Load a saved calculation into the current input fields and result state
     */
    fun loadCalculation(id: Long) {
        viewModelScope.launch {
            val entity = dwellingLoadRepository.getCalculationById(id) // Get entity
            entity?.let {
                // Map entity fields back to LiveData for UI update
                _dwellingType.value = it.dwellingType
                _squareFootage.value = it.squareFootage
                _smallApplianceCircuits.value = it.smallApplianceCircuits
                _laundryCircuits.value = it.laundryCircuits
                _appliances.value = it.appliances // This is already List<Appliance>

                // Reconstruct the DwellingLoadResult from the entity
                val result = DwellingLoadResult(
                    totalConnectedLoad = it.totalConnectedLoad,
                    totalDemandLoad = it.totalDemandLoad,
                    serviceSize = it.serviceSize,
                    generalLightingLoad = it.generalLightingLoad,
                    smallApplianceLoad = it.smallApplianceLoad,
                    laundryLoad = it.laundryLoad,
                    applianceLoads = it.applianceLoads,
                    demandFactors = it.demandFactors
                )
                _calculationResult.value = result // Update the result LiveData
                _uiState.value = DwellingLoadUiState.Success(result) // Update UI state
            }
        }
    }

    /**
     * Save the current calculation state with a given name.
     */
    fun saveCurrentCalculation(name: String) {
        viewModelScope.launch {
            val currentInput = DwellingLoadInput(
                dwellingType = _dwellingType.value ?: DwellingType.RESIDENTIAL,
                squareFootage = _squareFootage.value ?: 0.0,
                smallApplianceCircuits = _smallApplianceCircuits.value ?: 2,
                laundryCircuits = _laundryCircuits.value ?: 1,
                appliances = _appliances.value ?: emptyList()
            )
            val currentResult = _calculationResult.value

            if (currentResult != null) {
                try {
                    dwellingLoadRepository.saveCalculation(currentInput, currentResult, name)
                    // Optionally refresh history or show success message
                    loadCalculationHistory() // Refresh history list
                } catch (e: Exception) {
                    // Handle save error (e.g., show a Toast)
                     _uiState.value = DwellingLoadUiState.Error("Failed to save calculation: ${e.message}")
                }
            } else {
                // Handle case where there is no result to save
                 _uiState.value = DwellingLoadUiState.Error("No calculation result available to save.")
            }
        }
    }
}

/**
 * UI state for the dwelling load calculator screen
 */
sealed class DwellingLoadUiState {
    object Initial : DwellingLoadUiState()
    object Loading : DwellingLoadUiState()
    data class Success(val result: DwellingLoadResult) : DwellingLoadUiState()
    data class Error(val message: String) : DwellingLoadUiState()
}

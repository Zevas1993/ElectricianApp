package com.example.electricalcalculator.presentation.viewmodel.dwellingload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricalcalculator.domain.model.dwellingload.Appliance
import com.example.electricalcalculator.domain.model.dwellingload.DwellingLoadInput
import com.example.electricalcalculator.domain.model.dwellingload.DwellingLoadResult
import com.example.electricalcalculator.domain.model.dwellingload.DwellingType
import com.example.electricalcalculator.domain.repository.dwellingload.DwellingLoadRepository
import com.example.electricalcalculator.domain.usecase.dwellingload.CalculateDwellingLoadUseCase
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

    // Calculation history
    private val _calculationHistory = MutableLiveData<List<Pair<DwellingLoadInput, DwellingLoadResult>>>()
    val calculationHistory: LiveData<List<Pair<DwellingLoadInput, DwellingLoadResult>>> = _calculationHistory

    init {
        loadCalculationHistory()
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
                _calculationResult.value = result
                _uiState.value = DwellingLoadUiState.Success(result)

                // Save calculation to history
                dwellingLoadRepository.saveCalculation(input, result)
                loadCalculationHistory()
            } catch (e: Exception) {
                _uiState.value = DwellingLoadUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Load calculation history from repository
     */
    private fun loadCalculationHistory() {
        viewModelScope.launch {
            dwellingLoadRepository.getCalculationHistory().collect { history ->
                _calculationHistory.value = history
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
            dwellingLoadRepository.deleteCalculation(id)
            loadCalculationHistory()
        }
    }

    /**
     * Load a saved calculation
     */
    fun loadCalculation(id: Long) {
        viewModelScope.launch {
            val calculation = dwellingLoadRepository.getCalculationById(id)
            calculation?.let { (input, result) ->
                _dwellingType.value = input.dwellingType
                _squareFootage.value = input.squareFootage
                _smallApplianceCircuits.value = input.smallApplianceCircuits
                _laundryCircuits.value = input.laundryCircuits
                _appliances.value = input.appliances
                _calculationResult.value = result
                _uiState.value = DwellingLoadUiState.Success(result)
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

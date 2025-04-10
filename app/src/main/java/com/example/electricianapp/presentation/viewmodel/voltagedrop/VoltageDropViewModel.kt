package com.example.electricianapp.presentation.viewmodel.voltagedrop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.voltagedrop.*
import com.example.electricianapp.domain.repository.voltagedrop.VoltageDropRepository
import com.example.electricianapp.domain.usecase.voltagedrop.CalculateVoltageDropUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the UI state for voltage drop calculations
 */
sealed class VoltageDropUiState {
    object Initial : VoltageDropUiState()
    object Loading : VoltageDropUiState()
    data class Success(val result: VoltageDropResult) : VoltageDropUiState()
    data class Error(val message: String) : VoltageDropUiState()
}

/**
 * ViewModel for the voltage drop calculator screen
 */
@HiltViewModel
class VoltageDropViewModel @Inject constructor(
    private val calculateVoltageDropUseCase: CalculateVoltageDropUseCase,
    private val voltageDropRepository: VoltageDropRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<VoltageDropUiState>(VoltageDropUiState.Initial)
    val uiState: StateFlow<VoltageDropUiState> = _uiState

    // Input parameters
    private val _systemType = MutableStateFlow(SystemType.SINGLE_PHASE)
    val systemType: StateFlow<SystemType> = _systemType

    private val _conductorType = MutableStateFlow(ConductorType.COPPER)
    val conductorType: StateFlow<ConductorType> = _conductorType

    private val _conduitMaterial = MutableStateFlow(ConduitMaterial.PVC)
    val conduitMaterial: StateFlow<ConduitMaterial> = _conduitMaterial

    private val _temperatureRating = MutableStateFlow(TemperatureRating.RATING_75C)
    val temperatureRating: StateFlow<TemperatureRating> = _temperatureRating

    private val _wireSize = MutableStateFlow("12")
    val wireSize: StateFlow<String> = _wireSize

    private val _lengthInFeet = MutableStateFlow("")
    val lengthInFeet: StateFlow<String> = _lengthInFeet

    private val _loadInAmps = MutableStateFlow("")
    val loadInAmps: StateFlow<String> = _loadInAmps

    private val _voltageInVolts = MutableStateFlow("")
    val voltageInVolts: StateFlow<String> = _voltageInVolts

    private val _powerFactor = MutableStateFlow("1.0")
    val powerFactor: StateFlow<String> = _powerFactor

    // Update functions for input parameters
    fun updateSystemType(type: SystemType) {
        _systemType.value = type
    }

    fun updateConductorType(type: ConductorType) {
        _conductorType.value = type
    }

    fun updateConduitMaterial(material: ConduitMaterial) {
        _conduitMaterial.value = material
    }

    fun updateTemperatureRating(rating: TemperatureRating) {
        _temperatureRating.value = rating
    }

    fun updateWireSize(size: String) {
        _wireSize.value = size
    }

    fun updateLengthInFeet(length: String) {
        _lengthInFeet.value = length
    }

    fun updateLoadInAmps(load: String) {
        _loadInAmps.value = load
    }

    fun updateVoltageInVolts(voltage: String) {
        _voltageInVolts.value = voltage
    }

    fun updatePowerFactor(factor: String) {
        _powerFactor.value = factor
    }

    /**
     * Calculate voltage drop based on current input parameters
     */
    fun calculateVoltageDrop() {
        viewModelScope.launch {
            _uiState.value = VoltageDropUiState.Loading

            try {
                // Parse numeric inputs
                val length = _lengthInFeet.value.toDoubleOrNull()
                val load = _loadInAmps.value.toDoubleOrNull()
                val voltage = _voltageInVolts.value.toDoubleOrNull()
                val powerFactor = _powerFactor.value.toDoubleOrNull() ?: 1.0

                // Validate inputs
                if (length == null || length <= 0) {
                    _uiState.value = VoltageDropUiState.Error("Please enter a valid length.")
                    return@launch
                }

                if (load == null || load <= 0) {
                    _uiState.value = VoltageDropUiState.Error("Please enter a valid load current.")
                    return@launch
                }

                if (voltage == null || voltage <= 0) {
                    _uiState.value = VoltageDropUiState.Error("Please enter a valid voltage.")
                    return@launch
                }

                if (powerFactor <= 0 || powerFactor > 1.0) {
                    _uiState.value = VoltageDropUiState.Error("Power factor must be between 0 and 1.")
                    return@launch
                }

                // Create input object
                val input = VoltageDropInput(
                    systemType = _systemType.value,
                    conductorType = _conductorType.value,
                    conduitMaterial = _conduitMaterial.value,
                    temperatureRating = _temperatureRating.value,
                    wireSize = _wireSize.value,
                    lengthInFeet = length,
                    loadInAmps = load,
                    voltageInVolts = voltage,
                    powerFactor = powerFactor
                )

                // Calculate result
                val result = calculateVoltageDropUseCase(input)

                // Update UI state
                _uiState.value = VoltageDropUiState.Success(result)

                // Save calculation to history
                voltageDropRepository.saveCalculation(input, result)
            } catch (e: Exception) {
                _uiState.value = VoltageDropUiState.Error("Calculation error: ${e.message}")
            }
        }
    }

    /**
     * Reset all input fields to default values
     */
    fun resetInputs() {
        _systemType.value = SystemType.SINGLE_PHASE
        _conductorType.value = ConductorType.COPPER
        _conduitMaterial.value = ConduitMaterial.PVC
        _temperatureRating.value = TemperatureRating.RATING_75C
        _wireSize.value = "12"
        _lengthInFeet.value = ""
        _loadInAmps.value = ""
        _voltageInVolts.value = ""
        _powerFactor.value = "1.0"
        _uiState.value = VoltageDropUiState.Initial
    }
}

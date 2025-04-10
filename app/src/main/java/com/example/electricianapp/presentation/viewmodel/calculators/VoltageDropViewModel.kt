package com.example.electricianapp.presentation.viewmodel.calculators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.voltagedrop.*
import com.example.electricianapp.domain.usecase.voltagedrop.CalculateVoltageDropUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoltageDropUiState(
    val voltage: String = "",
    val phase: String = "Single Phase",
    val material: String = "Copper",
    val wireSize: String = "",
    val loadCurrent: String = "",
    val distance: String = "",
    val voltageDrop: Double? = null,
    val voltageDropPercent: Double? = null,
    val endVoltage: Double? = null,
    val errorMessage: String? = null,
    val isWithinRecommendedLimits: Boolean? = null
)

@HiltViewModel
class VoltageDropViewModel @Inject constructor(
    private val calculateVoltageDropUseCase: CalculateVoltageDropUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoltageDropUiState())
    val uiState: StateFlow<VoltageDropUiState> = _uiState.asStateFlow()

    // NEC Chapter 9, Table 8 - Conductor Properties (Approximate Circular Mils)
    // Using common sizes for this example. A more robust implementation would use a complete table.
    private val circularMilsMap = mapOf(
        "14" to 4110.0,
        "12" to 6530.0,
        "10" to 10380.0,
        "8" to 16510.0,
        "6" to 26240.0,
        "4" to 41740.0,
        "3" to 52620.0,
        "2" to 66360.0,
        "1" to 83690.0,
        "1/0" to 105600.0,
        "2/0" to 133100.0,
        "3/0" to 167800.0,
        "4/0" to 211600.0,
        "250" to 250000.0,
        "300" to 300000.0,
        "350" to 350000.0,
        "400" to 400000.0,
        "500" to 500000.0
        // Add more sizes as needed
    )

    fun updateVoltage(value: String) {
        _uiState.update { it.copy(voltage = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun updatePhase(value: String) {
        _uiState.update { it.copy(phase = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun updateMaterial(value: String) {
        _uiState.update { it.copy(material = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun updateWireSize(value: String) {
        _uiState.update { it.copy(wireSize = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun updateLoadCurrent(value: String) {
        _uiState.update { it.copy(loadCurrent = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun updateDistance(value: String) {
        _uiState.update { it.copy(distance = value, errorMessage = null, voltageDrop = null, voltageDropPercent = null, endVoltage = null) }
    }

    fun calculateVoltageDrop() {
        viewModelScope.launch {
            try {
                val voltage = _uiState.value.voltage.toDoubleOrNull()
                val loadCurrent = _uiState.value.loadCurrent.toDoubleOrNull()
                val distance = _uiState.value.distance.toDoubleOrNull()
                val wireSize = _uiState.value.wireSize
                val material = _uiState.value.material
                val phase = _uiState.value.phase

                // Validate inputs
                if (voltage == null || voltage <= 0) {
                    _uiState.update { it.copy(errorMessage = "Invalid Voltage") }
                    return@launch
                }
                if (loadCurrent == null || loadCurrent <= 0) {
                    _uiState.update { it.copy(errorMessage = "Invalid Load Current") }
                    return@launch
                }
                if (distance == null || distance <= 0) {
                    _uiState.update { it.copy(errorMessage = "Invalid Distance") }
                    return@launch
                }
                if (wireSize.isEmpty()) {
                    _uiState.update { it.copy(errorMessage = "Please select a Wire Size") }
                    return@launch
                }

                // Convert UI inputs to domain model
                val systemType = if (phase == "Single Phase") SystemType.SINGLE_PHASE else SystemType.THREE_PHASE
                val conductorType = if (material == "Copper") ConductorType.COPPER else ConductorType.ALUMINUM

                // Create input for use case
                val input = VoltageDropInput(
                    systemType = systemType,
                    conductorType = conductorType,
                    conduitMaterial = ConduitMaterial.PVC, // Default to PVC
                    temperatureRating = TemperatureRating.RATING_75C, // Default to 75°C
                    wireSize = wireSize,
                    lengthInFeet = distance,
                    loadInAmps = loadCurrent,
                    voltageInVolts = voltage,
                    powerFactor = 0.95 // Default power factor for most electrical loads
                )

                // Call the use case
                val result = calculateVoltageDropUseCase(input)

                // Update UI state with results
                _uiState.update {
                    it.copy(
                        voltageDrop = result.voltageDropInVolts,
                        voltageDropPercent = result.voltageDropPercentage,
                        endVoltage = result.endVoltage,
                        isWithinRecommendedLimits = result.isWithinRecommendedLimits,
                        errorMessage = null // Clear error on successful calculation
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Calculation error: ${e.message}") }
            }
        }
    }
}

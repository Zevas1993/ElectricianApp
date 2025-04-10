package com.example.electricianapp.presentation.viewmodel.pipebending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.tan
import kotlin.math.cos // Added for saddle calculation

// Enum for bend types
enum class BendType {
    OFFSET, SADDLE
}

// Data class to hold the results of an offset calculation
data class OffsetResult(
    val multiplier: Double = 0.0,
    val shrink: Double = 0.0, // Shrink specific to offset
    val travel: Double = 0.0,
    val distanceBetweenBends: Double = 0.0
)

// Data class to hold the results of a 3-point saddle calculation
data class SaddleResult(
    val centerMarkDistance: Double = 0.0,
    val sideMarkDistance: Double = 0.0,
    val totalShrink: Double = 0.0 // Shrink specific to saddle
)

// Data class to hold the overall UI state
data class PipeBendingUiState(
    val bendType: BendType = BendType.OFFSET,
    val offsetAmount: String = "", // Used for Offset
    val obstacleDiameter: String = "", // Used for Saddle
    val angle: String = "", // Center angle for Saddle, bend angle for Offset
    val offsetResult: OffsetResult? = null,
    val saddleResult: SaddleResult? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null // For non-standard angle warnings or other info
)

@HiltViewModel
class PipeBendingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PipeBendingUiState())
    val uiState: StateFlow<PipeBendingUiState> = _uiState.asStateFlow()

    // Standard shrink values per inch of offset/saddle height for common angles
    // Note: Shrink values can differ slightly between offset and saddle,
    // but using offset values as an approximation for now.
    // TODO: Use more precise saddle shrink values if available.
    private val standardShrinkValues = mapOf(
        10.0 to 1.0 / 16.0, // 1/16"
        22.5 to 3.0 / 16.0, // 3/16"
        30.0 to 1.0 / 4.0,  // 1/4"
        45.0 to 3.0 / 8.0,  // 3/8"
        60.0 to 1.0 / 2.0   // 1/2"
    )
    // Multipliers for saddle side marks (based on center angle)
    private val saddleSideMarkMultipliers = mapOf(
        22.5 to 2.6,
        30.0 to 2.0,
        45.0 to 1.4,
        60.0 to 1.2
    )

    fun updateBendType(type: BendType) {
        _uiState.value = _uiState.value.copy(
            bendType = type,
            offsetResult = null, // Clear results when type changes
            saddleResult = null,
            errorMessage = null,
            infoMessage = null
        )
    }

    fun updateOffsetAmount(amount: String) {
        // Only update if current type is OFFSET
        if (_uiState.value.bendType == BendType.OFFSET) {
            _uiState.value = _uiState.value.copy(offsetAmount = amount, offsetResult = null, errorMessage = null, infoMessage = null)
        }
    }

     fun updateObstacleDiameter(diameter: String) {
         // Only update if current type is SADDLE
        if (_uiState.value.bendType == BendType.SADDLE) {
            _uiState.value = _uiState.value.copy(obstacleDiameter = diameter, saddleResult = null, errorMessage = null, infoMessage = null)
        }
    }


    fun updateAngle(angle: String) {
        // Angle applies to both, clear results
        _uiState.value = _uiState.value.copy(angle = angle, offsetResult = null, saddleResult = null, errorMessage = null, infoMessage = null)
    }

    // Main calculate function delegates based on bendType
    fun calculate() {
        when (_uiState.value.bendType) {
            BendType.OFFSET -> calculateOffset()
            BendType.SADDLE -> calculateSaddle()
        }
    }

    private fun calculateOffset() {
        viewModelScope.launch {
            var infoMsg: String? = null
            val offset = _uiState.value.offsetAmount.toDoubleOrNull()
            val angleDegrees = _uiState.value.angle.toDoubleOrNull()

            if (offset == null || angleDegrees == null) {
                _uiState.value = _uiState.value.copy(offsetResult = null, errorMessage = "Invalid input. Please enter numeric values.", infoMessage = null)
                return@launch
            }

            if (angleDegrees <= 0 || angleDegrees >= 90) {
                 _uiState.value = _uiState.value.copy(offsetResult = null, errorMessage = "Angle must be between 0 and 90 degrees.", infoMessage = null)
                return@launch
            }

            try {
                val angleRadians = Math.toRadians(angleDegrees)
                val multiplier = 1 / sin(angleRadians) // Cosecant(angle)

                // Get shrink value
                val (shrinkPerInch, shrinkInfo) = getShrinkPerInch(angleDegrees)
                infoMsg = shrinkInfo // Capture potential info message about shrink

                val shrink = shrinkPerInch * offset
                val travel = offset * multiplier
                val distanceBetweenBends = travel // For simple offset

                val result = OffsetResult(
                    multiplier = multiplier,
                    shrink = shrink,
                    travel = travel,
                    distanceBetweenBends = distanceBetweenBends
                )
                _uiState.value = _uiState.value.copy(offsetResult = result, saddleResult = null, errorMessage = null, infoMessage = infoMsg)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(offsetResult = null, saddleResult = null, errorMessage = "Calculation error: ${e.message}", infoMessage = null)
            }
        }
    }

     private fun calculateSaddle() {
        viewModelScope.launch {
            var infoMsg: String? = null
            val obstacleHeight = _uiState.value.obstacleDiameter.toDoubleOrNull() // Using diameter field for saddle height
            val centerAngleDegrees = _uiState.value.angle.toDoubleOrNull() // Using angle field for center angle

            if (obstacleHeight == null || centerAngleDegrees == null) {
                _uiState.value = _uiState.value.copy(saddleResult = null, errorMessage = "Invalid input. Please enter numeric values.", infoMessage = null)
                return@launch
            }

             // Saddle angles typically 22.5, 30, 45, 60 for standard multipliers
            if (centerAngleDegrees !in listOf(22.5, 30.0, 45.0, 60.0)) {
                 // Allow calculation but warn that standard multipliers/shrink might be less accurate
                 infoMsg = "Non-standard center angle used. Side mark multiplier and shrink are estimates."
                 //_uiState.value = _uiState.value.copy(saddleResult = null, errorMessage = "Center angle must be 22.5, 30, 45, or 60 degrees for standard calculations.", infoMessage = null)
                 //return@launch
            }
             if (obstacleHeight <= 0) {
                  _uiState.value = _uiState.value.copy(saddleResult = null, errorMessage = "Obstacle height must be positive.", infoMessage = null)
                 return@launch
             }


            try {
                val centerAngleRadians = Math.toRadians(centerAngleDegrees)
                val multiplier = 1 / sin(centerAngleRadians) // Cosecant(angle) for center mark distance calculation

                // Calculate Center Mark Distance
                val centerMarkDistance = obstacleHeight * multiplier

                // Calculate Side Mark Distance using standard multipliers
                val sideMarkMultiplier = saddleSideMarkMultipliers[centerAngleDegrees]
                val sideMarkDistance = if (sideMarkMultiplier != null) {
                    obstacleHeight * sideMarkMultiplier
                } else {
                    // Estimate for non-standard angles (less accurate) - using tan(angle/2) relationship
                    // This is an approximation, real bending charts are better.
                    obstacleHeight / tan(centerAngleRadians / 2)
                }


                // Calculate Shrink
                // Note: Saddle shrink depends on the center angle. Using standard offset shrink values as approximation.
                 val (shrinkPerInch, shrinkInfo) = getShrinkPerInch(centerAngleDegrees)
                 if (shrinkInfo != null && infoMsg == null) infoMsg = shrinkInfo // Combine info messages if needed

                val totalShrink = shrinkPerInch * obstacleHeight

                val result = SaddleResult(
                    centerMarkDistance = centerMarkDistance,
                    sideMarkDistance = sideMarkDistance,
                    totalShrink = totalShrink
                )
                _uiState.value = _uiState.value.copy(saddleResult = result, offsetResult = null, errorMessage = null, infoMessage = infoMsg)
            } catch (e: Exception) {
                 _uiState.value = _uiState.value.copy(offsetResult = null, saddleResult = null, errorMessage = "Calculation error: ${e.message}", infoMessage = null)
            }
        }
    }

    // Helper to get shrink per inch and any associated info message
    private fun getShrinkPerInch(angleDegrees: Double): Pair<Double, String?> {
         val closestStandardAngle = standardShrinkValues.keys.minByOrNull { abs(it - angleDegrees) }
         return if (closestStandardAngle != null && abs(closestStandardAngle - angleDegrees) < 0.1) {
             Pair(standardShrinkValues[closestStandardAngle] ?: 0.0, null)
         } else {
             val estimatedShrink = standardShrinkValues[closestStandardAngle] ?: 0.0
             val info = "Using standard shrink calculation for ${closestStandardAngle ?: "N/A"} degrees. Shrink may vary for non-standard angles."
             Pair(estimatedShrink, info) // Return estimated shrink and info message
         }
    }
}

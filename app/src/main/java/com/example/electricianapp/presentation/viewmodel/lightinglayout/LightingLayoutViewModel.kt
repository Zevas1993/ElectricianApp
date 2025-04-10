package com.example.electricianapp.presentation.viewmodel.lightinglayout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.sqrt

// Data class for calculated spacing results
data class SpacingResult(
    val numRows: Int = 0,
    val numCols: Int = 0,
    val totalLuminaires: Int = 0,
    val spacingLength: Double = 0.0, // Spacing along the length
    val spacingWidth: Double = 0.0,  // Spacing along the width
    val borderSpacingLength: Double = 0.0, // Distance from wall along length
    val borderSpacingWidth: Double = 0.0   // Distance from wall along width
)

// Data class for the overall UI state
data class LightingLayoutUiState(
    val roomLength: String = "",
    val roomWidth: String = "",
    // Optional: Could add inputs for desired spacing or number of luminaires later
    val spacingResult: SpacingResult? = null,
    val errorMessage: String? = null
    // TODO: Add data structure for visualization points if needed
)

@HiltViewModel
class LightingLayoutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LightingLayoutUiState())
    val uiState: StateFlow<LightingLayoutUiState> = _uiState.asStateFlow()

    fun updateRoomLength(length: String) {
        _uiState.value = _uiState.value.copy(roomLength = length, spacingResult = null, errorMessage = null)
    }

    fun updateRoomWidth(width: String) {
        _uiState.value = _uiState.value.copy(roomWidth = width, spacingResult = null, errorMessage = null)
    }

    // Basic calculation assuming a simple grid layout based on area
    // TODO: Refine this logic based on standard lighting design practices (e.g., lumen method, desired footcandles)
    fun calculateLayout() {
        viewModelScope.launch {
            val length = _uiState.value.roomLength.toDoubleOrNull()
            val width = _uiState.value.roomWidth.toDoubleOrNull()

            if (length == null || width == null || length <= 0 || width <= 0) {
                _uiState.value = _uiState.value.copy(errorMessage = "Invalid room dimensions.")
                return@launch
            }

            try {
                // --- Simplified Placeholder Calculation ---
                // This is a very basic example assuming a target spacing or area per luminaire.
                // A real implementation would use more sophisticated methods.
                val assumedAreaPerLuminaire = 100.0 // Example: Assume 1 luminaire per 100 sq ft
                val roomArea = length * width
                val numLuminaires = ceil(roomArea / assumedAreaPerLuminaire).toInt()

                if (numLuminaires == 0) {
                     _uiState.value = _uiState.value.copy(errorMessage = "Cannot calculate layout for zero luminaires.")
                     return@launch
                }

                // Determine rows and columns (aim for roughly square grid cells)
                val ratio = length / width
                var numCols = ceil(sqrt(numLuminaires / ratio)).toInt()
                var numRows = ceil(numLuminaires.toDouble() / numCols).toInt()

                // Adjust if calculation leads to zero rows/cols (shouldn't happen if numLuminaires > 0)
                 if (numCols == 0) numCols = 1
                 if (numRows == 0) numRows = 1

                // Recalculate total based on grid
                val totalInGrid = numRows * numCols

                // Calculate spacing
                val spacingLength = length / numCols
                val spacingWidth = width / numRows
                val borderLength = spacingLength / 2.0
                val borderWidth = spacingWidth / 2.0
                // --- End Simplified Calculation ---

                val result = SpacingResult(
                    numRows = numRows,
                    numCols = numCols,
                    totalLuminaires = totalInGrid, // Use the number fitting the grid
                    spacingLength = spacingLength,
                    spacingWidth = spacingWidth,
                    borderSpacingLength = borderLength,
                    borderSpacingWidth = borderWidth
                )
                _uiState.value = _uiState.value.copy(spacingResult = result, errorMessage = null)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Calculation error: ${e.message}")
            }
        }
    }
}

package com.example.electricianapp.domain.repository.dwellingload // Corrected package

import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity // Import Entity
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult // Corrected import
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for dwelling load calculations
 */
interface DwellingLoadRepository {
    /**
     * Save a dwelling load calculation
     * @param input The calculation input
     * @param result The calculation result
     * @param calculationName A name for the saved calculation
     * @return The ID of the saved calculation
     */
    suspend fun saveCalculation(input: DwellingLoadInput, result: DwellingLoadResult, calculationName: String): Long // Add name parameter

    /**
     * Get the history of saved calculations
     * @return Flow of saved calculation entities
     */
    fun getSavedCalculations(): Flow<List<DwellingLoadCalculationEntity>> // Update return type and name

    /**
     * Get a specific saved calculation entity by ID
     * @param id The calculation ID
     * @return The saved calculation entity, or null if not found
     */
    suspend fun getCalculationById(id: Long): DwellingLoadCalculationEntity? // Update return type

    /**
     * Delete a saved calculation
     * @param calculation The entity to delete
     */
    suspend fun deleteCalculation(calculation: DwellingLoadCalculationEntity) // Update parameter type

    /**
     * Clear all saved calculation history
     */
    suspend fun clearHistory()
}

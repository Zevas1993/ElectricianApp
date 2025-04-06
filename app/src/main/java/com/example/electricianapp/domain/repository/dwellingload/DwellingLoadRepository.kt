package com.example.electricianapp.domain.repository.dwellingload // Corrected package

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
     * @return The ID of the saved calculation
     */
    suspend fun saveCalculation(input: DwellingLoadInput, result: DwellingLoadResult): Long
    
    /**
     * Get the history of calculations
     * @return Flow of pairs of input and result
     */
    suspend fun getCalculationHistory(): Flow<List<Pair<DwellingLoadInput, DwellingLoadResult>>>
    
    /**
     * Get a specific calculation by ID
     * @param id The calculation ID
     * @return The calculation input and result, or null if not found
     */
    suspend fun getCalculationById(id: Long): Pair<DwellingLoadInput, DwellingLoadResult>?
    
    /**
     * Delete a calculation
     * @param id The calculation ID
     */
    suspend fun deleteCalculation(id: Long)
    
    /**
     * Clear all calculation history
     */
    suspend fun clearHistory()
}

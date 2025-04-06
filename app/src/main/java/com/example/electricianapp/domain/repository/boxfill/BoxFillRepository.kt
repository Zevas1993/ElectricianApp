package com.example.electricianapp.domain.repository.boxfill // Corrected package

import com.example.electricianapp.domain.model.boxfill.BoxFillInput // Corrected import
import com.example.electricianapp.domain.model.boxfill.BoxFillResult // Corrected import
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for box fill calculations
 */
interface BoxFillRepository {
    /**
     * Save a box fill calculation
     * @param input The calculation input
     * @param result The calculation result
     * @return The ID of the saved calculation
     */
    suspend fun saveCalculation(input: BoxFillInput, result: BoxFillResult): Long

    /**
     * Get the history of calculations
     * @return Flow of pairs of input and result
     */
    suspend fun getCalculationHistory(): Flow<List<Pair<BoxFillInput, BoxFillResult>>>

    /**
     * Get a specific calculation by ID
     * @param id The calculation ID
     * @return The calculation input and result, or null if not found
     */
    suspend fun getCalculationById(id: Long): Pair<BoxFillInput, BoxFillResult>?

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

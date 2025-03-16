package com.example.electricalcalculator.domain.repository.conduitfill

import com.example.electricalcalculator.domain.model.conduitfill.ConduitFillInput
import com.example.electricalcalculator.domain.model.conduitfill.ConduitFillResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for conduit fill calculations
 */
interface ConduitFillRepository {
    /**
     * Save a conduit fill calculation
     * @param input The calculation input
     * @param result The calculation result
     * @return The ID of the saved calculation
     */
    suspend fun saveCalculation(input: ConduitFillInput, result: ConduitFillResult): Long
    
    /**
     * Get the history of calculations
     * @return Flow of pairs of input and result
     */
    suspend fun getCalculationHistory(): Flow<List<Pair<ConduitFillInput, ConduitFillResult>>>
    
    /**
     * Get a specific calculation by ID
     * @param id The calculation ID
     * @return The calculation input and result, or null if not found
     */
    suspend fun getCalculationById(id: Long): Pair<ConduitFillInput, ConduitFillResult>?
    
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

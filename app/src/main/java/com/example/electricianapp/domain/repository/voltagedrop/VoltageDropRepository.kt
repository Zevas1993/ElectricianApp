package com.example.electricianapp.domain.repository.voltagedrop

import com.example.electricianapp.domain.model.voltagedrop.VoltageDropInput
import com.example.electricianapp.domain.model.voltagedrop.VoltageDropResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for voltage drop calculations.
 * Defines methods for saving, retrieving, and managing voltage drop calculations.
 */
interface VoltageDropRepository {
    
    /**
     * Save a voltage drop calculation to the database
     * @param input The input parameters used for the calculation
     * @param result The result of the calculation
     * @return The ID of the saved calculation
     */
    suspend fun saveCalculation(input: VoltageDropInput, result: VoltageDropResult): Long
    
    /**
     * Get the history of voltage drop calculations
     * @return A flow of pairs containing input parameters and results
     */
    suspend fun getCalculationHistory(): Flow<List<Pair<VoltageDropInput, VoltageDropResult>>>
    
    /**
     * Get a specific voltage drop calculation by ID
     * @param id The ID of the calculation to retrieve
     * @return A pair containing input parameters and result, or null if not found
     */
    suspend fun getCalculationById(id: Long): Pair<VoltageDropInput, VoltageDropResult>?
    
    /**
     * Delete a specific voltage drop calculation
     * @param id The ID of the calculation to delete
     */
    suspend fun deleteCalculation(id: Long)
    
    /**
     * Clear all voltage drop calculation history
     */
    suspend fun clearHistory()
}

package com.example.electricalcalculator.domain.repository

import com.example.electricalcalculator.domain.model.BoxFill
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing BoxFill calculations.
 */
interface BoxFillRepository {
    /**
     * Get all box fill calculations from the data source.
     */
    fun getAllBoxFills(): Flow<List<BoxFill>>
    
    /**
     * Get a box fill calculation by its ID.
     */
    suspend fun getBoxFillById(id: Long): BoxFill?
    
    /**
     * Insert a new box fill calculation into the data source.
     * @return the ID of the newly inserted box fill calculation
     */
    suspend fun insertBoxFill(boxFill: BoxFill): Long
    
    /**
     * Update an existing box fill calculation in the data source.
     */
    suspend fun updateBoxFill(boxFill: BoxFill)
    
    /**
     * Delete a box fill calculation from the data source.
     */
    suspend fun deleteBoxFill(boxFill: BoxFill)
    
    /**
     * Delete a box fill calculation by its ID.
     */
    suspend fun deleteBoxFillById(id: Long)
    
    /**
     * Add a wire to a box fill calculation.
     */
    suspend fun addWireToBoxFill(boxFillId: Long, wireId: Long)
    
    /**
     * Remove a wire from a box fill calculation.
     */
    suspend fun removeWireFromBoxFill(boxFillId: Long, wireId: Long)
}

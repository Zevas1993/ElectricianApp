package com.example.electricalcalculator.domain.repository

import com.example.electricalcalculator.domain.model.Conduit
import com.example.electricalcalculator.domain.model.ConduitFill
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing ConduitFill calculations.
 */
interface ConduitFillRepository {
    /**
     * Get all conduit fill calculations from the data source.
     */
    fun getAllConduitFills(): Flow<List<ConduitFill>>
    
    /**
     * Get a conduit fill calculation by its ID.
     */
    suspend fun getConduitFillById(id: Long): ConduitFill?
    
    /**
     * Insert a new conduit fill calculation into the data source.
     * @return the ID of the newly inserted conduit fill calculation
     */
    suspend fun insertConduitFill(conduitFill: ConduitFill): Long
    
    /**
     * Update an existing conduit fill calculation in the data source.
     */
    suspend fun updateConduitFill(conduitFill: ConduitFill)
    
    /**
     * Delete a conduit fill calculation from the data source.
     */
    suspend fun deleteConduitFill(conduitFill: ConduitFill)
    
    /**
     * Delete a conduit fill calculation by its ID.
     */
    suspend fun deleteConduitFillById(id: Long)
    
    /**
     * Add a wire to a conduit fill calculation.
     */
    suspend fun addWireToConduitFill(conduitFillId: Long, wireId: Long)
    
    /**
     * Remove a wire from a conduit fill calculation.
     */
    suspend fun removeWireFromConduitFill(conduitFillId: Long, wireId: Long)
    
    /**
     * Get all available conduit types.
     */
    fun getAllConduits(): Flow<List<Conduit>>
    
    /**
     * Get a conduit by its ID.
     */
    suspend fun getConduitById(id: Long): Conduit?
}

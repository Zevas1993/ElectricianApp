package com.example.electricalcalculator.domain.repository

import com.example.electricalcalculator.domain.model.Wire
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Wire objects.
 */
interface WireRepository {
    /**
     * Get all wires from the data source.
     */
    fun getAllWires(): Flow<List<Wire>>
    
    /**
     * Get a wire by its ID.
     */
    suspend fun getWireById(id: Long): Wire?
    
    /**
     * Insert a new wire into the data source.
     * @return the ID of the newly inserted wire
     */
    suspend fun insertWire(wire: Wire): Long
    
    /**
     * Update an existing wire in the data source.
     */
    suspend fun updateWire(wire: Wire)
    
    /**
     * Delete a wire from the data source.
     */
    suspend fun deleteWire(wire: Wire)
    
    /**
     * Delete a wire by its ID.
     */
    suspend fun deleteWireById(id: Long)
}

package com.example.electricianapp.data.repository

import com.example.electricianapp.data.local.dao.WireDao
import com.example.electricianapp.data.local.entity.toEntity
import com.example.electricianapp.data.local.entity.toDomainModel
import com.example.electricianapp.domain.model.conduitfill.Wire // Corrected import path
import com.example.electricianapp.domain.repository.WireRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the WireRepository interface.
 * Handles interaction with the Room database for Wire objects.
 */
class WireRepositoryImpl @Inject constructor(
    private val wireDao: WireDao
) : WireRepository {

    /**
     * Get all wires from the data source.
     */
    override fun getAllWires(): Flow<List<Wire>> {
        return wireDao.getAllWires().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Get a wire by its ID.
     */
    override suspend fun getWireById(id: Long): Wire? {
        return wireDao.getWireById(id)?.toDomainModel()
    }

    /**
     * Insert a new wire into the data source.
     * @return the ID of the newly inserted wire
     */
    override suspend fun insertWire(wire: Wire): Long {
        return wireDao.insertWire(wire.toEntity())
    }

    /**
     * Update an existing wire in the data source.
     */
    override suspend fun updateWire(wire: Wire) {
        wireDao.updateWire(wire.toEntity())
    }

    /**
     * Delete a wire from the data source.
     */
    override suspend fun deleteWire(wire: Wire) {
        wireDao.deleteWire(wire.toEntity())
    }

    /**
     * Delete a wire by its ID.
     */
    override suspend fun deleteWireById(id: Long) {
        wireDao.deleteWireById(id)
    }
}

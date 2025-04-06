package com.example.electricianapp.data.local.dao // Corrected package

import androidx.room.*
import com.example.electricianapp.data.local.entity.WireEntity // Corrected import
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Wire entities.
 * Defines database operations for wires.
 * Note: This seems related to a general wire database, potentially separate
 * from the specific needs of conduit/box fill calculations which might use
 * pre-defined NEC tables instead of storing individual wire instances.
 */
@Dao
interface WireDao {

    /**
     * Get all wires from the database.
     * @return A Flow of all wires.
     */
    @Query("SELECT * FROM wires")
    fun getAllWires(): Flow<List<WireEntity>>

    /**
     * Get a wire by its ID.
     * @param id The wire ID.
     * @return The wire with the given ID, or null if not found.
     */
    @Query("SELECT * FROM wires WHERE id = :id")
    suspend fun getWireById(id: Long): WireEntity?

    /**
     * Insert a new wire into the database.
     * @param wire The wire to insert.
     * @return The ID of the newly inserted wire.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWire(wire: WireEntity): Long

    /**
     * Update an existing wire in the database.
     * @param wire The wire to update.
     */
    @Update
    suspend fun updateWire(wire: WireEntity)

    /**
     * Delete a wire from the database.
     * @param wire The wire to delete.
     */
    @Delete
    suspend fun deleteWire(wire: WireEntity)

    /**
     * Delete a wire by its ID.
     * @param id The ID of the wire to delete.
     */
    @Query("DELETE FROM wires WHERE id = :id")
    suspend fun deleteWireById(id: Long)

    /**
     * Get all wires of a specific type.
     * @param type The wire type.
     * @return A Flow of wires matching the given type.
     */
    @Query("SELECT * FROM wires WHERE type = :type")
    fun getWiresByType(type: String): Flow<List<WireEntity>>

    /**
     * Get all wires of a specific size.
     * @param size The wire size.
     * @return A Flow of wires matching the given size.
     */
    @Query("SELECT * FROM wires WHERE size = :size")
    fun getWiresBySize(size: String): Flow<List<WireEntity>>
}

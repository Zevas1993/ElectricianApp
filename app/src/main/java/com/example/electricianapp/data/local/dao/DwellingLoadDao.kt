package com.example.electricianapp.data.local.dao // Corrected package

import androidx.room.*
import com.example.electricianapp.data.local.entity.DwellingLoadEntity // Corrected Entity import
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for dwelling load calculation history.
 */
@Dao
interface DwellingLoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DwellingLoadEntity): Long // Use correct Entity

    @Query("SELECT * FROM dwelling_load_history WHERE id = :id")
    suspend fun getCalculationById(id: Long): DwellingLoadEntity? // Use correct Entity

    // Get all calculations, ordered by timestamp
    @Query("SELECT * FROM dwelling_load_history ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<DwellingLoadEntity>> // Use correct Entity

    @Delete
    suspend fun delete(entity: DwellingLoadEntity) // Use correct Entity

    @Query("DELETE FROM dwelling_load_history") // Remove userId filter
    suspend fun deleteAll()

    // Add other queries if needed
}

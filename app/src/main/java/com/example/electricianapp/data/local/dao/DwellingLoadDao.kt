package com.example.electricianapp.data.local.dao // Corrected package

import androidx.room.*
import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity // Import the new entity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for saved dwelling load calculations.
 */
@Dao
interface DwellingLoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: DwellingLoadCalculationEntity): Long // Use new entity

    @Query("SELECT * FROM dwelling_load_calculations WHERE id = :id") // Use correct table name
    suspend fun getCalculationById(id: Long): DwellingLoadCalculationEntity? // Use new entity

    // Get all saved calculations, ordered by timestamp
    @Query("SELECT * FROM dwelling_load_calculations ORDER BY timestamp DESC") // Use correct table name
    fun getAllCalculations(): Flow<List<DwellingLoadCalculationEntity>> // Use new entity

    @Delete
    suspend fun deleteCalculation(calculation: DwellingLoadCalculationEntity) // Use new entity

    @Query("DELETE FROM dwelling_load_calculations") // Use correct table name
    suspend fun deleteAllCalculations()

    // Add other queries if needed, e.g., update, search by name
}

package com.example.electricianapp.data.local.dao // Corrected package

import androidx.room.*
import com.example.electricianapp.data.local.entity.ConduitFillEntity // Corrected import
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for conduit fill calculation history.
 */
@Dao
interface ConduitFillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conduitFillEntity: ConduitFillEntity): Long // Returns the new rowId

    @Query("SELECT * FROM conduit_fill_history ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<ConduitFillEntity>>

    @Query("SELECT * FROM conduit_fill_history WHERE id = :id")
    suspend fun getCalculationById(id: Long): ConduitFillEntity?

    @Query("DELETE FROM conduit_fill_history WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)

    @Query("DELETE FROM conduit_fill_history")
    suspend fun clearAll()

    // Add other queries if needed, e.g., filtering by conduit type or size
}

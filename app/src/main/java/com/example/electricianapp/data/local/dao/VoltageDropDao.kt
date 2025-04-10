package com.example.electricianapp.data.local.dao

import androidx.room.*
import com.example.electricianapp.data.local.entity.VoltageDropEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for voltage drop calculations.
 * Defines database interactions for voltage drop history.
 */
@Dao
interface VoltageDropDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: VoltageDropEntity): Long

    @Query("SELECT * FROM voltage_drop_calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<VoltageDropEntity>>

    @Query("SELECT * FROM voltage_drop_calculations WHERE id = :id")
    suspend fun getCalculationById(id: Long): VoltageDropEntity?

    @Query("DELETE FROM voltage_drop_calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)

    @Query("DELETE FROM voltage_drop_calculations")
    suspend fun clearAllCalculations()
}

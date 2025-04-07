package com.example.electricianapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.electricianapp.data.local.entity.ConduitFillCalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConduitFillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: ConduitFillCalculationEntity): Long

    @Query("SELECT * FROM conduit_fill_calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<ConduitFillCalculationEntity>>

    @Query("SELECT * FROM conduit_fill_calculations WHERE id = :id")
    suspend fun getCalculationById(id: Long): ConduitFillCalculationEntity?

    @Query("DELETE FROM conduit_fill_calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)

    @Query("DELETE FROM conduit_fill_calculations")
    suspend fun clearAllCalculations()
}

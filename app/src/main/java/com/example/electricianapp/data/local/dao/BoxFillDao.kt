package com.example.electricianapp.data.local.dao // Corrected package

import androidx.room.*
import com.example.electricianapp.data.local.entity.BoxFillInputEntity // Corrected import
import com.example.electricianapp.data.local.entity.BoxFillResultEntity // Corrected import
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for box fill calculation inputs and results.
 * Defines database interactions for box fill history.
 */
@Dao
interface BoxFillDao {

    // --- BoxFillInputEntity Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInput(input: BoxFillInputEntity): Long // Returns the new rowId

    @Query("SELECT * FROM box_fill_inputs WHERE id = :id")
    suspend fun getInputById(id: Long): BoxFillInputEntity?

    @Query("SELECT * FROM box_fill_inputs ORDER BY timestamp DESC")
    fun getAllInputs(): Flow<List<BoxFillInputEntity>>

    @Query("DELETE FROM box_fill_inputs WHERE id = :id")
    suspend fun deleteInputById(id: Long)

    @Query("DELETE FROM box_fill_inputs")
    suspend fun deleteAllInputs()

    // --- BoxFillResultEntity Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: BoxFillResultEntity): Long

    @Query("SELECT * FROM box_fill_results WHERE inputId = :inputId")
    suspend fun getResultByInputId(inputId: Long): BoxFillResultEntity?

    @Query("DELETE FROM box_fill_results WHERE inputId = :inputId")
    suspend fun deleteResultByInputId(inputId: Long)

    @Query("DELETE FROM box_fill_results")
    suspend fun deleteAllResults()

    // --- Combined Operations for History ---

    // Define a data class to hold the combined input and result for history queries
    data class BoxFillHistoryEntry(
        @Embedded val input: BoxFillInputEntity,
        @Relation(
             parentColumn = "id",
             entityColumn = "inputId"
        )
        val result: BoxFillResultEntity? // Result might be nullable if saving input first is possible
    )

    @Transaction // Ensures the query runs in a single transaction
    @Query("SELECT * FROM box_fill_inputs ORDER BY timestamp DESC")
    fun getCalculationHistory(): Flow<List<BoxFillHistoryEntry>>

    @Transaction
    @Query("SELECT * FROM box_fill_inputs WHERE id = :id")
    suspend fun getCalculationById(id: Long): BoxFillHistoryEntry?

    @Transaction
    suspend fun deleteCalculation(id: Long) {
        deleteResultByInputId(id) // Delete result first (or handle potential foreign key constraints)
        deleteInputById(id)
    }

    @Transaction
    suspend fun clearHistory() {
        deleteAllResults()
        deleteAllInputs()
    }
}

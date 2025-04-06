package com.example.electricianapp.data.local.entity // Corrected package

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.conduitfill.ConduitType // Corrected import

/**
 * Entity representing a saved conduit fill calculation in the database history.
 */
@Entity(tableName = "conduit_fill_history") // Define table name
data class ConduitFillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(), // Record when calculation was saved

    // Input parameters
    val conduitType: String, // Store enum as String
    val conduitSize: String,
    val wiresJson: String, // Store list of wires as JSON string

    // Result parameters
    val conduitAreaInSqInches: Double,
    val totalWireAreaInSqInches: Double,
    val fillPercentage: Double,
    val maximumAllowedFillPercentage: Double,
    val isWithinLimits: Boolean,
    val wireDetailsJson: String // Store list of wire details as JSON string

    // Consider adding userId if implementing user-specific history
    // val userId: Long? = null
)

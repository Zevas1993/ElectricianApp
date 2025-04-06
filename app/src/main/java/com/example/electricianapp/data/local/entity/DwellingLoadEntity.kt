package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.dwellingload.DwellingType

/**
 * Entity representing a saved dwelling load calculation in the database history.
 */
@Entity(tableName = "dwelling_load_history") // Define table name
data class DwellingLoadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    // Input parameters
    val dwellingType: String, // Store enum as String
    val squareFootage: Double,
    val smallApplianceCircuits: Int,
    val laundryCircuits: Int,
    val appliancesJson: String, // Store list of Appliance as JSON string

    // Result parameters
    val totalConnectedLoad: Double,
    val totalDemandLoad: Double,
    val serviceSize: Int,
    val generalLightingLoad: Double,
    val smallApplianceLoad: Double,
    val laundryLoad: Double,
    val applianceLoadsJson: String, // Store Map<String, Double> as JSON string
    val demandFactorsJson: String // Store Map<String, Double> as JSON string

    // Consider adding userId if implementing user-specific history
    // val userId: Long? = null
)

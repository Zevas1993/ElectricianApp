package com.example.electricianapp.data.local.entity // Corrected package

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representation of an appliance in the database.
 * This seems related to a more detailed appliance database, potentially
 * distinct from the simple Appliance model used in DwellingLoadInput.
 * Adjust fields as necessary based on actual requirements.
 */
@Entity(tableName = "appliances") // Define table name
data class ApplianceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val wattage: Double,
    val voltageRating: Double?, // Voltage might be optional
    val isMotorLoad: Boolean = false // Default to false if not specified

    // Consider adding fields like:
    // val phase: String? // e.g., SINGLE_PHASE, THREE_PHASE (use enum + TypeConverter)
    // val description: String?
    // val category: String? // e.g., Kitchen, HVAC, Motor
)

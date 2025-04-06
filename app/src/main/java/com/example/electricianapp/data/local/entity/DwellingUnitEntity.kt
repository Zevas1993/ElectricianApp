package com.example.electricianapp.data.local.entity // Corrected package

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representation of a dwelling unit in the database.
 * This seems related to a more detailed Dwelling Unit calculation feature.
 */
@Entity(tableName = "dwelling_units") // Define table name
data class DwellingUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String?, // Name might be optional
    val squareFootage: Double,
    val voltageRating: Int?, // Voltage might be optional
    val timestamp: Long = System.currentTimeMillis() // Record when created/updated

    // Consider adding fields like:
    // val dwellingType: String // e.g., RESIDENTIAL, COMMERCIAL (use enum + TypeConverter)
    // val address: String?
    // val notes: String?
    // val userId: Long? // To associate with a user
)

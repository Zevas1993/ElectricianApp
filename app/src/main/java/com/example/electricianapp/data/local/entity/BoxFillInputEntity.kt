package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.boxfill.BoxType

/**
 * Entity representing the input parameters for a saved box fill calculation.
 */
@Entity(tableName = "box_fill_inputs")
data class BoxFillInputEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long = System.currentTimeMillis(),

    val boxType: String, // Store enum as String
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val componentsJson: String // Store list of BoxComponent as JSON string

    // Consider adding userId if implementing user-specific history
    // val userId: Long? = null
)

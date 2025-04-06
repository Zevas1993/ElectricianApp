package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing the results of a saved box fill calculation.
 */
@Entity(
    tableName = "box_fill_results",
    foreignKeys = [ForeignKey(
        entity = BoxFillInputEntity::class,
        parentColumns = ["id"],
        childColumns = ["inputId"],
        onDelete = ForeignKey.CASCADE // Delete result if input is deleted
    )],
    indices = [Index("inputId")] // Index for faster lookup by inputId
)
data class BoxFillResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val inputId: Long, // Foreign key linking to BoxFillInputEntity

    // Result fields matching BoxFillResult domain model
    val boxType: String, // Store enum as String
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val totalRequiredVolumeInCubicInches: Double,
    val remainingVolumeInCubicInches: Double,
    val fillPercentage: Double,
    val isWithinLimits: Boolean,
    val componentDetailsJson: String // Store list of ComponentDetail as JSON string
)

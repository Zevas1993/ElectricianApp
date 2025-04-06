package com.example.electricianapp.data.local.entity // Corrected package

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross-reference entity to represent many-to-many relationship between dwelling units and appliances.
 * This seems related to a more detailed Dwelling Unit calculation feature, potentially
 * different from the simpler DwellingLoadInput model.
 */
@Entity(
    primaryKeys = ["dwellingUnitId", "applianceId"], // Composite primary key
    foreignKeys = [
        ForeignKey(
            entity = DwellingUnitEntity::class, // Assumes DwellingUnitEntity exists
            parentColumns = ["id"],
            childColumns = ["dwellingUnitId"],
            onDelete = ForeignKey.CASCADE // Delete cross-ref if dwelling unit is deleted
        ),
        ForeignKey(
            entity = ApplianceEntity::class, // Uses the ApplianceEntity we defined
            parentColumns = ["id"],
            childColumns = ["applianceId"],
            onDelete = ForeignKey.CASCADE // Delete cross-ref if appliance is deleted
        )
    ],
    indices = [
        Index("applianceId") // Index for faster lookups based on applianceId
    ]
)
data class DwellingUnitApplianceCrossRef(
    val dwellingUnitId: Long,
    val applianceId: Long,
    val quantity: Int // How many of this specific appliance are in this dwelling unit
)

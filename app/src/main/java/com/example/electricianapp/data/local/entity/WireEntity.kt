package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.conduitfill.Wire // Explicitly use conduitfill Wire
import com.example.electricianapp.domain.model.conduitfill.WireType

/**
 * Room entity for Wire objects.
 * This might represent a general wire definition rather than one specific to conduit fill.
 */
@Entity(tableName = "wires") // Define table name
data class WireEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // Consider using WireType enum with a TypeConverter
    val size: String, // e.g., "14", "12", "1/0", "250"
    val diameter: Double?, // Diameter might be optional or derived
    val quantity: Int = 1, // Quantity might not belong in a general wire definition entity
    val insulated: Boolean = true
    // Consider adding areaInSqInches if this is meant for conduit fill
    // val areaInSqInches: Double?
)

/**
 * Convert a WireEntity to a domain Wire object.
 * Ensure the domain Wire model (from conduitfill) matches the fields used here.
 */
fun WireEntity.toDomainModel(): Wire {
    // Map to the Wire data class from ConduitFillModels.kt
    return Wire(
        type = WireType.valueOf(type), // Convert stored String back to Enum
        size = size,
        quantity = quantity,
        // areaInSqInches needs to be looked up or calculated, not stored directly here?
        // Or add areaInSqInches to WireEntity if it should be stored.
        // For now, using a placeholder or assuming it's handled elsewhere.
        areaInSqInches = diameter ?: 0.0 // Placeholder: Using diameter if available, else 0.0
    )
}

/**
 * Convert a domain Wire object to a WireEntity.
 * Ensure the domain Wire model (from conduitfill) matches the fields used here.
 */
fun Wire.toEntity(): WireEntity {
     // Map from the Wire data class from ConduitFillModels.kt
    return WireEntity(
        // id = 0, // Let Room handle the ID generation
        type = type.name, // Store enum name as string
        size = size,
        diameter = areaInSqInches, // Placeholder: Storing area in diameter field? Revisit this mapping.
        quantity = quantity,
        insulated = true // Assuming insulated
    )
}

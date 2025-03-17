package com.example.electricalcalculator.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricalcalculator.domain.model.Wire

/**
 * Room entity for Wire objects.
 */
@Entity(tableName = "wires")
data class WireEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val size: String,
    val diameter: Double,
    val quantity: Int = 1,
    val insulated: Boolean = true
)

/**
 * Convert a WireEntity to a domain Wire object.
 */
fun WireEntity.toDomainModel(): Wire {
    return Wire(
        id = id,
        type = type,
        size = size,
        diameter = diameter,
        quantity = quantity,
        insulated = insulated
    )
}

/**
 * Convert a domain Wire object to a WireEntity.
 */
fun Wire.toEntity(): WireEntity {
    return WireEntity(
        id = id,
        type = type,
        size = size,
        diameter = diameter,
        quantity = quantity,
        insulated = insulated
    )
}

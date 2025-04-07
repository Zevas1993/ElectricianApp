package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.electricianapp.data.local.converter.Converters // Assuming Converters handles List<Wire> and List<WireDetail>
import com.example.electricianapp.domain.model.conduitfill.ConduitType
import com.example.electricianapp.domain.model.conduitfill.Wire
import com.example.electricianapp.domain.model.conduitfill.WireDetail

@Entity(tableName = "conduit_fill_calculations")
@TypeConverters(Converters::class) // Apply converters for complex types
data class ConduitFillCalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),

    // Input fields
    val conduitType: ConduitType,
    val conduitSize: String,
    val wires: List<Wire>, // Requires TypeConverter

    // Result fields
    val conduitAreaInSqInches: Double,
    val totalWireAreaInSqInches: Double,
    val fillPercentage: Double,
    val maximumAllowedFillPercentage: Double,
    val isWithinLimits: Boolean,
    val wireDetails: List<WireDetail> // Requires TypeConverter
)

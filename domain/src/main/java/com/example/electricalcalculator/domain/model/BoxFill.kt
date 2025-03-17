package com.example.electricalcalculator.domain.model

/**
 * Represents a box fill calculation with all necessary components.
 */
data class BoxFill(
    val id: Long = 0,
    val boxType: String,
    val boxVolume: Double, // in cubic inches
    val wires: List<Wire> = emptyList(),
    val deviceCount: Int = 0,
    val clampCount: Int = 0,
    val supportFittingsCount: Int = 0,
    val equipmentGroundingCount: Int = 0
)

/**
 * Represents the results of a box fill calculation.
 */
data class BoxFillResult(
    val totalVolumeUsed: Double,
    val boxVolume: Double,
    val isAcceptable: Boolean,
    val remainingVolume: Double,
    val wireDetails: List<WireVolumeDetail> = emptyList()
)

/**
 * Detailed breakdown of volume used per wire type.
 */
data class WireVolumeDetail(
    val wireType: String,
    val wireSize: String,
    val wireCount: Int,
    val volumePerWire: Double,
    val totalVolume: Double
)

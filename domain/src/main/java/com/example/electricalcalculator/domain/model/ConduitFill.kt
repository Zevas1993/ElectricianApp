package com.example.electricalcalculator.domain.model

/**
 * Represents a conduit with specific dimensions.
 */
data class Conduit(
    val id: Long = 0,
    val type: String, // EMT, IMC, RMC, etc.
    val tradeSize: String, // 1/2", 3/4", 1", etc.
    val innerDiameter: Double, // in inches
    val area: Double // in square inches
)

/**
 * Represents a conduit fill calculation with all necessary components.
 */
data class ConduitFill(
    val id: Long = 0,
    val conduit: Conduit,
    val wires: List<Wire> = emptyList(),
    val fillPercentage: Double = 40.0 // Default NEC standard for up to 2 wires
)

/**
 * Represents the results of a conduit fill calculation.
 */
data class ConduitFillResult(
    val totalAreaUsed: Double, // in square inches
    val conduitArea: Double, // in square inches
    val percentFilled: Double, // actual percentage filled
    val maxAllowedFill: Double, // maximum allowed fill percentage
    val isAcceptable: Boolean,
    val remainingArea: Double, // in square inches
    val wireDetails: List<WireAreaDetail> = emptyList()
)

/**
 * Detailed breakdown of area used per wire.
 */
data class WireAreaDetail(
    val wireType: String,
    val wireSize: String,
    val wireCount: Int,
    val areaPerWire: Double, // in square inches
    val totalArea: Double // in square inches
)

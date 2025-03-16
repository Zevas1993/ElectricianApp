package com.example.electricalcalculator.domain.model.conduitfill

/**
 * Enum representing different types of conduits
 */
enum class ConduitType {
    EMT,    // Electrical Metallic Tubing
    IMC,    // Intermediate Metal Conduit
    RMC,    // Rigid Metal Conduit
    PVC,    // Polyvinyl Chloride Conduit
    ENT     // Electrical Nonmetallic Tubing
}

/**
 * Enum representing different types of wires
 */
enum class WireType {
    THHN,   // Thermoplastic High Heat-resistant Nylon-coated
    XHHW,   // Cross-linked High Heat-resistant Water-resistant
    THW,    // Thermoplastic Heat and Water-resistant
    THWN,   // Thermoplastic Heat and Water-resistant Nylon-coated
    USE,    // Underground Service Entrance
    TW      // Thermoplastic Wet-rated
}

/**
 * Data class representing a wire for conduit fill calculations
 */
data class Wire(
    val type: WireType,
    val size: String,  // AWG or kcmil size (e.g., "14 AWG", "500 kcmil")
    val quantity: Int,
    val areaInSqInches: Double  // Cross-sectional area in square inches
)

/**
 * Data class representing input parameters for conduit fill calculations
 */
data class ConduitFillInput(
    val conduitType: ConduitType,
    val conduitSize: String,  // Trade size (e.g., "1/2", "3/4", "1")
    val wires: List<Wire> = emptyList()
)

/**
 * Data class representing the result of a conduit fill calculation
 */
data class ConduitFillResult(
    val conduitType: ConduitType,
    val conduitSize: String,
    val conduitAreaInSqInches: Double,
    val totalWireAreaInSqInches: Double,
    val fillPercentage: Double,
    val maximumAllowedFillPercentage: Double,
    val isWithinLimits: Boolean,
    val wireDetails: List<WireDetail>
)

/**
 * Data class representing details of a wire in the calculation result
 */
data class WireDetail(
    val type: WireType,
    val size: String,
    val quantity: Int,
    val areaPerWireInSqInches: Double,
    val totalAreaInSqInches: Double
)

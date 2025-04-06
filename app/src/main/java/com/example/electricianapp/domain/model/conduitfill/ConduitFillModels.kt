package com.example.electricianapp.domain.model.conduitfill // Corrected package

/**
 * Enum representing different types of conduits (NEC Chapter 9, Table 4)
 */
enum class ConduitType {
    EMT,    // Electrical Metallic Tubing
    IMC,    // Intermediate Metal Conduit
    RMC,    // Rigid Metal Conduit
    PVC_SCHEDULE_40, // Rigid PVC Conduit, Schedule 40
    PVC_SCHEDULE_80, // Rigid PVC Conduit, Schedule 80
    ENT     // Electrical Nonmetallic Tubing
    // Add other types like FMC, LFMC, LFNC if needed
}

/**
 * Enum representing different types of insulated conductors (NEC Chapter 9, Table 5)
 */
enum class WireType {
    THHN,   // Thermoplastic High Heat-resistant Nylon-coated
    THWN,   // Thermoplastic Heat and Water-resistant Nylon-coated
    THW,    // Thermoplastic Heat and Water-resistant
    XHHW,   // Cross-linked High Heat-resistant Water-resistant
    USE,    // Underground Service Entrance
    TW      // Thermoplastic Wet-rated
    // Add other insulation types as needed (RHW, FEP, etc.)
}

/**
 * Data class representing a wire/conductor for conduit fill calculations
 */
data class Wire(
    val type: WireType,
    val size: String,  // AWG or kcmil size (e.g., "14", "12", "1/0", "250") - Note: "AWG" or "kcmil" suffix might be handled separately or assumed based on size value
    val quantity: Int,
    val areaInSqInches: Double  // Cross-sectional area in square inches (from NEC Chapter 9, Table 5)
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
    val conduitAreaInSqInches: Double, // Total internal area from NEC Table 4
    val totalWireAreaInSqInches: Double, // Sum of areas of all wires
    val fillPercentage: Double, // (totalWireArea / conduitArea) * 100
    val maximumAllowedFillPercentage: Double, // Based on number of conductors (NEC Table 1)
    val isWithinLimits: Boolean, // fillPercentage <= maximumAllowedFillPercentage
    val wireDetails: List<WireDetail> // Breakdown of individual wire contributions
)

/**
 * Data class representing details of a specific wire type/size group in the calculation result
 */
data class WireDetail(
    val type: WireType,
    val size: String,
    val quantity: Int,
    val areaPerWireInSqInches: Double,
    val totalAreaInSqInches: Double // quantity * areaPerWire
)

package com.example.electricalcalculator.domain.model.conduitfill

/**
 * Represents the details of a specific type and size of wire used in conduit fill.
 * This can represent either an input wire or a calculated detail in the result.
 *
 * @param type A description of the wire insulation type (e.g., "THHN", "XHHW").
 * @param size The AWG wire size (e.g., "14", "12", "1/0").
 * @param quantity The total number of wires of this specific type and size.
 * @param areaPerWireInSqInches The cross-sectional area of a single wire of this type and size (from NEC tables).
 * @param totalAreaInSqInches The total cross-sectional area for all wires of this type and size.
 */
data class WireDetail(
    val type: String,
    val size: String, // Assuming AWG string format
    val quantity: Int,
    val areaPerWireInSqInches: Double, // Renamed for clarity
    val totalAreaInSqInches: Double
)

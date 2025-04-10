package com.example.electricalcalculator.domain.model.conduitfill

/**
 * Represents the result of a conduit fill calculation.
 * @param conduitType The type of conduit used.
 * @param conduitSize The trade size of the conduit.
 * @param conduitAreaInSqInches The internal cross-sectional area of the conduit.
 * @param totalWireAreaInSqInches The total cross-sectional area of all wires.
 * @param fillPercentage The calculated fill percentage.
 * @param maximumAllowedFillPercentage The maximum fill percentage allowed by NEC for the number of conductors.
 * @param isWithinLimits True if the calculated fill percentage is within the allowed limit, false otherwise.
 * @param wireDetails A list containing the details of each wire group used in the calculation.
 */
data class ConduitFillResult(
    val conduitType: ConduitType,
    val conduitSize: String,
    val conduitAreaInSqInches: Double,
    val totalWireAreaInSqInches: Double,
    val fillPercentage: Double,
    val maximumAllowedFillPercentage: Double,
    val isWithinLimits: Boolean,
    val wireDetails: List<WireDetail> // Reusing WireDetail for calculated results
)

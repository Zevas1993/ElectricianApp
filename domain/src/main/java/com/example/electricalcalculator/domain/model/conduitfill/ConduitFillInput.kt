package com.example.electricalcalculator.domain.model.conduitfill

/**
 * Input parameters for the conduit fill calculation.
 * @param conduitType The type of conduit being used.
 * @param conduitSize The trade size of the conduit (e.g., "1/2", "1", "4").
 * @param wires A list of wires to be placed in the conduit.
 */
data class ConduitFillInput(
    val conduitType: ConduitType,
    val conduitSize: String, // Assuming trade size string format
    val wires: List<WireDetail> // Using WireDetail to represent input wires as well
)

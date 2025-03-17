package com.example.electricalcalculator.domain.model

/**
 * Represents a wire with properties needed for calculations.
 */
data class Wire(
    val id: Long = 0,
    val type: String,
    val size: String,
    val diameter: Double,
    val quantity: Int = 1,
    val insulated: Boolean = true
)

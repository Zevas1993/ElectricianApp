package com.example.electricalcalculator.domain.model.dwellingload

/**
 * Represents an appliance used in dwelling load calculations.
 * @param name A descriptive name for the appliance (e.g., "Electric Range", "Dishwasher").
 * @param wattage The power rating of the appliance in watts (or VA if power factor is unknown).
 * @param quantity The number of identical appliances.
 * @param demandFactor The default demand factor (0.0 to 1.0) for this appliance, before special NEC rules are applied.
 */
data class Appliance(
    val name: String,
    val wattage: Double,
    val quantity: Int,
    val demandFactor: Double = 1.0 // Default to 1.0 if not specified
)

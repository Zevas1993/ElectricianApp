package com.example.electricalcalculator.domain.model.dwellingload

/**
 * Represents the result of a dwelling load calculation.
 * @param totalConnectedLoad The sum of all loads before demand factors are applied (VA).
 * @param totalDemandLoad The calculated load after applying NEC demand factors (VA).
 * @param serviceSize The minimum recommended service size in Amperes based on the total demand load.
 * @param generalLightingLoad The calculated general lighting load (VA).
 * @param smallApplianceLoad The calculated small appliance load (VA).
 * @param laundryLoad The calculated laundry load (VA).
 * @param applianceLoads A map of individual appliance names to their calculated connected load (VA).
 * @param demandFactors A map of load types (e.g., "lighting", appliance names) to the applied demand factor (0.0 to 1.0).
 */
data class DwellingLoadResult(
    val totalConnectedLoad: Double,
    val totalDemandLoad: Double,
    val serviceSize: Int,
    val generalLightingLoad: Double,
    val smallApplianceLoad: Double,
    val laundryLoad: Double,
    val applianceLoads: Map<String, Double>,
    val demandFactors: Map<String, Double>
)

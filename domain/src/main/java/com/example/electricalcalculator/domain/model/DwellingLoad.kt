package com.example.electricalcalculator.domain.model

/**
 * Represents an appliance in a dwelling load calculation.
 */
data class Appliance(
    val id: Long = 0,
    val name: String,
    val type: ApplianceType,
    val wattage: Double,
    val quantity: Int = 1
)

/**
 * Enum for different types of appliances with different calculation rules.
 */
enum class ApplianceType {
    GENERAL_LIGHTING,
    SMALL_APPLIANCE,
    LAUNDRY,
    FIXED_APPLIANCE,
    MOTOR,
    RANGE,
    DRYER,
    WATER_HEATER,
    AIR_CONDITIONER,
    HEATING,
    OTHER
}

/**
 * Represents a dwelling unit for load calculations.
 */
data class DwellingUnit(
    val id: Long = 0,
    val squareFootage: Double,
    val appliances: List<Appliance> = emptyList(),
    val voltageSystem: Double = 240.0,
    val phaseType: PhaseType = PhaseType.SINGLE_PHASE
)

/**
 * Enum for different types of electrical phase systems.
 */
enum class PhaseType {
    SINGLE_PHASE,
    THREE_PHASE
}

/**
 * Represents the results of a dwelling load calculation.
 */
data class DwellingLoadResult(
    val totalConnectedLoad: Double, // Total watts before any demand factors
    val generalLightingLoad: Double, // General lighting watts
    val smallApplianceLoad: Double, // Small appliance watts
    val laundryLoad: Double, // Laundry watts
    val largeApplianceLoads: List<ApplianceLoadDetail>, // Fixed and other large appliances
    val totalDemandLoad: Double, // Total watts after applying demand factors
    val minimumAmpacity: Double, // Minimum service ampacity required
    val recommendedServiceSize: Int // Recommended service size in amps (e.g. 100A, 200A)
)

/**
 * Detailed breakdown of load for each appliance after demand factors.
 */
data class ApplianceLoadDetail(
    val applianceName: String,
    val originalLoad: Double, // Original watts
    val demandFactor: Double, // Applied demand factor
    val calculatedLoad: Double // Watts after demand factor
)

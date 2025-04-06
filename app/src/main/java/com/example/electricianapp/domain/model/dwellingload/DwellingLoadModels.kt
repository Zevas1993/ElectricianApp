package com.example.electricianapp.domain.model.dwellingload // Corrected package

/**
 * Enum representing different types of dwellings for load calculations
 */
enum class DwellingType {
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL
}

/**
 * Data class representing an appliance for dwelling load calculations
 */
data class Appliance(
    val name: String,
    val wattage: Double,
    val quantity: Int = 1,
    val demandFactor: Double = 1.0
)

/**
 * Data class representing input parameters for dwelling load calculations
 */
data class DwellingLoadInput(
    val dwellingType: DwellingType,
    val squareFootage: Double,
    val smallApplianceCircuits: Int = 2,
    val laundryCircuits: Int = 1,
    val appliances: List<Appliance> = emptyList()
)

/**
 * Data class representing the result of a dwelling load calculation
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

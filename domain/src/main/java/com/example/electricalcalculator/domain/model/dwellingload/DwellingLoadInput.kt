package com.example.electricalcalculator.domain.model.dwellingload

/**
 * Input parameters for the dwelling load calculation.
 * @param dwellingType The type of dwelling (Residential, Commercial, Industrial).
 * @param squareFootage The total floor area in square feet.
 * @param smallApplianceCircuits The number of small appliance branch circuits (typically 2 for residential).
 * @param laundryCircuits The number of laundry branch circuits (typically 1 for residential).
 * @param appliances A list of appliances in the dwelling.
 */
data class DwellingLoadInput(
    val dwellingType: DwellingType,
    val squareFootage: Double,
    val smallApplianceCircuits: Int,
    val laundryCircuits: Int,
    val appliances: List<Appliance>
)

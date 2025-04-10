package com.example.electricianapp.domain.usecase.dwellingload // Corrected package

import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingType // Corrected import
import javax.inject.Inject // Added missing Inject import
import kotlin.math.ceil
import kotlin.math.max

/**
 * Use case for calculating dwelling load based on NEC standards
 */
class CalculateDwellingLoadUseCase @Inject constructor() { // Added @Inject constructor

    /**
     * Calculate dwelling load based on input parameters
     * @param input The dwelling load calculation input parameters
     * @return The dwelling load calculation result
     */
    operator fun invoke(input: DwellingLoadInput): DwellingLoadResult {
        // Calculate general lighting load
        val generalLightingLoad = calculateGeneralLightingLoad(input)

        // Calculate small appliance load
        val smallApplianceLoad = calculateSmallApplianceLoad(input)

        // Calculate laundry load
        val laundryLoad = calculateLaundryLoad(input)

        // Calculate total lighting and receptacle load before demand factors
        val totalLightingAndReceptacleLoad = generalLightingLoad + smallApplianceLoad + laundryLoad

        // Apply demand factors to lighting and receptacle load (NEC 220.42)
        val lightingDemandLoad = applyLightingDemandFactors(totalLightingAndReceptacleLoad)

        // Calculate appliance loads and apply demand factors (NEC 220.53, 220.54, 220.55)
        val (applianceLoads, applianceDemandFactors) = calculateApplianceDemandLoads(input.appliances)
        val totalApplianceDemandLoad = applianceLoads.entries.sumOf { (name, load) -> // Replace sumByDouble with sumOf
            load * (applianceDemandFactors[name] ?: 1.0)
        }

        // Calculate total connected load (sum of all loads before demand factors)
        val totalConnectedLoad = totalLightingAndReceptacleLoad + applianceLoads.values.sum()

        // Calculate total demand load (sum after applying all demand factors)
        val totalDemandLoad = lightingDemandLoad + totalApplianceDemandLoad

        // Calculate service size (in amperes) based on total demand load
        // Assuming 240V single-phase service for typical dwellings
        val serviceSize = calculateServiceSize(totalDemandLoad, 240.0)

        // Combine demand factors for reporting
        val allDemandFactors = applianceDemandFactors.toMutableMap()
        if (totalLightingAndReceptacleLoad > 0) {
             allDemandFactors["General Lighting & Receptacles"] = lightingDemandLoad / totalLightingAndReceptacleLoad
        }


        return DwellingLoadResult(
            totalConnectedLoad = totalConnectedLoad,
            totalDemandLoad = totalDemandLoad,
            serviceSize = serviceSize,
            generalLightingLoad = generalLightingLoad, // Report the calculated value before demand
            smallApplianceLoad = smallApplianceLoad, // Report the calculated value before demand
            laundryLoad = laundryLoad, // Report the calculated value before demand
            applianceLoads = applianceLoads, // Report individual appliance loads before demand
            demandFactors = allDemandFactors // Report the applied demand factors
        )
    }

    /**
     * Calculate general lighting load based on square footage (NEC 220.12)
     * @param input The dwelling load calculation input
     * @return The general lighting load in VA
     */
    private fun calculateGeneralLightingLoad(input: DwellingLoadInput): Double {
        // NEC 220.12 specifies 3 VA per square foot for dwelling units
        // Other occupancy types have different values (Table 220.12)
        val vaPerSquareFoot = when (input.dwellingType) {
            DwellingType.RESIDENTIAL -> 3.0 // Standard for dwelling units
            // Add other types if needed, referencing NEC Table 220.12
            else -> 3.0 // Defaulting to residential
        }
        return input.squareFootage * vaPerSquareFoot
    }

    /**
     * Calculate small appliance branch circuit load (NEC 220.52(A))
     * @param input The dwelling load calculation input
     * @return The small appliance load in VA
     */
    private fun calculateSmallApplianceLoad(input: DwellingLoadInput): Double {
        // NEC 220.52(A) requires minimum 2 small-appliance circuits at 1500 VA each
        return max(input.smallApplianceCircuits.toDouble(), 2.0) * 1500.0
    }

    /**
     * Calculate laundry branch circuit load (NEC 220.52(B))
     * @param input The dwelling load calculation input
     * @return The laundry load in VA
     */
    private fun calculateLaundryLoad(input: DwellingLoadInput): Double {
        // NEC 220.52(B) requires minimum 1 laundry circuit at 1500 VA if laundry facilities exist
        return max(input.laundryCircuits.toDouble(), 1.0) * 1500.0
    }

    /**
     * Apply demand factors to general lighting and receptacle load (NEC Table 220.42)
     * @param totalLoad The sum of general lighting, small appliance, and laundry loads in VA
     * @return The load after applying demand factors
     */
    private fun applyLightingDemandFactors(totalLoad: Double): Double {
        // NEC Table 220.42 Demand Factors for General Lighting and Small Appliance/Laundry Loads
        return when {
            totalLoad <= 3000 -> totalLoad * 1.00 // First 3000 VA at 100%
            totalLoad <= 120000 -> 3000 + ((totalLoad - 3000) * 0.35) // Next 117,000 VA at 35%
            else -> 3000 + (117000 * 0.35) + ((totalLoad - 120000) * 0.25) // Remainder over 120,000 VA at 25%
        }
    }

    /**
     * Calculate loads and demand factors for appliances (NEC 220.53, 220.54, 220.55)
     * @param appliances List of appliances
     * @return Pair containing: Map of appliance name to load (VA), Map of appliance name to demand factor
     */
    private fun calculateApplianceDemandLoads(appliances: List<Appliance>): Pair<Map<String, Double>, Map<String, Double>> {
        val applianceLoads = mutableMapOf<String, Double>()
        val applianceDemandFactors = mutableMapOf<String, Double>()

        // Separate appliances for specific demand factor calculations
        val ranges = appliances.filter { it.name.contains("range", ignoreCase = true) }
        val dryers = appliances.filter { it.name.contains("dryer", ignoreCase = true) }
        // Fixed appliances (excluding ranges, dryers, space heating, AC) - NEC 220.53
        val fixedAppliances = appliances.filter {
            !it.name.contains("range", ignoreCase = true) &&
            !it.name.contains("dryer", ignoreCase = true) &&
            !it.name.contains("hvac", ignoreCase = true) &&
            !it.name.contains("air condition", ignoreCase = true) &&
            !it.name.contains("heat", ignoreCase = true)
            // Add more exclusions if necessary (e.g., specific motor loads handled elsewhere)
        }
        // HVAC loads (often largest of heating or AC is used, NEC 220.60) - simplified here
        val hvacAppliances = appliances.filter {
             it.name.contains("hvac", ignoreCase = true) ||
             it.name.contains("air condition", ignoreCase = true) ||
             it.name.contains("heat", ignoreCase = true)
        }

        // 1. Calculate individual appliance loads (before demand)
        appliances.forEach { appliance ->
            applianceLoads[appliance.name] = appliance.wattage * appliance.quantity
        }

        // 2. Apply demand factors

        // --- Apply Demand Factors ---

        // Ranges (NEC Table 220.55) - Implementing more detailed logic
        val numRanges = ranges.sumOf { it.quantity }
        if (numRanges > 0) {
            // Note: This implementation assumes standard calculation method. Optional methods exist.
            // It simplifies column B/C logic. A full implementation might need UI for rating selection.
            // For simplicity, we'll use Column A logic (up to 12kW ranges) as a base.
            val demandKw = when (numRanges) {
                1 -> 8.0
                2 -> 11.0
                3 -> 14.0
                4 -> 17.0
                5 -> 20.0
                6 -> 21.0
                7 -> 22.0
                8 -> 23.0
                9 -> 24.0
                10 -> 25.0
                11 -> 26.0
                12 -> 27.0
                13 -> 28.0
                14 -> 29.0
                15 -> 30.0
                16 -> 31.0
                17 -> 32.0
                18 -> 33.0
                19 -> 34.0
                20 -> 35.0
                21 -> 36.0
                22 -> 37.0
                23 -> 38.0
                24 -> 39.0
                25 -> 40.0
                in 26..30 -> 15.0 + numRanges // 15kW + 1kW per range
                in 31..40 -> 15.0 + numRanges // (Simplified - Table uses 15kW + 1kW)
                in 41..50 -> 25.0 + (0.75 * numRanges) // (Simplified - Table uses 25kW + 3/4kW per range)
                in 51..60 -> 25.0 + (0.75 * numRanges) // (Simplified)
                else -> 25.0 + (0.75 * numRanges) // (Simplified - over 61 ranges)
            } * 1000 // Convert kW to VA (assuming power factor 1.0)

            val totalRangeLoad = ranges.sumOf { applianceLoads[it.name] ?: 0.0 }
            val effectiveDemandFactor = if (totalRangeLoad > 0) demandKw / totalRangeLoad else 1.0
            ranges.forEach { applianceDemandFactors[it.name] = effectiveDemandFactor }
        }


        // Dryers (NEC Table 220.54) - Minimum 5000W or nameplate, demand factors apply
        val numDryers = dryers.sumOf { it.quantity }
        if (numDryers > 0) {
            val dryerDemandFactor = when {
                numDryers <= 4 -> 1.00
                numDryers == 5 -> 0.85
                numDryers == 6 -> 0.75
                numDryers == 7 -> 0.65
                numDryers == 8 -> 0.60
                numDryers == 9 -> 0.55
                numDryers == 10 -> 0.50
                numDryers == 11 -> 0.47
                // For 12-23 units: 47% - 1% for each dryer over 11
                numDryers in 12..23 -> 0.47 - (numDryers - 11) * 0.01
                // For 24-42 units: 35% + 0.5% for each dryer over 23 (simplified from table note)
                numDryers in 24..42 -> 0.35 + (numDryers - 23) * 0.005
                // Over 42 units: 25% + 0.25% for each dryer over 42 (simplified from table note)
                else -> 0.25 + (numDryers - 42) * 0.0025
            }.coerceAtLeast(0.25) // Ensure minimum demand factor

            // Apply the factor. Note: NEC uses minimum 5000VA per dryer *before* demand.
            // The current structure applies factor to nameplate. This might need adjustment
            // depending on how total load is calculated later. For now, apply to nameplate load.
            dryers.forEach { applianceDemandFactors[it.name] = dryerDemandFactor }
        }


        // Fixed Appliances (NEC 220.53) - 75% demand factor if 4 or more (excluding ranges, dryers, HVAC)
        if (fixedAppliances.size >= 4) {
            fixedAppliances.forEach { appliance ->
                // Only apply if no specific factor was already set (like for range/dryer)
                applianceDemandFactors.putIfAbsent(appliance.name, 0.75)
            }
        }

        // HVAC (NEC 220.60) - Typically 100% of largest + potentially others depending on non-coincident operation.
        // Simplified: Assume 100% demand factor for all HVAC listed.
        hvacAppliances.forEach { appliance ->
             applianceDemandFactors.putIfAbsent(appliance.name, 1.0)
        }

        // Ensure all appliances have a default demand factor of 1.0 if not otherwise specified
        appliances.forEach { appliance ->
            applianceDemandFactors.putIfAbsent(appliance.name, 1.0)
        }


        return Pair(applianceLoads, applianceDemandFactors)
    }


    /**
     * Calculate service size in amperes based on total demand load and voltage
     * @param totalDemandLoad The total demand load in VA
     * @param voltage The system voltage (e.g., 240V)
     * @return The minimum service size in amperes (rounded up to standard sizes)
     */
    private fun calculateServiceSize(totalDemandLoad: Double, voltage: Double): Int {
        if (voltage <= 0) return 0 // Avoid division by zero
        val calculatedAmps = totalDemandLoad / voltage

        // Round up to the next standard service size (NEC 230.79) - Common sizes shown
        return when {
            calculatedAmps <= 15 -> 15
            calculatedAmps <= 20 -> 20
            calculatedAmps <= 30 -> 30
            calculatedAmps <= 60 -> 60
            calculatedAmps <= 100 -> 100
            calculatedAmps <= 125 -> 125
            calculatedAmps <= 150 -> 150
            calculatedAmps <= 175 -> 175
            calculatedAmps <= 200 -> 200
            calculatedAmps <= 225 -> 225
            calculatedAmps <= 250 -> 250
            calculatedAmps <= 300 -> 300
            calculatedAmps <= 400 -> 400
            calculatedAmps <= 500 -> 500
            calculatedAmps <= 600 -> 600
            // Add larger standard sizes if needed (800, 1000, 1200...)
            else -> (ceil(calculatedAmps / 100.0) * 100).toInt() // Fallback: round up to next 100A
        }
    }
}

// Extension function to sum by double (already present in previous file, ensure only one definition exists)
// private inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double {
//     var sum = 0.0
//     for (element in this) {
//         sum += selector(element)
//     }
//     return sum
// }

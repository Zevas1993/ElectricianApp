package com.example.electricianapp.domain.usecase.dwellingload // Corrected package

import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult // Corrected import
import com.example.electricianapp.domain.model.dwellingload.DwellingType // Corrected import
import kotlin.math.ceil
import javax.inject.Inject // Import Inject

/**
 * Use case for calculating dwelling load based on NEC standards
 */
class CalculateDwellingLoadUseCase @Inject constructor() { // Add @Inject constructor

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
        
        // Calculate total lighting and receptacle load
        val totalLightingLoad = generalLightingLoad + smallApplianceLoad + laundryLoad
        
        // Apply demand factors to lighting load
        val lightingDemandLoad = applyLightingDemandFactors(totalLightingLoad)
        
        // Calculate appliance loads
        val applianceLoads = calculateApplianceLoads(input.appliances)
        
        // Apply demand factors to appliances
        val applianceDemandFactors = calculateApplianceDemandFactors(input.appliances)
        
        // Calculate total connected load
        val totalConnectedLoad = totalLightingLoad + applianceLoads.values.sum()
        
        // Calculate total demand load
        val totalDemandLoad = lightingDemandLoad + 
                              applianceLoads.entries.sumByDouble { (name, load) -> 
                                  load * (applianceDemandFactors[name] ?: 1.0) 
                              }
        
        // Calculate service size (in amperes)
        val serviceSize = calculateServiceSize(totalDemandLoad)
        
        return DwellingLoadResult(
            totalConnectedLoad = totalConnectedLoad,
            totalDemandLoad = totalDemandLoad,
            serviceSize = serviceSize,
            generalLightingLoad = generalLightingLoad,
            smallApplianceLoad = smallApplianceLoad,
            laundryLoad = laundryLoad,
            applianceLoads = applianceLoads,
            demandFactors = applianceDemandFactors + mapOf("lighting" to (lightingDemandLoad / totalLightingLoad))
        )
    }
    
    /**
     * Calculate general lighting load based on square footage
     * @param input The dwelling load calculation input
     * @return The general lighting load in VA
     */
    private fun calculateGeneralLightingLoad(input: DwellingLoadInput): Double {
        val vaPerSquareFoot = when (input.dwellingType) {
            DwellingType.RESIDENTIAL -> 3.0
            DwellingType.COMMERCIAL -> 3.5
            DwellingType.INDUSTRIAL -> 2.5
        }
        return input.squareFootage * vaPerSquareFoot
    }
    
    /**
     * Calculate small appliance load
     * @param input The dwelling load calculation input
     * @return The small appliance load in VA
     */
    private fun calculateSmallApplianceLoad(input: DwellingLoadInput): Double {
        return input.smallApplianceCircuits * 1500.0
    }
    
    /**
     * Calculate laundry load
     * @param input The dwelling load calculation input
     * @return The laundry load in VA
     */
    private fun calculateLaundryLoad(input: DwellingLoadInput): Double {
        return input.laundryCircuits * 1500.0
    }
    
    /**
     * Apply demand factors to lighting load according to NEC Table 220.42
     * @param totalLightingLoad The total lighting load in VA
     * @return The lighting load after applying demand factors
     */
    private fun applyLightingDemandFactors(totalLightingLoad: Double): Double {
        return when {
            totalLightingLoad <= 3000 -> totalLightingLoad
            else -> 3000 + (totalLightingLoad - 3000) * 0.35
        }
    }
    
    /**
     * Calculate loads for each appliance
     * @param appliances List of appliances
     * @return Map of appliance name to load in VA
     */
    private fun calculateApplianceLoads(appliances: List<Appliance>): Map<String, Double> {
        return appliances.associate { appliance ->
            appliance.name to (appliance.wattage * appliance.quantity)
        }
    }
    
    /**
     * Calculate demand factors for appliances
     * @param appliances List of appliances
     * @return Map of appliance name to demand factor
     */
    private fun calculateApplianceDemandFactors(appliances: List<Appliance>): Map<String, Double> {
        // Apply special demand factors for ranges, dryers, etc. according to NEC
        val applianceDemandFactors = mutableMapOf<String, Double>()
        
        // Apply demand factors from NEC for each appliance
        appliances.forEach { appliance ->
            // Apply specific demand factors based on appliance type
            val demandFactor = when {
                appliance.name.contains("range", ignoreCase = true) -> calculateRangeDemandFactor(appliance)
                appliance.name.contains("dryer", ignoreCase = true) -> calculateDryerDemandFactor(appliance)
                else -> appliance.demandFactor
            }
            applianceDemandFactors[appliance.name] = demandFactor
        }
        
        // Apply 75% demand factor for 4 or more fastened-in-place appliances
        val fastenedAppliances = appliances.filter { 
            !it.name.contains("range", ignoreCase = true) && 
            !it.name.contains("dryer", ignoreCase = true) &&
            !it.name.contains("hvac", ignoreCase = true) &&
            !it.name.contains("air condition", ignoreCase = true) &&
            !it.name.contains("heat", ignoreCase = true) &&
            it.wattage >= 500
        }
        
        if (fastenedAppliances.size >= 4) {
            fastenedAppliances.forEach { appliance ->
                applianceDemandFactors[appliance.name] = 0.75
            }
        }
        
        return applianceDemandFactors
    }
    
    /**
     * Calculate demand factor for electric ranges according to NEC Table 220.55
     * @param appliance The range appliance
     * @return The demand factor for the range
     */
    private fun calculateRangeDemandFactor(appliance: Appliance): Double {
        return when {
            appliance.wattage <= 12000 && appliance.quantity == 1 -> 0.8  // Column C for single range
            appliance.quantity > 1 -> {
                when (appliance.quantity) {
                    2 -> 0.75
                    3 -> 0.7
                    4 -> 0.66
                    5 -> 0.62
                    else -> 0.5  // For 6 or more ranges
                }
            }
            else -> appliance.demandFactor
        }
    }
    
    /**
     * Calculate demand factor for electric dryers according to NEC Table 220.54
     * @param appliance The dryer appliance
     * @return The demand factor for the dryer
     */
    private fun calculateDryerDemandFactor(appliance: Appliance): Double {
        return when (appliance.quantity) {
            1 -> 1.0
            2 -> 0.75
            3 -> 0.7
            4 -> 0.65
            else -> 0.5  // For 5 or more dryers
        }
    }
    
    /**
     * Calculate service size in amperes based on total demand load
     * @param totalDemandLoad The total demand load in VA
     * @return The service size in amperes
     */
    private fun calculateServiceSize(totalDemandLoad: Double): Int {
        // Assuming 240V service for residential
        val amperes = totalDemandLoad / 240.0
        
        // Round up to the next standard service size
        return when {
            amperes <= 100 -> 100
            amperes <= 125 -> 125
            amperes <= 150 -> 150
            amperes <= 200 -> 200
            amperes <= 225 -> 225
            amperes <= 400 -> 400
            else -> ceil(amperes / 100).toInt() * 100  // Round up to next 100A increment
        }
    }
}

// Extension function to sum by double
private inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double {
    var sum = 0.0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

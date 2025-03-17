package com.example.electricalcalculator.domain.usecase

import com.example.electricalcalculator.domain.model.*

/**
 * Use case for calculating dwelling load requirements according to NEC standards.
 */
class CalculateDwellingLoadUseCase {
    
    /**
     * Execute the dwelling load calculation.
     * 
     * @param dwellingUnit The dwelling unit data to calculate
     * @return DwellingLoadResult containing calculation results
     */
    operator fun invoke(dwellingUnit: DwellingUnit): DwellingLoadResult {
        // Track all loads and apply demand factors
        var totalConnectedLoad = 0.0
        var totalDemandLoad = 0.0
        
        // Calculate general lighting load (3 VA per square foot)
        val generalLightingLoad = dwellingUnit.squareFootage * 3
        totalConnectedLoad += generalLightingLoad
        
        // Calculate small appliance load (1500 VA for each 20A circuit, minimum of 2)
        val smallApplianceLoad = 3000.0 // 2 * 1500
        totalConnectedLoad += smallApplianceLoad
        
        // Calculate laundry load (1500 VA)
        val laundryLoad = 1500.0
        totalConnectedLoad += laundryLoad
        
        // Initial 10,000 VA subject to 100% demand factor
        val firstTenThousand = minOf(10000.0, generalLightingLoad + smallApplianceLoad + laundryLoad)
        
        // Remainder subject to 40% demand factor
        val remainder = maxOf(0.0, generalLightingLoad + smallApplianceLoad + laundryLoad - 10000.0)
        
        // Apply demand factors to standard loads
        val standardLoadDemand = firstTenThousand + (remainder * 0.4)
        totalDemandLoad += standardLoadDemand
        
        // Process other appliances
        val largeApplianceLoads = mutableListOf<ApplianceLoadDetail>()
        
        // Group appliances by type to apply appropriate demand factors
        val appliancesByType = dwellingUnit.appliances.groupBy { it.type }
        
        // We've already accounted for general lighting, small appliance, and laundry loads
        // Process the rest of the appliances
        
        // FIXED APPLIANCES (applying 75% demand factor for 4+ appliances)
        processApplianceType(
            appliances = appliancesByType[ApplianceType.FIXED_APPLIANCE] ?: emptyList(),
            demandFactor = { count -> if (count >= 4) 0.75 else 1.0 },
            largeApplianceLoads = largeApplianceLoads,
            totalConnectedLoad = { load -> totalConnectedLoad += load },
            totalDemandLoad = { load -> totalDemandLoad += load }
        )
        
        // RANGES (apply NEC demand factors based on total KW)
        processRanges(
            appliances = appliancesByType[ApplianceType.RANGE] ?: emptyList(),
            largeApplianceLoads = largeApplianceLoads,
            totalConnectedLoad = { load -> totalConnectedLoad += load },
            totalDemandLoad = { load -> totalDemandLoad += load }
        )
        
        // DRYER (100% demand factor)
        processApplianceType(
            appliances = appliancesByType[ApplianceType.DRYER] ?: emptyList(),
            demandFactor = { 1.0 },
            largeApplianceLoads = largeApplianceLoads,
            totalConnectedLoad = { load -> totalConnectedLoad += load },
            totalDemandLoad = { load -> totalDemandLoad += load }
        )
        
        // HEATING/AC (use larger of the two, not both simultaneously)
        processHeatingAndCooling(
            heating = appliancesByType[ApplianceType.HEATING] ?: emptyList(),
            cooling = appliancesByType[ApplianceType.AIR_CONDITIONER] ?: emptyList(),
            largeApplianceLoads = largeApplianceLoads,
            totalConnectedLoad = { load -> totalConnectedLoad += load },
            totalDemandLoad = { load -> totalDemandLoad += load }
        )
        
        // OTHER APPLIANCES (100% demand factor)
        processApplianceType(
            appliances = appliancesByType[ApplianceType.OTHER] ?: emptyList(),
            demandFactor = { 1.0 },
            largeApplianceLoads = largeApplianceLoads,
            totalConnectedLoad = { load -> totalConnectedLoad += load },
            totalDemandLoad = { load -> totalDemandLoad += load }
        )
        
        // Calculate service size
        // Convert to amps: Watts ÷ Volts = Amps
        val minimumAmpacity = totalDemandLoad / dwellingUnit.voltageSystem
        
        // Determine recommended service size
        val recommendedServiceSize = determineServiceSize(minimumAmpacity)
        
        return DwellingLoadResult(
            totalConnectedLoad = totalConnectedLoad,
            generalLightingLoad = generalLightingLoad,
            smallApplianceLoad = smallApplianceLoad,
            laundryLoad = laundryLoad,
            largeApplianceLoads = largeApplianceLoads,
            totalDemandLoad = totalDemandLoad,
            minimumAmpacity = minimumAmpacity,
            recommendedServiceSize = recommendedServiceSize
        )
    }
    
    /**
     * Process appliances of a specific type, applying the appropriate demand factor.
     */
    private fun processApplianceType(
        appliances: List<Appliance>,
        demandFactor: (Int) -> Double,
        largeApplianceLoads: MutableList<ApplianceLoadDetail>,
        totalConnectedLoad: (Double) -> Unit,
        totalDemandLoad: (Double) -> Unit
    ) {
        if (appliances.isEmpty()) return
        
        // Calculate total connected load for this type
        val connectedLoad = appliances.sumOf { it.wattage * it.quantity }
        totalConnectedLoad(connectedLoad)
        
        // Apply demand factor
        val factor = demandFactor(appliances.size)
        val demandLoad = connectedLoad * factor
        totalDemandLoad(demandLoad)
        
        // Add details for each appliance
        appliances.forEach { appliance ->
            val originalLoad = appliance.wattage * appliance.quantity
            val calculatedLoad = originalLoad * factor
            
            largeApplianceLoads.add(
                ApplianceLoadDetail(
                    applianceName = appliance.name,
                    originalLoad = originalLoad,
                    demandFactor = factor,
                    calculatedLoad = calculatedLoad
                )
            )
        }
    }
    
    /**
     * Special processing for ranges according to NEC rules.
     */
    private fun processRanges(
        appliances: List<Appliance>,
        largeApplianceLoads: MutableList<ApplianceLoadDetail>,
        totalConnectedLoad: (Double) -> Unit,
        totalDemandLoad: (Double) -> Unit
    ) {
        if (appliances.isEmpty()) return
        
        // Calculate total connected load for ranges
        val connectedLoad = appliances.sumOf { it.wattage * it.quantity }
        totalConnectedLoad(connectedLoad)
        
        // Determine the demand factor based on NEC Table 220.55
        val totalKW = connectedLoad / 1000
        val factor = when {
            totalKW <= 12 -> 0.8
            totalKW <= 27 -> 0.8 - ((totalKW - 12) * 0.01) // Decreases by 1% for each kW over 12
            totalKW <= 30 -> 0.65
            else -> 0.65 - ((totalKW - 30) * 0.005) // Decreases by 0.5% for each kW over 30, minimum 30%
        }.coerceAtLeast(0.3)
        
        val demandLoad = connectedLoad * factor
        totalDemandLoad(demandLoad)
        
        // Add details for each range
        appliances.forEach { appliance ->
            val originalLoad = appliance.wattage * appliance.quantity
            val calculatedLoad = originalLoad * factor
            
            largeApplianceLoads.add(
                ApplianceLoadDetail(
                    applianceName = appliance.name,
                    originalLoad = originalLoad,
                    demandFactor = factor,
                    calculatedLoad = calculatedLoad
                )
            )
        }
    }
    
    /**
     * Special processing for heating and cooling loads (use larger of the two).
     */
    private fun processHeatingAndCooling(
        heating: List<Appliance>,
        cooling: List<Appliance>,
        largeApplianceLoads: MutableList<ApplianceLoadDetail>,
        totalConnectedLoad: (Double) -> Unit,
        totalDemandLoad: (Double) -> Unit
    ) {
        val heatingLoad = heating.sumOf { it.wattage * it.quantity }
        val coolingLoad = cooling.sumOf { it.wattage * it.quantity }
        
        // Add both to connected load for completeness
        totalConnectedLoad(heatingLoad + coolingLoad)
        
        // Add only the larger of the two to demand load
        val largerLoad = maxOf(heatingLoad, coolingLoad)
        totalDemandLoad(largerLoad)
        
        // Add details
        if (heatingLoad > 0) {
            largeApplianceLoads.add(
                ApplianceLoadDetail(
                    applianceName = "Heating",
                    originalLoad = heatingLoad,
                    demandFactor = if (heatingLoad >= coolingLoad) 1.0 else 0.0,
                    calculatedLoad = if (heatingLoad >= coolingLoad) heatingLoad else 0.0
                )
            )
        }
        
        if (coolingLoad > 0) {
            largeApplianceLoads.add(
                ApplianceLoadDetail(
                    applianceName = "Air Conditioning",
                    originalLoad = coolingLoad,
                    demandFactor = if (coolingLoad > heatingLoad) 1.0 else 0.0,
                    calculatedLoad = if (coolingLoad > heatingLoad) coolingLoad else 0.0
                )
            )
        }
    }
    
    /**
     * Determine the recommended service size based on the calculated minimum ampacity.
     */
    private fun determineServiceSize(minimumAmpacity: Double): Int {
        return when {
            minimumAmpacity <= 60 -> 60
            minimumAmpacity <= 100 -> 100
            minimumAmpacity <= 125 -> 125
            minimumAmpacity <= 150 -> 150
            minimumAmpacity <= 175 -> 175
            minimumAmpacity <= 200 -> 200
            minimumAmpacity <= 225 -> 225
            minimumAmpacity <= 300 -> 300
            minimumAmpacity <= 350 -> 350
            minimumAmpacity <= 400 -> 400
            else -> 400 // For larger loads, may need special service
        }
    }
}

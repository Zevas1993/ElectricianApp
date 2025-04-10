package com.example.electricianapp.domain.usecase.voltagedrop

import com.example.electricianapp.domain.model.voltagedrop.*
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Use case for calculating voltage drop in electrical circuits.
 * Based on NEC guidelines and standard electrical engineering formulas.
 */
class CalculateVoltageDropUseCase @Inject constructor() {

    /**
     * Calculate voltage drop based on input parameters
     * @param input The voltage drop calculation input parameters
     * @return The voltage drop calculation result
     */
    operator fun invoke(input: VoltageDropInput): VoltageDropResult {
        // Get resistance and reactance values based on conductor properties
        val resistancePerKFt = getResistancePerKFt(input.conductorType, input.wireSize, input.temperatureRating)
        val reactancePerKFt = getReactancePerKFt(input.conduitMaterial, input.wireSize)
        
        // Calculate impedance based on circuit length
        val resistance = resistancePerKFt * (input.lengthInFeet / 1000.0)
        val reactance = reactancePerKFt * (input.lengthInFeet / 1000.0)
        
        // Calculate total impedance (for AC circuits with power factor)
        val impedance = when (input.systemType) {
            SystemType.DC -> resistance // DC has no reactance
            else -> {
                // For AC, use impedance formula with power factor
                val pf = input.powerFactor
                val pfAngle = Math.acos(pf)
                val resistiveComponent = resistance * pf
                val reactiveComponent = reactance * Math.sin(pfAngle)
                sqrt(resistiveComponent.pow(2) + reactiveComponent.pow(2))
            }
        }
        
        // Apply multiplier based on system type
        val multiplier = when (input.systemType) {
            SystemType.SINGLE_PHASE -> 2.0      // Round trip for single-phase
            SystemType.THREE_PHASE -> sqrt(3.0) // Factor for three-phase
            SystemType.DC -> 2.0                // Round trip for DC
        }
        
        // Calculate voltage drop
        val voltageDropInVolts = input.loadInAmps * impedance * multiplier
        
        // Calculate percentage voltage drop
        val voltageDropPercentage = (voltageDropInVolts / input.voltageInVolts) * 100.0
        
        // Determine if within recommended limits
        // NEC recommends 3% for branch circuits, 5% for feeders
        val recommendedLimit = 3.0 // Using 3% as default (more conservative)
        val isWithinRecommendedLimits = voltageDropPercentage <= recommendedLimit
        
        // Calculate end voltage
        val endVoltage = input.voltageInVolts - voltageDropInVolts
        
        return VoltageDropResult(
            voltageDropInVolts = voltageDropInVolts,
            voltageDropPercentage = voltageDropPercentage,
            conductorResistance = resistancePerKFt,
            conductorReactance = reactancePerKFt,
            impedance = impedance * multiplier,
            isWithinRecommendedLimits = isWithinRecommendedLimits,
            recommendedLimit = recommendedLimit,
            endVoltage = endVoltage
        )
    }
    
    /**
     * Get resistance per 1000 feet based on conductor type, size, and temperature rating
     * Values based on NEC Chapter 9, Table 8
     * @param conductorType The type of conductor (copper or aluminum)
     * @param wireSize The size of the wire (AWG or kcmil)
     * @param temperatureRating The temperature rating of the conductor
     * @return The resistance in ohms per 1000 feet
     */
    private fun getResistancePerKFt(
        conductorType: ConductorType,
        wireSize: String,
        temperatureRating: TemperatureRating
    ): Double {
        // Base resistance values at 75°C (common reference)
        val baseResistance = when (conductorType) {
            ConductorType.COPPER -> when (wireSize) {
                "14" -> 3.07
                "12" -> 1.93
                "10" -> 1.21
                "8" -> 0.764
                "6" -> 0.491
                "4" -> 0.308
                "3" -> 0.245
                "2" -> 0.194
                "1" -> 0.154
                "1/0" -> 0.122
                "2/0" -> 0.0967
                "3/0" -> 0.0766
                "4/0" -> 0.0608
                "250" -> 0.0515
                "300" -> 0.0429
                "350" -> 0.0367
                "400" -> 0.0321
                "500" -> 0.0258
                "600" -> 0.0214
                "750" -> 0.0171
                "1000" -> 0.0129
                else -> throw IllegalArgumentException("Unsupported wire size: $wireSize")
            }
            ConductorType.ALUMINUM -> when (wireSize) {
                "12" -> 3.18
                "10" -> 1.99
                "8" -> 1.26
                "6" -> 0.808
                "4" -> 0.508
                "3" -> 0.403
                "2" -> 0.319
                "1" -> 0.253
                "1/0" -> 0.201
                "2/0" -> 0.159
                "3/0" -> 0.126
                "4/0" -> 0.100
                "250" -> 0.0847
                "300" -> 0.0707
                "350" -> 0.0605
                "400" -> 0.0529
                "500" -> 0.0424
                "600" -> 0.0353
                "750" -> 0.0282
                "1000" -> 0.0212
                else -> throw IllegalArgumentException("Unsupported wire size for aluminum: $wireSize")
            }
        }
        
        // Apply temperature correction factor
        return when (temperatureRating) {
            TemperatureRating.RATING_60C -> baseResistance * 0.95 // Slightly lower resistance at lower temp
            TemperatureRating.RATING_75C -> baseResistance        // Base values are at 75°C
            TemperatureRating.RATING_90C -> baseResistance * 1.05 // Slightly higher resistance at higher temp
        }
    }
    
    /**
     * Get reactance per 1000 feet based on conduit material and wire size
     * Values based on NEC Chapter 9, Table 9
     * @param conduitMaterial The type of conduit/raceway
     * @param wireSize The size of the wire (AWG or kcmil)
     * @return The reactance in ohms per 1000 feet
     */
    private fun getReactancePerKFt(conduitMaterial: ConduitMaterial, wireSize: String): Double {
        // Base reactance values for PVC conduit
        val baseReactance = when (wireSize) {
            "14" -> 0.058
            "12" -> 0.054
            "10" -> 0.050
            "8" -> 0.052
            "6" -> 0.051
            "4" -> 0.048
            "3" -> 0.047
            "2" -> 0.045
            "1" -> 0.046
            "1/0" -> 0.044
            "2/0" -> 0.043
            "3/0" -> 0.042
            "4/0" -> 0.041
            "250" -> 0.041
            "300" -> 0.041
            "350" -> 0.040
            "400" -> 0.040
            "500" -> 0.039
            "600" -> 0.039
            "750" -> 0.038
            "1000" -> 0.037
            else -> 0.045 // Default value if size not found
        }
        
        // Apply conduit material correction factor
        return when (conduitMaterial) {
            ConduitMaterial.PVC -> baseReactance
            ConduitMaterial.STEEL -> baseReactance * 1.2       // Steel has higher reactance
            ConduitMaterial.ALUMINUM -> baseReactance * 1.1    // Aluminum has slightly higher reactance
            ConduitMaterial.DIRECT_BURIAL -> baseReactance * 0.9 // Direct burial has lower reactance
        }
    }
}

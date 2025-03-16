package com.example.electricalcalculator.domain.usecase.boxfill

import com.example.electricalcalculator.domain.model.boxfill.BoxComponent
import com.example.electricalcalculator.domain.model.boxfill.BoxFillInput
import com.example.electricalcalculator.domain.model.boxfill.BoxFillResult
import com.example.electricalcalculator.domain.model.boxfill.ComponentDetail
import com.example.electricalcalculator.domain.model.boxfill.ComponentType
import javax.inject.Inject

/**
 * Use case for calculating box fill based on NEC standards
 */
class CalculateBoxFillUseCase @Inject constructor() {

    /**
     * Calculate box fill based on input parameters
     * @param input The box fill calculation input parameters
     * @return The box fill calculation result
     */
    operator fun invoke(input: BoxFillInput): BoxFillResult {
        // Group components by type and wire size
        val componentDetails = mutableListOf<ComponentDetail>()
        
        // Process conductors by size
        input.components
            .filter { it.type == ComponentType.CONDUCTOR }
            .groupBy { it.wireSize }
            .forEach { (wireSize, components) ->
                val totalQuantity = components.sumOf { it.quantity }
                val volumePerUnit = getVolumeForWireSize(wireSize)
                val totalVolume = volumePerUnit * totalQuantity
                
                componentDetails.add(
                    ComponentDetail(
                        type = ComponentType.CONDUCTOR,
                        description = "$wireSize AWG Conductor",
                        quantity = totalQuantity,
                        volumePerUnitInCubicInches = volumePerUnit,
                        totalVolumeInCubicInches = totalVolume
                    )
                )
            }
        
        // Process devices
        val devices = input.components.filter { it.type == ComponentType.DEVICE }
        if (devices.isNotEmpty()) {
            // Per NEC, count double volume for one device and single volume for additional devices
            val largestWireSize = findLargestWireSize(input.components)
            val volumePerUnit = getVolumeForWireSize(largestWireSize)
            
            // First device counts as double volume
            val firstDeviceVolume = volumePerUnit * 2
            // Additional devices count as single volume
            val additionalDevicesVolume = volumePerUnit * (devices.sumOf { it.quantity } - 1).coerceAtLeast(0)
            val totalVolume = firstDeviceVolume + additionalDevicesVolume
            
            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.DEVICE,
                    description = "Device(s)",
                    quantity = devices.sumOf { it.quantity },
                    volumePerUnitInCubicInches = volumePerUnit, // This is simplified
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }
        
        // Process clamps
        val clamps = input.components.filter { it.type == ComponentType.CLAMP }
        if (clamps.isNotEmpty()) {
            val largestWireSize = findLargestWireSize(input.components)
            val volumePerUnit = getVolumeForWireSize(largestWireSize)
            val totalVolume = volumePerUnit // All clamps together count as one conductor volume
            
            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.CLAMP,
                    description = "Cable Clamp(s)",
                    quantity = clamps.sumOf { it.quantity },
                    volumePerUnitInCubicInches = volumePerUnit / clamps.sumOf { it.quantity }, // Divided for display
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }
        
        // Process support fittings
        val supportFittings = input.components.filter { it.type == ComponentType.SUPPORT_FITTING }
        if (supportFittings.isNotEmpty()) {
            val largestWireSize = findLargestWireSize(input.components)
            val volumePerUnit = getVolumeForWireSize(largestWireSize) * 2 // Double volume
            val totalVolume = volumePerUnit // All support fittings together count as double conductor volume
            
            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.SUPPORT_FITTING,
                    description = "Support Fitting(s)",
                    quantity = supportFittings.sumOf { it.quantity },
                    volumePerUnitInCubicInches = volumePerUnit / supportFittings.sumOf { it.quantity }, // Divided for display
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }
        
        // Process equipment grounding conductors
        val groundingConductors = input.components.filter { it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR }
        if (groundingConductors.isNotEmpty()) {
            val largestWireSize = findLargestWireSize(input.components)
            val volumePerUnit = getVolumeForWireSize(largestWireSize)
            val totalVolume = volumePerUnit // All grounding conductors together count as one conductor volume
            
            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR,
                    description = "Equipment Grounding Conductor(s)",
                    quantity = groundingConductors.sumOf { it.quantity },
                    volumePerUnitInCubicInches = volumePerUnit / groundingConductors.sumOf { it.quantity }, // Divided for display
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }
        
        // Calculate total required volume
        val totalRequiredVolume = componentDetails.sumOf { it.totalVolumeInCubicInches }
        
        // Calculate remaining volume
        val remainingVolume = input.boxVolumeInCubicInches - totalRequiredVolume
        
        // Calculate fill percentage
        val fillPercentage = (totalRequiredVolume / input.boxVolumeInCubicInches) * 100
        
        // Check if within limits
        val isWithinLimits = totalRequiredVolume <= input.boxVolumeInCubicInches
        
        return BoxFillResult(
            boxType = input.boxType,
            boxDimensions = input.boxDimensions,
            boxVolumeInCubicInches = input.boxVolumeInCubicInches,
            totalRequiredVolumeInCubicInches = totalRequiredVolume,
            remainingVolumeInCubicInches = remainingVolume,
            fillPercentage = fillPercentage,
            isWithinLimits = isWithinLimits,
            componentDetails = componentDetails
        )
    }
    
    /**
     * Get volume requirement for a wire size based on NEC Table 314.16(B)
     * @param wireSize The wire size in AWG
     * @return The volume requirement in cubic inches
     */
    private fun getVolumeForWireSize(wireSize: String): Double {
        return when (wireSize) {
            "18" -> 1.5
            "16" -> 1.75
            "14" -> 2.0
            "12" -> 2.25
            "10" -> 2.5
            "8" -> 3.0
            "6" -> 5.0
            "4" -> 6.0
            "3" -> 7.0
            "2" -> 8.0
            "1" -> 9.0
            "1/0" -> 10.0
            "2/0" -> 11.0
            "3/0" -> 12.0
            "4/0" -> 13.0
            else -> throw IllegalArgumentException("Unsupported wire size: $wireSize")
        }
    }
    
    /**
     * Find the largest wire size among components
     * @param components List of box components
     * @return The largest wire size as a string
     */
    private fun findLargestWireSize(components: List<BoxComponent>): String {
        val conductors = components.filter { it.type == ComponentType.CONDUCTOR }
        if (conductors.isEmpty()) {
            return "14" // Default to 14 AWG if no conductors
        }
        
        // Map of wire sizes to their relative sizes (larger number = smaller wire)
        val wireSizeMap = mapOf(
            "18" to 18, "16" to 16, "14" to 14, "12" to 12, "10" to 10, "8" to 8,
            "6" to 6, "4" to 4, "3" to 3, "2" to 2, "1" to 1,
            "1/0" to 0, "2/0" to -1, "3/0" to -2, "4/0" to -3
        )
        
        // Find the smallest number in the map (largest wire)
        return conductors
            .map { it.wireSize }
            .minByOrNull { wireSizeMap[it] ?: Int.MAX_VALUE }
            ?: "14" // Default to 14 AWG if mapping fails
    }
}

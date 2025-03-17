package com.example.electricalcalculator.domain.usecase

import com.example.electricalcalculator.domain.model.BoxFill
import com.example.electricalcalculator.domain.model.BoxFillResult
import com.example.electricalcalculator.domain.model.Wire
import com.example.electricalcalculator.domain.model.WireVolumeDetail
import kotlin.math.max

/**
 * Use case for calculating box fill requirements according to NEC standards.
 */
class CalculateBoxFillUseCase {
    
    /**
     * Execute the box fill calculation.
     * 
     * @param boxFill The box fill data to calculate
     * @return BoxFillResult containing calculation results
     */
    operator fun invoke(boxFill: BoxFill): BoxFillResult {
        val wireDetails = mutableListOf<WireVolumeDetail>()
        var totalVolumeUsed = 0.0
        
        // Group wires by size
        val wiresBySize = boxFill.wires.groupBy { it.size }
        
        // Calculate volume per wire size
        wiresBySize.forEach { (size, wires) ->
            // Get the volume allowance for this wire size
            val volumePerWire = getVolumeAllowanceForWire(size)
            
            // Count the wires
            val count = wires.sumOf { it.quantity }
            
            // Calculate the total volume for this wire size
            val totalVolume = volumePerWire * count
            
            // Add to the total
            totalVolumeUsed += totalVolume
            
            // Add detail for this wire size
            wireDetails.add(
                WireVolumeDetail(
                    wireType = wires.firstOrNull()?.type ?: "Unknown",
                    wireSize = size,
                    wireCount = count,
                    volumePerWire = volumePerWire,
                    totalVolume = totalVolume
                )
            )
        }
        
        // Add device volumes (2x largest wire volume for each device)
        if (boxFill.deviceCount > 0) {
            val largestWireVolume = wireDetails.maxOfOrNull { it.volumePerWire } ?: 0.0
            val deviceVolume = largestWireVolume * 2 * boxFill.deviceCount
            totalVolumeUsed += deviceVolume
            
            wireDetails.add(
                WireVolumeDetail(
                    wireType = "Device",
                    wireSize = "N/A",
                    wireCount = boxFill.deviceCount,
                    volumePerWire = largestWireVolume * 2,
                    totalVolume = deviceVolume
                )
            )
        }
        
        // Add clamp volumes (if any)
        if (boxFill.clampCount > 0) {
            val largestWireVolume = wireDetails.maxOfOrNull { it.volumePerWire } ?: 0.0
            val clampVolume = largestWireVolume * boxFill.clampCount
            totalVolumeUsed += clampVolume
            
            wireDetails.add(
                WireVolumeDetail(
                    wireType = "Clamp",
                    wireSize = "N/A",
                    wireCount = boxFill.clampCount,
                    volumePerWire = largestWireVolume,
                    totalVolume = clampVolume
                )
            )
        }
        
        // Add support fitting volumes (if any)
        if (boxFill.supportFittingsCount > 0) {
            val largestWireVolume = wireDetails.maxOfOrNull { it.volumePerWire } ?: 0.0
            val supportVolume = largestWireVolume * 2 * boxFill.supportFittingsCount
            totalVolumeUsed += supportVolume
            
            wireDetails.add(
                WireVolumeDetail(
                    wireType = "Support Fitting",
                    wireSize = "N/A",
                    wireCount = boxFill.supportFittingsCount,
                    volumePerWire = largestWireVolume * 2,
                    totalVolume = supportVolume
                )
            )
        }
        
        // Add equipment grounding conductor volumes (if any)
        if (boxFill.equipmentGroundingCount > 0) {
            val largestWireVolume = wireDetails.maxOfOrNull { it.volumePerWire } ?: 0.0
            val groundingVolume = largestWireVolume * boxFill.equipmentGroundingCount
            totalVolumeUsed += groundingVolume
            
            wireDetails.add(
                WireVolumeDetail(
                    wireType = "Equipment Grounding",
                    wireSize = "N/A",
                    wireCount = boxFill.equipmentGroundingCount,
                    volumePerWire = largestWireVolume,
                    totalVolume = groundingVolume
                )
            )
        }
        
        // Check if the total volume is acceptable
        val isAcceptable = totalVolumeUsed <= boxFill.boxVolume
        val remainingVolume = max(0.0, boxFill.boxVolume - totalVolumeUsed)
        
        return BoxFillResult(
            totalVolumeUsed = totalVolumeUsed,
            boxVolume = boxFill.boxVolume,
            isAcceptable = isAcceptable,
            remainingVolume = remainingVolume,
            wireDetails = wireDetails
        )
    }
    
    /**
     * Get the volume allowance for a wire based on its size (as per NEC).
     */
    private fun getVolumeAllowanceForWire(wireSize: String): Double {
        return when (wireSize) {
            "14 AWG" -> 2.0
            "12 AWG" -> 2.25
            "10 AWG" -> 2.5
            "8 AWG" -> 3.0
            "6 AWG" -> 5.0
            "4 AWG" -> 10.0
            "3 AWG" -> 10.0
            "2 AWG" -> 12.5
            "1 AWG" -> 15.5
            "1/0 AWG" -> 18.0
            "2/0 AWG" -> 20.0
            "3/0 AWG" -> 22.0
            "4/0 AWG" -> 25.0
            else -> 2.0 // Default to 14 AWG if unknown
        }
    }
}

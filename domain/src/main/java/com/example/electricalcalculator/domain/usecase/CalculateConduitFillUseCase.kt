package com.example.electricalcalculator.domain.usecase

import com.example.electricalcalculator.domain.model.ConduitFill
import com.example.electricalcalculator.domain.model.ConduitFillResult
import com.example.electricalcalculator.domain.model.WireAreaDetail
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow

/**
 * Use case for calculating conduit fill requirements according to NEC standards.
 */
class CalculateConduitFillUseCase {
    
    /**
     * Execute the conduit fill calculation.
     * 
     * @param conduitFill The conduit fill data to calculate
     * @return ConduitFillResult containing calculation results
     */
    operator fun invoke(conduitFill: ConduitFill): ConduitFillResult {
        val wireDetails = mutableListOf<WireAreaDetail>()
        var totalAreaUsed = 0.0
        
        // Group wires by size and type
        val wiresByTypeAndSize = conduitFill.wires.groupBy { 
            "${it.type}:${it.size}" 
        }
        
        // Calculate area per wire type and size
        wiresByTypeAndSize.forEach { (key, wires) ->
            // Split key to get type and size
            val parts = key.split(":")
            val wireType = parts[0]
            val wireSize = parts[1]
            
            // Count the wires
            val count = wires.sumOf { it.quantity }
            
            // Calculate area for each wire (πr²)
            val wireRadius = wires.first().diameter / 2
            val areaPerWire = PI * wireRadius.pow(2)
            
            // Calculate the total area for these wires
            val totalArea = areaPerWire * count
            
            // Add to the total
            totalAreaUsed += totalArea
            
            // Add detail for this wire type and size
            wireDetails.add(
                WireAreaDetail(
                    wireType = wireType,
                    wireSize = wireSize,
                    wireCount = count,
                    areaPerWire = areaPerWire,
                    totalArea = totalArea
                )
            )
        }
        
        // Get the conduit's cross-sectional area
        val conduitArea = conduitFill.conduit.area
        
        // Calculate percentage filled
        val percentFilled = (totalAreaUsed / conduitArea) * 100
        
        // Check if the fill percentage is acceptable
        val isAcceptable = percentFilled <= conduitFill.fillPercentage
        val remainingArea = max(0.0, conduitArea - totalAreaUsed)
        
        return ConduitFillResult(
            totalAreaUsed = totalAreaUsed,
            conduitArea = conduitArea,
            percentFilled = percentFilled,
            maxAllowedFill = conduitFill.fillPercentage,
            isAcceptable = isAcceptable,
            remainingArea = remainingArea,
            wireDetails = wireDetails
        )
    }
    
    /**
     * Helper method to determine maximum fill percentage based on NEC standards.
     * 
     * According to NEC Table 1, Chapter 9:
     * - 1 conductor: 53% fill
     * - 2 conductors: 31% fill
     * - 3+ conductors: 40% fill
     */
    fun calculateMaxFillPercentage(conductorCount: Int): Double {
        return when {
            conductorCount == 1 -> 53.0
            conductorCount == 2 -> 31.0
            else -> 40.0
        }
    }
}

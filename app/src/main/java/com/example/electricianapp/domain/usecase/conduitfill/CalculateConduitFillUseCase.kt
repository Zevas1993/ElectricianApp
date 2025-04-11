package com.example.electricianapp.domain.usecase.conduitfill // Corrected package

import com.example.electricianapp.domain.model.conduitfill.ConduitFillInput // Corrected import
import com.example.electricianapp.domain.model.conduitfill.ConduitFillResult // Corrected import
import com.example.electricianapp.domain.model.conduitfill.ConduitType // Corrected import
import com.example.electricianapp.domain.model.conduitfill.WireDetail // Corrected import
import javax.inject.Inject

/**
 * Use case for calculating conduit fill based on NEC standards
 */
class CalculateConduitFillUseCase @Inject constructor() {

    /**
     * Calculate conduit fill based on input parameters
     * @param input The conduit fill calculation input parameters
     * @return The conduit fill calculation result
     */
    operator fun invoke(input: ConduitFillInput): ConduitFillResult {
        // Get conduit area based on type and size
        val conduitAreaInSqInches = getConduitArea(input.conduitType, input.conduitSize)
        
        // Calculate total wire area
        val wireDetails = input.wires.groupBy { "${it.type}-${it.size}" }
            .map { (_, wires) ->
                val wire = wires.first() // Get a representative wire for type, size, area
                val totalQuantity = wires.sumOf { it.quantity }
                val areaPerWire = wire.areaPerWireInSqInches // Use the correct property name from input
                val totalArea = areaPerWire * totalQuantity
                WireDetail(
                    type = wire.type,
                    size = wire.size,
                    quantity = totalQuantity,
                    areaPerWireInSqInches = areaPerWire, // Pass the correct value
                    totalAreaInSqInches = totalArea // Pass the calculated total area
                )
            }
        
        val totalWireAreaInSqInches = wireDetails.sumOf { it.totalAreaInSqInches } // Use the correct property name
        
        // Calculate fill percentage
        val fillPercentage = (totalWireAreaInSqInches / conduitAreaInSqInches) * 100
        
        // Determine maximum allowed fill percentage based on NEC
        val maximumAllowedFillPercentage = getMaximumAllowedFillPercentage(input.wires.size)
        
        // Check if within limits
        val isWithinLimits = fillPercentage <= maximumAllowedFillPercentage
        
        return ConduitFillResult(
            conduitType = input.conduitType,
            conduitSize = input.conduitSize,
            conduitAreaInSqInches = conduitAreaInSqInches,
            totalWireAreaInSqInches = totalWireAreaInSqInches,
            fillPercentage = fillPercentage,
            maximumAllowedFillPercentage = maximumAllowedFillPercentage,
            isWithinLimits = isWithinLimits,
            wireDetails = wireDetails
        )
    }
    
    /**
     * Get conduit area in square inches based on type and size
     * @param conduitType The type of conduit
     * @param conduitSize The trade size of the conduit
     * @return The internal cross-sectional area in square inches
     */
    private fun getConduitArea(conduitType: ConduitType, conduitSize: String): Double {
        // Areas based on NEC Chapter 9, Table 4
        return when (conduitType) {
            ConduitType.EMT -> when (conduitSize) {
                "1/2" -> 0.304
                "3/4" -> 0.533
                "1" -> 0.864
                "1-1/4" -> 1.496
                "1-1/2" -> 2.036
                "2" -> 3.356
                "2-1/2" -> 5.858
                "3" -> 8.846
                "3-1/2" -> 11.545
                "4" -> 14.753
                else -> throw IllegalArgumentException("Unsupported EMT conduit size: $conduitSize")
            }
            ConduitType.IMC -> when (conduitSize) {
                "1/2" -> 0.285
                "3/4" -> 0.508
                "1" -> 0.814
                "1-1/4" -> 1.453
                "1-1/2" -> 1.986
                "2" -> 3.291
                "2-1/2" -> 5.610
                "3" -> 8.477
                "3-1/2" -> 11.258
                "4" -> 14.309
                else -> throw IllegalArgumentException("Unsupported IMC conduit size: $conduitSize")
            }
            ConduitType.RMC -> when (conduitSize) {
                "1/2" -> 0.249
                "3/4" -> 0.445
                "1" -> 0.788
                "1-1/4" -> 1.405
                "1-1/2" -> 1.828
                "2" -> 3.269
                "2-1/2" -> 5.172
                "3" -> 8.085
                "3-1/2" -> 10.684
                "4" -> 13.631
                else -> throw IllegalArgumentException("Unsupported RMC conduit size: $conduitSize")
            }
            // Updated PVC cases for Schedule 40 and 80
            ConduitType.PVC_SCHEDULE_40 -> when (conduitSize) {
                "1/2" -> 0.314 // NEC Ch9, T4 - PVC Sched 40
                "3/4" -> 0.555
                "1" -> 0.907
                "1-1/4" -> 1.583
                "1-1/2" -> 2.147
                "2" -> 3.538
                "2-1/2" -> 4.990 // Note: Values might differ slightly based on specific NEC edition
                "3" -> 7.698
                "3-1/2" -> 10.227
                "4" -> 13.146
                else -> throw IllegalArgumentException("Unsupported PVC Schedule 40 conduit size: $conduitSize")
            }
            ConduitType.PVC_SCHEDULE_80 -> when (conduitSize) {
                "1/2" -> 0.235 // NEC Ch9, T4 - PVC Sched 80
                "3/4" -> 0.433
                "1" -> 0.710
                "1-1/4" -> 1.282
                "1-1/2" -> 1.738
                "2" -> 2.907
                "2-1/2" -> 4.236
                "3" -> 6.677
                "3-1/2" -> 8.833
                "4" -> 11.596
                else -> throw IllegalArgumentException("Unsupported PVC Schedule 80 conduit size: $conduitSize")
            }
            // Removed duplicated/incorrect ENT block
            ConduitType.ENT -> when (conduitSize) {
                "1/2" -> 0.285
                "3/4" -> 0.508
                "1" -> 0.814
                "1-1/4" -> 1.453
                "1-1/2" -> 1.986
                "2" -> 3.291
                else -> throw IllegalArgumentException("Unsupported ENT conduit size: $conduitSize")
            }
        }
    }
    
    /**
     * Get maximum allowed fill percentage based on NEC
     * @param numberOfConductors The number of conductors in the conduit
     * @return The maximum allowed fill percentage
     */
    private fun getMaximumAllowedFillPercentage(numberOfConductors: Int): Double {
        return when {
            numberOfConductors == 1 -> 53.0  // One conductor: 53% fill
            numberOfConductors == 2 -> 31.0  // Two conductors: 31% fill
            numberOfConductors >= 3 -> 40.0  // Three or more conductors: 40% fill
            else -> 40.0  // Default to 40% if no conductors (shouldn't happen)
        }
    }
}

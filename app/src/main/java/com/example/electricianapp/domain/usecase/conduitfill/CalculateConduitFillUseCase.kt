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
        // Group wires by type and size to correctly sum quantities and areas
        val wireDetails = input.wires.groupBy { "${it.type}-${it.size}" }
            .map { (_, wires) ->
                val wire = wires.first() // Get a representative wire for type/size/area
                val totalQuantity = wires.sumOf { it.quantity }
                WireDetail(
                    type = wire.type,
                    size = wire.size,
                    quantity = totalQuantity,
                    areaPerWireInSqInches = wire.areaInSqInches, // Use area from the model
                    totalAreaInSqInches = wire.areaInSqInches * totalQuantity
                )
            }

        val totalWireAreaInSqInches = wireDetails.sumOf { it.totalAreaInSqInches }

        // Calculate fill percentage
        val fillPercentage = if (conduitAreaInSqInches > 0) {
            (totalWireAreaInSqInches / conduitAreaInSqInches) * 100
        } else {
            0.0 // Avoid division by zero if conduit area is invalid
        }


        // Determine maximum allowed fill percentage based on NEC Chapter 9, Table 1
        val totalNumberOfConductors = input.wires.sumOf { it.quantity }
        val maximumAllowedFillPercentage = getMaximumAllowedFillPercentage(totalNumberOfConductors)

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
    // TODO: Expand this table with more conduit types and sizes from NEC Chapter 9, Table 4
    private fun getConduitArea(conduitType: ConduitType, conduitSize: String): Double {
        // Areas based on NEC Chapter 9, Table 4 (Total Area)
        return when (conduitType) {
            ConduitType.EMT -> when (conduitSize) {
                "1/2" -> 0.304
                "3/4" -> 0.533
                "1" -> 0.864
                "1-1/4" -> 1.496
                "1-1/2" -> 2.036
                "2" -> 3.356
                "2-1/2" -> 4.788 // Corrected based on common tables
                "3" -> 7.383 // Corrected based on common tables
                "3-1/2" -> 9.900 // Corrected based on common tables
                "4" -> 12.720 // Corrected based on common tables
                else -> throw IllegalArgumentException("Unsupported EMT conduit size: $conduitSize")
            }
            ConduitType.IMC -> when (conduitSize) {
                 "1/2" -> 0.368 // Example values, verify with NEC Table 4
                 "3/4" -> 0.616
                 "1" -> 0.983
                 "1-1/4" -> 1.698
                 "1-1/2" -> 2.290
                 "2" -> 3.716
                 "2-1/2" -> 5.980 // Example
                 "3" -> 8.898 // Example
                 "3-1/2" -> 11.77 // Example
                 "4" -> 14.79 // Example
                else -> throw IllegalArgumentException("Unsupported IMC conduit size: $conduitSize")
            }
            ConduitType.RMC -> when (conduitSize) {
                 "1/2" -> 0.314 // Example values, verify with NEC Table 4
                 "3/4" -> 0.533
                 "1" -> 0.861
                 "1-1/4" -> 1.504
                 "1-1/2" -> 2.048
                 "2" -> 3.355
                 "2-1/2" -> 4.788 // Example
                 "3" -> 7.383 // Example
                 "3-1/2" -> 9.898 // Example
                 "4" -> 12.718 // Example
                else -> throw IllegalArgumentException("Unsupported RMC conduit size: $conduitSize")
            }
             // Add cases for PVC_SCHEDULE_40, PVC_SCHEDULE_80, ENT based on NEC Table 4
             ConduitType.PVC_SCHEDULE_40 -> when (conduitSize) {
                 "1/2" -> 0.285 // Example
                 "3/4" -> 0.508 // Example
                 // ... add other sizes
                 else -> throw IllegalArgumentException("Unsupported PVC Sched 40 size: $conduitSize")
             }
             ConduitType.PVC_SCHEDULE_80 -> when (conduitSize) {
                 "1/2" -> 0.235 // Example
                 "3/4" -> 0.433 // Example
                 // ... add other sizes
                 else -> throw IllegalArgumentException("Unsupported PVC Sched 80 size: $conduitSize")
             }
             ConduitType.ENT -> when (conduitSize) {
                 "1/2" -> 0.285 // Example
                 "3/4" -> 0.508 // Example
                  // ... add other sizes
                  else -> throw IllegalArgumentException("Unsupported ENT size: $conduitSize")
              }
            // Add an else branch to handle any other ConduitType values if they exist
            // or if new ones are added later without updating this function.
            // Returning 0.0 or throwing an exception are options.
            else -> throw IllegalArgumentException("Unsupported ConduitType: $conduitType")
        }
    }

    /**
     * Get maximum allowed fill percentage based on NEC Chapter 9, Table 1
     * @param numberOfConductors The total number of conductors in the conduit
     * @return The maximum allowed fill percentage
     */
    private fun getMaximumAllowedFillPercentage(numberOfConductors: Int): Double {
        return when {
            numberOfConductors <= 0 -> 0.0  // No conductors, 0% fill allowed conceptually
            numberOfConductors == 1 -> 53.0   // Over 2 wires note 1: "Use 53 percent for 1 conductor"
            numberOfConductors == 2 -> 31.0   // Over 2 wires note 2: "Use 31 percent for 2 conductors"
            else -> 40.0 // Covers >= 3 conductors (NEC Chapter 9, Table 1) and ensures exhaustiveness
        }
    }
}

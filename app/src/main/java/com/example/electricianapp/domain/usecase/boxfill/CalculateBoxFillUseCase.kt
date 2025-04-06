package com.example.electricianapp.domain.usecase.boxfill // Corrected package

import com.example.electricianapp.domain.model.boxfill.BoxComponent // Corrected import
import com.example.electricianapp.domain.model.boxfill.BoxFillInput // Corrected import
import com.example.electricianapp.domain.model.boxfill.BoxFillResult // Corrected import
import com.example.electricianapp.domain.model.boxfill.ComponentDetail // Corrected import
import com.example.electricianapp.domain.model.boxfill.ComponentType // Corrected import
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
                // Provide a default wire size if null, although wireSize should ideally not be null for CONDUCTOR type
                val volumePerUnit = getVolumeForWireSize(wireSize ?: "14") // Default to "14" if null
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
            // Per NEC 314.16(B)(4), count double volume allowance for each yoke or strap containing one or more devices
            val largestWireSizeConnectedToDevice = findLargestWireSize(devices) // Find largest conductor connected *to these devices*
            val volumePerDeviceAllowance = getVolumeForWireSize(largestWireSizeConnectedToDevice) * 2
            val totalDeviceQuantity = devices.sumOf { it.quantity } // Assuming quantity on BoxComponent for device means number of yokes
            val totalVolume = volumePerDeviceAllowance * totalDeviceQuantity

            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.DEVICE,
                    description = "Device(s) / Yoke(s)",
                    quantity = totalDeviceQuantity,
                    volumePerUnitInCubicInches = volumePerDeviceAllowance, // Volume allowance per yoke
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }

        // Process clamps
        val clamps = input.components.filter { it.type == ComponentType.CLAMP }
        if (clamps.isNotEmpty()) {
            // Per NEC 314.16(B)(2), a single volume allowance based on the largest conductor *in the box*
            val largestWireSizeInBox = findLargestWireSize(input.components) // Largest conductor overall
            val totalVolume = getVolumeForWireSize(largestWireSizeInBox) // Single allowance for all internal clamps

            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.CLAMP,
                    description = "Internal Cable Clamp(s)",
                    quantity = clamps.sumOf { it.quantity }, // Informational quantity
                    volumePerUnitInCubicInches = 0.0, // Volume is a single allowance, not per clamp
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }

        // Process support fittings
        val supportFittings = input.components.filter { it.type == ComponentType.SUPPORT_FITTING }
        if (supportFittings.isNotEmpty()) {
            // Per NEC 314.16(B)(3), each fitting (like fixture studs or hickeys) counts as a single volume allowance based on largest conductor *in the box*
            val largestWireSizeInBox = findLargestWireSize(input.components)
            val volumePerFitting = getVolumeForWireSize(largestWireSizeInBox)
            val totalQuantity = supportFittings.sumOf { it.quantity }
            val totalVolume = volumePerFitting * totalQuantity

            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.SUPPORT_FITTING,
                    description = "Support Fitting(s)",
                    quantity = totalQuantity,
                    volumePerUnitInCubicInches = volumePerFitting,
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }

        // Process equipment grounding conductors
        val groundingConductors = input.components.filter { it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR }
        if (groundingConductors.isNotEmpty()) {
            // Per NEC 314.16(B)(5), a single volume allowance for all grounding conductors based on the largest grounding conductor *present*
            val largestGroundingWireSize = findLargestWireSize(groundingConductors)
            val totalVolume = getVolumeForWireSize(largestGroundingWireSize) // Single allowance for all grounds

            componentDetails.add(
                ComponentDetail(
                    type = ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR,
                    description = "Equipment Grounding Conductor(s)",
                    quantity = groundingConductors.sumOf { it.quantity }, // Informational quantity
                    volumePerUnitInCubicInches = 0.0, // Volume is a single allowance
                    totalVolumeInCubicInches = totalVolume
                )
            )
        }

        // Calculate total required volume
        val totalRequiredVolume = componentDetails.sumOf { it.totalVolumeInCubicInches }

        // Calculate remaining volume
        val remainingVolume = input.boxVolumeInCubicInches - totalRequiredVolume

        // Calculate fill percentage
        val fillPercentage = if (input.boxVolumeInCubicInches > 0) {
             (totalRequiredVolume / input.boxVolumeInCubicInches) * 100
        } else {
            Double.POSITIVE_INFINITY // Or handle as error if box volume is zero/invalid
        }


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
     * @param wireSize The wire size in AWG (e.g., "14", "12", "1/0")
     * @return The volume requirement in cubic inches
     */
    private fun getVolumeForWireSize(wireSize: String): Double {
        return when (wireSize) {
            "18" -> 1.50
            "16" -> 1.75
            "14" -> 2.00
            "12" -> 2.25
            "10" -> 2.50
            "8" -> 3.00
            "6" -> 5.00
            // Note: NEC 314.16(B) stops at 6 AWG. Larger conductors aren't typically used in standard box fill calcs this way.
            // If needed for specific scenarios, consult relevant NEC sections.
            else -> throw IllegalArgumentException("Unsupported or invalid wire size for standard box fill calculation: $wireSize")
        }
    }

    /**
     * Find the largest wire size (smallest AWG number) among a list of components.
     * @param components List of box components to check.
     * @return The largest wire size as a string (e.g., "12", "10", "8"). Defaults if no conductors found.
     */
    private fun findLargestWireSize(components: List<BoxComponent>): String {
        // Filter for components that represent conductors and have a wire size
        val conductorSizes = components
            .filter { it.type == ComponentType.CONDUCTOR || it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR }
            .mapNotNull { it.wireSize } // Get non-null wire sizes

        if (conductorSizes.isEmpty()) {
            // If no conductors are found in the provided list (e.g., only devices),
            // we might need a default or context from the overall input.
            // For simplicity here, default to a common small size. Adjust if needed.
            return "14"
        }

        // Map of wire sizes to their relative sizes (larger number = smaller wire, lower number = larger wire)
        // Using Double to handle potential kcmil sizes later if needed, though not in 314.16(B) table directly.
        val wireSizeOrder = mapOf(
            "18" to 18.0, "16" to 16.0, "14" to 14.0, "12" to 12.0, "10" to 10.0, "8" to 8.0, "6" to 6.0
            // Add larger sizes if necessary for other contexts, mapping them appropriately
            // "4" to 4.0, "2" to 2.0, "1" to 1.0, "1/0" to 0.0, "2/0" to -1.0, etc.
        )

        // Find the wire size string corresponding to the minimum value in the map
        return conductorSizes
            .minByOrNull { wireSizeOrder[it] ?: Double.MAX_VALUE } // Find the smallest numerical representation (largest wire)
            ?: "14" // Default if no valid sizes are found
    }
}

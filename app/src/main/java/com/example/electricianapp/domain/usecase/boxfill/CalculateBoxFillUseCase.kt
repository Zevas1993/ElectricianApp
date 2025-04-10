package com.example.electricianapp.domain.usecase.boxfill

import com.example.electricianapp.domain.model.boxfill.* // Import all box fill models including BoxType
import javax.inject.Inject
import kotlin.math.ceil // Import ceil

/**
 * Use case for calculating box fill based on NEC standards
 */
class CalculateBoxFillUseCase @Inject constructor() {

    // NEC Table 314.16(A) - Metal Box Volumes (subset)
    private val standardMetalBoxVolumes = mapOf(
        "DEVICE:4x2.125x1.5" to 10.5, "DEVICE:4x2.125x1.875" to 13.0, "DEVICE:4x2.125x2.125" to 14.5,
        "MASONRY:3.75x2x2.5" to 14.0, "MASONRY:3.75x2x3.5" to 21.0,
        "SQUARE:4x4x1.25" to 18.0, "SQUARE:4x4x1.5" to 21.0, "SQUARE:4x4x2.125" to 30.3,
        "SQUARE:4.6875x4.6875x1.5" to 32.0, "SQUARE:4.6875x4.6875x2.125" to 42.0,
        "ROUND_OCT:4x1.5" to 15.5, "ROUND_OCT:4x2.125" to 21.5
    )

    // NEC Table 314.16(B) - Conductor Volumes
    private val conductorVolumes = mapOf(
        "18" to 1.50, "16" to 1.75, "14" to 2.00,
        "12" to 2.25, "10" to 2.50, "8" to 3.00, "6" to 5.00
    )

    operator fun invoke(input: BoxFillInput): BoxFillResult {
        val actualBoxVolume = getActualBoxVolume(input.boxType, input.boxDimensions, input.boxVolumeInCubicInches)
            ?: throw IllegalArgumentException("Invalid or unknown box volume/dimensions.")

        val allConductorsAndGrounds = input.components.filter { it.type == ComponentType.CONDUCTOR || it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR }
        val largestConductorSize = findLargestWireSize(allConductorsAndGrounds, defaultIfNone = "14")
        val largestGroundingConductorSize = findLargestWireSize(
            input.components.filter { it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR },
            defaultIfNone = largestConductorSize
        )

        val componentDetails = mutableListOf<ComponentDetail>()
        var totalRequiredVolume = 0.0

        // 1. Conductors
        input.components
            .filter { it.type == ComponentType.CONDUCTOR }
            .forEach { conductor ->
                val volumePer = getVolumeForWireSize(conductor.wireSize ?: largestConductorSize)
                val volume = volumePer * conductor.quantity
                totalRequiredVolume += volume
                componentDetails.add(ComponentDetail(ComponentType.CONDUCTOR, "${conductor.wireSize ?: "?"} AWG Conductor", conductor.quantity, volumePer, volume))
            }

        // 2. Clamps
        val clampCount = input.components.count { it.type == ComponentType.CLAMP }
        if (clampCount > 0) {
            val volume = getVolumeForWireSize(largestConductorSize)
            totalRequiredVolume += volume
            componentDetails.add(ComponentDetail(ComponentType.CLAMP, "Internal Cable Clamp(s)", clampCount, 0.0, volume))
        }

        // 3. Support Fittings
        input.components
            .filter { it.type == ComponentType.SUPPORT_FITTING }
            .forEach { fitting ->
                val volumePer = getVolumeForWireSize(largestConductorSize)
                val volume = volumePer * fitting.quantity
                totalRequiredVolume += volume
                componentDetails.add(ComponentDetail(ComponentType.SUPPORT_FITTING, "Support Fitting(s) (e.g., Stud, Hickey)", fitting.quantity, volumePer, volume))
            }

        // 4. Devices
        val deviceCount = input.components.count { it.type == ComponentType.DEVICE }
        if (deviceCount > 0) {
            val volumePerDevice = getVolumeForWireSize(largestConductorSize) * 2.0 // Ensure double multiplication
            val volume = volumePerDevice * deviceCount
            totalRequiredVolume += volume
            componentDetails.add(ComponentDetail(ComponentType.DEVICE, "Device(s) / Yoke(s)", deviceCount, volumePerDevice, volume))
        }

        // 5. Equipment Grounding Conductors
        val groundingConductorCount = input.components.count { it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR }
        if (groundingConductorCount > 0) {
            val volume = getVolumeForWireSize(largestGroundingConductorSize)
            totalRequiredVolume += volume
            componentDetails.add(ComponentDetail(ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR, "Equipment Grounding Conductor(s)", groundingConductorCount, 0.0, volume))
        }

        // Final Calculations
        val remainingVolume = actualBoxVolume - totalRequiredVolume
        val fillPercentage = if (actualBoxVolume > 0.0) (totalRequiredVolume / actualBoxVolume) * 100.0 else (if (totalRequiredVolume > 0.0) Double.POSITIVE_INFINITY else 0.0)
        val isWithinLimits = totalRequiredVolume <= actualBoxVolume

        return BoxFillResult(
            boxType = input.boxType,
            boxDimensions = input.boxDimensions,
            boxVolumeInCubicInches = actualBoxVolume,
            totalRequiredVolumeInCubicInches = totalRequiredVolume,
            remainingVolumeInCubicInches = remainingVolume,
            fillPercentage = fillPercentage,
            isWithinLimits = isWithinLimits,
            componentDetails = componentDetails
        )
    }

    private fun getVolumeForWireSize(wireSize: String): Double {
        return conductorVolumes[wireSize]
            ?: throw IllegalArgumentException("Unsupported wire size for box fill: $wireSize")
    }

    private fun getActualBoxVolume(boxType: BoxType, dimensionsOrVolumeString: String, directVolumeInput: Double?): Double? {
        if (directVolumeInput != null && directVolumeInput > 0) return directVolumeInput
        val parsedVolume = dimensionsOrVolumeString.toDoubleOrNull()
        if (parsedVolume != null && parsedVolume > 0) return parsedVolume
        val key = "${boxType.name}:${dimensionsOrVolumeString.replace(" ", "")}"
        return standardMetalBoxVolumes[key]
    }

    private fun findLargestWireSize(components: List<BoxComponent>, defaultIfNone: String): String {
        val conductorSizes = components
            .filter { (it.type == ComponentType.CONDUCTOR || it.type == ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR) && it.wireSize != null }
            .mapNotNull { it.wireSize }

        if (conductorSizes.isEmpty()) return defaultIfNone

        val wireSizeOrder = mapOf(
            "18" to 18.0, "16" to 16.0, "14" to 14.0, "12" to 12.0, "10" to 10.0, "8" to 8.0, "6" to 6.0
        )

        return conductorSizes
            .minByOrNull { wireSizeOrder[it] ?: Double.MAX_VALUE } // Use 'it' directly
            ?: defaultIfNone
    }
}

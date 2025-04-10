package com.example.electricalcalculator.domain.model.boxfill

/**
 * Represents the result of a box fill calculation.
 * @param boxType The type of the box used in the calculation.
 * @param boxDimensions The dimensions of the box.
 * @param boxVolumeInCubicInches The total volume of the box.
 * @param totalRequiredVolumeInCubicInches The total calculated volume required by the components.
 * @param remainingVolumeInCubicInches The difference between box volume and required volume.
 * @param fillPercentage The percentage of the box volume used.
 * @param isWithinLimits True if the required volume is less than or equal to the box volume, false otherwise.
 * @param componentDetails A list containing the calculated details for each component type.
 */
data class BoxFillResult(
    val boxType: String,
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val totalRequiredVolumeInCubicInches: Double,
    val remainingVolumeInCubicInches: Double,
    val fillPercentage: Double,
    val isWithinLimits: Boolean,
    val componentDetails: List<ComponentDetail>
)

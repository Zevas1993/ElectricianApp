package com.example.electricalcalculator.domain.model.boxfill

/**
 * Input parameters for the box fill calculation.
 * @param boxType A description of the box type (e.g., "Metallic", "Nonmetallic").
 * @param boxDimensions A description of the box dimensions (e.g., "4x4x1.5").
 * @param boxVolumeInCubicInches The total internal volume of the box.
 * @param components A list of components inside the box.
 */
data class BoxFillInput(
    val boxType: String,
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val components: List<BoxComponent>
)

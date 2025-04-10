package com.example.electricalcalculator.domain.model.boxfill

/**
 * Represents the calculated details for a specific type of component in the box fill result.
 * @param type The type of the component.
 * @param description A user-friendly description (e.g., "14 AWG Conductor", "Device(s)").
 * @param quantity The total quantity of this component type.
 * @param volumePerUnitInCubicInches The calculated volume allowance per single unit (might be averaged or based on largest wire).
 * @param totalVolumeInCubicInches The total calculated volume allowance for all units of this type.
 */
data class ComponentDetail(
    val type: ComponentType,
    val description: String,
    val quantity: Int,
    val volumePerUnitInCubicInches: Double,
    val totalVolumeInCubicInches: Double
)

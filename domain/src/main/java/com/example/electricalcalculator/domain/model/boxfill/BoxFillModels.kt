package com.example.electricalcalculator.domain.model.boxfill

/**
 * Enum representing different types of electrical boxes
 */
enum class BoxType {
    DEVICE_BOX,    // Standard device box
    JUNCTION_BOX,  // Junction box
    CEILING_BOX,   // Ceiling box for fixtures
    FLOOR_BOX,     // Floor box
    MASONRY_BOX    // Masonry box
}

/**
 * Enum representing different types of box components
 */
enum class ComponentType {
    CONDUCTOR,     // Wire conductor
    DEVICE,        // Switch, receptacle, etc.
    CLAMP,         // Cable clamp
    SUPPORT_FITTING, // Support fitting
    EQUIPMENT_GROUNDING_CONDUCTOR // Equipment grounding conductor
}

/**
 * Data class representing a component in a box for fill calculations
 */
data class BoxComponent(
    val type: ComponentType,
    val wireSize: String = "",  // AWG size for conductors (e.g., "14", "12")
    val quantity: Int = 1,
    val volumeInCubicInches: Double = 0.0  // Pre-calculated volume if known
)

/**
 * Data class representing input parameters for box fill calculations
 */
data class BoxFillInput(
    val boxType: BoxType,
    val boxDimensions: String,  // Dimensions (e.g., "4x4x1.5")
    val boxVolumeInCubicInches: Double,  // Volume in cubic inches
    val components: List<BoxComponent> = emptyList()
)

/**
 * Data class representing the result of a box fill calculation
 */
data class BoxFillResult(
    val boxType: BoxType,
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val totalRequiredVolumeInCubicInches: Double,
    val remainingVolumeInCubicInches: Double,
    val fillPercentage: Double,
    val isWithinLimits: Boolean,
    val componentDetails: List<ComponentDetail>
)

/**
 * Data class representing details of a component in the calculation result
 */
data class ComponentDetail(
    val type: ComponentType,
    val description: String,
    val quantity: Int,
    val volumePerUnitInCubicInches: Double,
    val totalVolumeInCubicInches: Double
)

package com.example.electricianapp.domain.model.boxfill // Corrected package

/**
 * Enum representing different types of electrical boxes
 */
enum class BoxType {
    DEVICE_BOX,    // Standard device box
    JUNCTION_BOX,  // Junction box
    CEILING_BOX,   // Ceiling box for fixtures
    FLOOR_BOX,     // Floor box
    MASONRY_BOX    // Masonry box
    // TODO: Add more specific types if needed (e.g., SQUARE_BOX, ROUND_BOX from other files)
}

/**
 * Enum representing different types of box components counted for fill (NEC 314.16(B))
 */
enum class ComponentType {
    CONDUCTOR,     // Each conductor originating outside and terminating/spliced inside
    DEVICE,        // Each yoke or strap containing one or more devices (switch, receptacle)
    CLAMP,         // Internal cable clamps (single allowance based on largest conductor)
    SUPPORT_FITTING, // Fixture studs or hickeys (each counts as one based on largest conductor)
    EQUIPMENT_GROUNDING_CONDUCTOR // All equipment grounding conductors combined (single allowance based on largest EGC)
}

/**
 * Data class representing a component to be included in a box fill calculation
 */
data class BoxComponent(
    val type: ComponentType,
    val wireSize: String? = null,  // AWG size (e.g., "14", "12", "10", "8", "6") - Required for CONDUCTOR, DEVICE, CLAMP, SUPPORT_FITTING, EQUIPMENT_GROUNDING_CONDUCTOR
    val quantity: Int = 1 // Number of this specific component (e.g., number of #12 conductors, number of devices on a single yoke)
    // Note: Volume is calculated based on type and wireSize according to NEC 314.16(B)
)

/**
 * Data class representing input parameters for box fill calculations
 */
data class BoxFillInput(
    // val id: Long = 0, // Consider adding if saving inputs
    val boxType: BoxType,
    val boxDimensions: String,  // Descriptive dimensions (e.g., "4x4x1.5", "3x2x2.5") - Used for display/lookup
    val boxVolumeInCubicInches: Double,  // Actual marked or calculated volume of the box
    val components: List<BoxComponent> = emptyList()
    // val timestamp: Long = System.currentTimeMillis(), // Consider adding if saving inputs
    // val notes: String = "" // Consider adding if saving inputs
)

/**
 * Data class representing the result of a box fill calculation
 */
data class BoxFillResult(
    // val id: Long = 0, // Consider adding if saving results
    // val inputId: Long, // Consider adding if saving results
    val boxType: BoxType,
    val boxDimensions: String,
    val boxVolumeInCubicInches: Double,
    val totalRequiredVolumeInCubicInches: Double,
    val remainingVolumeInCubicInches: Double,
    val fillPercentage: Double,
    val isWithinLimits: Boolean,
    val componentDetails: List<ComponentDetail> // Breakdown of volume contributions
)

/**
 * Data class representing details of a component's volume contribution in the calculation result
 */
data class ComponentDetail(
    val type: ComponentType,
    val description: String, // e.g., "#12 Conductor", "Device Yoke", "Internal Clamps"
    val quantity: Int, // How many of this item were counted
    val volumePerUnitInCubicInches: Double, // Volume allowance per item according to NEC 314.16(B)
    val totalVolumeInCubicInches: Double // Total volume allowance for this component type/size
)

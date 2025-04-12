2BSArF-uMUJn3B3F8bmUfGEEin9rA3KNpackage com.example.electricianapp.domain.model.boxfill // Corrected package

/**
 * Represents a single component input for box fill calculation.
 * @param type The type of the component.
 * @param quantity The number of this component.
 * @param wireSize The AWG wire size associated with the component (e.g., "14", "12"), relevant for conductors, devices, etc.
 */
data class BoxComponent(
    val type: ComponentType,
    val quantity: Int,
    val wireSize: String // Assuming AWG string format like "14", "12", "1/0" etc.
)

package com.example.electricianapp.domain.model.voltagedrop

/**
 * Enum representing different types of electrical systems
 */
enum class SystemType {
    SINGLE_PHASE,  // Single-phase AC
    THREE_PHASE,   // Three-phase AC
    DC             // Direct current
}

/**
 * Enum representing different types of conductors
 */
enum class ConductorType {
    COPPER,        // Copper conductors
    ALUMINUM       // Aluminum conductors
}

/**
 * Enum representing different types of conduit/raceway materials
 */
enum class ConduitMaterial {
    PVC,           // PVC conduit
    STEEL,         // Steel conduit
    ALUMINUM,      // Aluminum conduit
    DIRECT_BURIAL  // Direct burial (no conduit)
}

/**
 * Enum representing different temperature ratings of conductors
 */
enum class TemperatureRating {
    RATING_60C,    // 60°C (140°F)
    RATING_75C,    // 75°C (167°F)
    RATING_90C     // 90°C (194°F)
}

/**
 * Data class representing input parameters for voltage drop calculations
 */
data class VoltageDropInput(
    val systemType: SystemType,
    val conductorType: ConductorType,
    val conduitMaterial: ConduitMaterial,
    val temperatureRating: TemperatureRating,
    val wireSize: String,         // AWG or kcmil size (e.g., "14", "12", "1/0", "250")
    val lengthInFeet: Double,     // One-way length of the circuit
    val loadInAmps: Double,       // Load current in amperes
    val voltageInVolts: Double,   // System voltage (e.g., 120, 240, 480)
    val powerFactor: Double = 1.0 // Power factor (0.0-1.0), default 1.0 for DC or resistive loads
)

/**
 * Data class representing the result of a voltage drop calculation
 */
data class VoltageDropResult(
    val voltageDropInVolts: Double,       // Calculated voltage drop in volts
    val voltageDropPercentage: Double,    // Voltage drop as a percentage of source voltage
    val conductorResistance: Double,      // Resistance per 1000 feet (ohms/1000ft)
    val conductorReactance: Double,       // Reactance per 1000 feet (ohms/1000ft)
    val impedance: Double,                // Total impedance (ohms)
    val isWithinRecommendedLimits: Boolean, // True if voltage drop is within NEC recommended limits
    val recommendedLimit: Double,         // NEC recommended limit (typically 3% for branch circuits, 5% for feeders)
    val endVoltage: Double                // Voltage at the load end
)

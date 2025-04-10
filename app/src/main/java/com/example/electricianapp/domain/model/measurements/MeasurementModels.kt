package com.example.electricianapp.domain.model.measurements

import java.util.Date
import java.util.UUID

/**
 * Represents a measurement device that can be connected to the app
 */
data class MeasurementDevice(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: DeviceType,
    val manufacturer: String,
    val model: String,
    val macAddress: String,
    val lastConnected: Date? = null,
    val isConnected: Boolean = false,
    val batteryLevel: Int? = null
)

/**
 * Represents the type of measurement device
 */
enum class DeviceType {
    LASER_DISTANCE_METER,
    THERMAL_CAMERA,
    POWER_QUALITY_ANALYZER,
    MULTIMETER,
    CLAMP_METER,
    VOLTAGE_TESTER,
    OTHER
}

/**
 * Represents a distance measurement taken with a laser distance meter
 */
data class DistanceMeasurement(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val jobId: String? = null,
    val timestamp: Date = Date(),
    val distance: Double,
    val unit: DistanceUnit,
    val label: String = "",
    val notes: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Represents the unit of distance measurement
 */
enum class DistanceUnit {
    METER,
    CENTIMETER,
    MILLIMETER,
    FOOT,
    INCH
}

/**
 * Represents a thermal image captured with a thermal camera
 */
data class ThermalImage(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val jobId: String? = null,
    val timestamp: Date = Date(),
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String = "",
    val description: String = "",
    val minTemperature: Double? = null,
    val maxTemperature: Double? = null,
    val averageTemperature: Double? = null,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tags: List<String> = emptyList()
)

/**
 * Represents the unit of temperature measurement
 */
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT,
    KELVIN
}

/**
 * Represents a power quality measurement taken with a power quality analyzer
 */
data class PowerQualityMeasurement(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val jobId: String? = null,
    val timestamp: Date = Date(),
    val location: String = "",
    val voltage: Double? = null,
    val current: Double? = null,
    val frequency: Double? = null,
    val powerFactor: Double? = null,
    val activePower: Double? = null,
    val reactivePower: Double? = null,
    val apparentPower: Double? = null,
    val totalHarmonicDistortion: Double? = null,
    val notes: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Represents a measurement project that groups related measurements
 */
data class MeasurementProject(
    val id: String = UUID.randomUUID().toString(),
    val jobId: String? = null,
    val name: String,
    val description: String = "",
    val createdDate: Date = Date(),
    val modifiedDate: Date = Date(),
    val distanceMeasurements: List<DistanceMeasurement> = emptyList(),
    val thermalImages: List<ThermalImage> = emptyList(),
    val powerQualityMeasurements: List<PowerQualityMeasurement> = emptyList(),
    val tags: List<String> = emptyList()
)

/**
 * Represents a Bluetooth device discovered during scanning
 */
data class BluetoothDeviceInfo(
    val name: String?,
    val address: String,
    val rssi: Int,
    val deviceClass: Int,
    val isBonded: Boolean
)

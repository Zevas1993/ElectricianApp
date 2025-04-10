package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a measurement device in the database
 */
@Entity(tableName = "measurement_devices")
data class MeasurementDeviceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // Stored as string representation of DeviceType
    val manufacturer: String,
    val model: String,
    val macAddress: String,
    val lastConnected: Long?, // Stored as timestamp
    val isConnected: Boolean,
    val batteryLevel: Int?
)

/**
 * Entity representing a distance measurement in the database
 */
@Entity(
    tableName = "distance_measurements",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementDeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("deviceId"),
        Index("jobId")
    ]
)
data class DistanceMeasurementEntity(
    @PrimaryKey
    val id: String,
    val deviceId: String,
    val jobId: String?,
    val timestamp: Long, // Stored as timestamp
    val distance: Double,
    val unit: String, // Stored as string representation of DistanceUnit
    val label: String,
    val notes: String,
    val latitude: Double?,
    val longitude: Double?
)

/**
 * Entity representing a thermal image in the database
 */
@Entity(
    tableName = "thermal_images",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementDeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("deviceId"),
        Index("jobId")
    ]
)
data class ThermalImageEntity(
    @PrimaryKey
    val id: String,
    val deviceId: String,
    val jobId: String?,
    val timestamp: Long, // Stored as timestamp
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val description: String,
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val averageTemperature: Double?,
    val temperatureUnit: String, // Stored as string representation of TemperatureUnit
    val latitude: Double?,
    val longitude: Double?,
    val tagsJson: String // JSON array of tags
)

/**
 * Entity representing a power quality measurement in the database
 */
@Entity(
    tableName = "power_quality_measurements",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementDeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("deviceId"),
        Index("jobId")
    ]
)
data class PowerQualityMeasurementEntity(
    @PrimaryKey
    val id: String,
    val deviceId: String,
    val jobId: String?,
    val timestamp: Long, // Stored as timestamp
    val location: String,
    val voltage: Double?,
    val current: Double?,
    val frequency: Double?,
    val powerFactor: Double?,
    val activePower: Double?,
    val reactivePower: Double?,
    val apparentPower: Double?,
    val totalHarmonicDistortion: Double?,
    val notes: String,
    val latitude: Double?,
    val longitude: Double?
)

/**
 * Entity representing a measurement project in the database
 */
@Entity(
    tableName = "measurement_projects",
    indices = [Index("jobId")]
)
data class MeasurementProjectEntity(
    @PrimaryKey
    val id: String,
    val jobId: String?,
    val name: String,
    val description: String,
    val createdDate: Long, // Stored as timestamp
    val modifiedDate: Long, // Stored as timestamp
    val tagsJson: String // JSON array of tags
)

/**
 * Entity representing a relationship between a project and a distance measurement
 */
@Entity(
    tableName = "project_distance_measurements",
    primaryKeys = ["projectId", "measurementId"],
    foreignKeys = [
        ForeignKey(
            entity = MeasurementProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DistanceMeasurementEntity::class,
            parentColumns = ["id"],
            childColumns = ["measurementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index("measurementId")
    ]
)
data class ProjectDistanceMeasurementCrossRef(
    val projectId: String,
    val measurementId: String
)

/**
 * Entity representing a relationship between a project and a thermal image
 */
@Entity(
    tableName = "project_thermal_images",
    primaryKeys = ["projectId", "imageId"],
    foreignKeys = [
        ForeignKey(
            entity = MeasurementProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ThermalImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index("imageId")
    ]
)
data class ProjectThermalImageCrossRef(
    val projectId: String,
    val imageId: String
)

/**
 * Entity representing a relationship between a project and a power quality measurement
 */
@Entity(
    tableName = "project_power_quality_measurements",
    primaryKeys = ["projectId", "measurementId"],
    foreignKeys = [
        ForeignKey(
            entity = MeasurementProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PowerQualityMeasurementEntity::class,
            parentColumns = ["id"],
            childColumns = ["measurementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index("measurementId")
    ]
)
data class ProjectPowerQualityMeasurementCrossRef(
    val projectId: String,
    val measurementId: String
)

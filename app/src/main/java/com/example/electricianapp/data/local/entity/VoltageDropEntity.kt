package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.voltagedrop.ConduitMaterial
import com.example.electricianapp.domain.model.voltagedrop.ConductorType
import com.example.electricianapp.domain.model.voltagedrop.SystemType
import com.example.electricianapp.domain.model.voltagedrop.TemperatureRating

/**
 * Room entity for storing voltage drop calculations.
 * Combines both input and result data for simplicity.
 */
@Entity(tableName = "voltage_drop_calculations")
data class VoltageDropEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Input fields
    val systemType: SystemType,
    val conductorType: ConductorType,
    val conduitMaterial: ConduitMaterial,
    val temperatureRating: TemperatureRating,
    val wireSize: String,
    val lengthInFeet: Double,
    val loadInAmps: Double,
    val voltageInVolts: Double,
    val powerFactor: Double,
    
    // Result fields
    val voltageDropInVolts: Double,
    val voltageDropPercentage: Double,
    val conductorResistance: Double,
    val conductorReactance: Double,
    val impedance: Double,
    val isWithinRecommendedLimits: Boolean,
    val recommendedLimit: Double,
    val endVoltage: Double,
    
    // Metadata
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

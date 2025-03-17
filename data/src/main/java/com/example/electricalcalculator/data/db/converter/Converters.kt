package com.example.electricalcalculator.data.db.converter

import androidx.room.TypeConverter
import com.example.electricalcalculator.domain.model.ApplianceType
import com.example.electricalcalculator.domain.model.PhaseType

/**
 * Type converters for Room database.
 * Handles conversion between custom types and primitive types that Room can store.
 */
class Converters {
    
    /**
     * Convert ApplianceType enum to string for storage.
     */
    @TypeConverter
    fun fromApplianceType(applianceType: ApplianceType): String {
        return applianceType.name
    }
    
    /**
     * Convert string to ApplianceType enum.
     */
    @TypeConverter
    fun toApplianceType(value: String): ApplianceType {
        return try {
            ApplianceType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ApplianceType.OTHER // Default in case of invalid value
        }
    }
    
    /**
     * Convert PhaseType enum to string for storage.
     */
    @TypeConverter
    fun fromPhaseType(phaseType: PhaseType): String {
        return phaseType.name
    }
    
    /**
     * Convert string to PhaseType enum.
     */
    @TypeConverter
    fun toPhaseType(value: String): PhaseType {
        return try {
            PhaseType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            PhaseType.SINGLE_PHASE // Default in case of invalid value
        }
    }
    
    /**
     * Convert list of strings to a single string for storage.
     */
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }
    
    /**
     * Convert single string to list of strings.
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",")
        }
    }
    
    /**
     * Convert list of doubles to a single string for storage.
     */
    @TypeConverter
    fun fromDoubleList(list: List<Double>): String {
        return list.joinToString(",")
    }
    
    /**
     * Convert single string to list of doubles.
     */
    @TypeConverter
    fun toDoubleList(value: String): List<Double> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(",").map { it.toDouble() }
        }
    }
}

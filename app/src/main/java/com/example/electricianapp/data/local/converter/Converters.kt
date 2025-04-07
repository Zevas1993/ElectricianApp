package com.example.electricianapp.data.local.converter // Corrected package

import androidx.room.TypeConverter
// TODO: Define these enums in the domain.model package if they are needed
// import com.example.electricianapp.domain.model.ApplianceType
// import com.example.electricianapp.domain.model.PhaseType
import java.util.Date // Import Date for potential future use
import com.example.electricianapp.domain.model.dwellingload.Appliance // Import Appliance
import com.example.electricianapp.domain.model.dwellingload.DwellingType // Import DwellingType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database.
 * Handles conversion between custom types and primitive types that Room can store.
 */
class Converters {

    // --- Converters from the Job/Task App ---
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Assuming JobStatus enum exists in the correct package
    @TypeConverter
    fun fromJobStatus(status: com.example.electricianapp.data.model.JobStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toJobStatus(value: String?): com.example.electricianapp.data.model.JobStatus? {
        return value?.let { enumValueOf<com.example.electricianapp.data.model.JobStatus>(it) }
    }

    // --- Converters from the Calculator App Report (Potentially adapt/remove if enums don't exist) ---

    /**
     * Convert ApplianceType enum to string for storage.
     */
    /* // Uncomment if ApplianceType enum is defined and needed
    @TypeConverter
    fun fromApplianceType(applianceType: ApplianceType): String {
        return applianceType.name
    }
    */

    /**
     * Convert string to ApplianceType enum.
     */
    /* // Uncomment if ApplianceType enum is defined and needed
    @TypeConverter
    fun toApplianceType(value: String): ApplianceType {
        return try {
            ApplianceType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ApplianceType.OTHER // Default in case of invalid value
        }
    }
    */

    /**
     * Convert PhaseType enum to string for storage.
     */
    /* // Uncomment if PhaseType enum is defined and needed
    @TypeConverter
    fun fromPhaseType(phaseType: PhaseType): String {
        return phaseType.name
    }
    */

    /**
     * Convert string to PhaseType enum.
     */
    /* // Uncomment if PhaseType enum is defined and needed
    @TypeConverter
    fun toPhaseType(value: String): PhaseType {
        return try {
            PhaseType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            PhaseType.SINGLE_PHASE // Default in case of invalid value
        }
    }
    */

    /**
     * Convert list of strings to a single string for storage.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    /**
     * Convert single string to list of strings.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.takeIf { it.isNotBlank() }?.split(",")
    }

    /**
     * Convert list of doubles to a single string for storage.
     */
    @TypeConverter
    fun fromDoubleList(list: List<Double>?): String? {
        return list?.joinToString(",")
    }

    /**
     * Convert single string to list of doubles.
     */
    @TypeConverter
    fun toDoubleList(value: String?): List<Double>? {
        return value?.takeIf { it.isNotBlank() }?.split(",")?.mapNotNull { it.toDoubleOrNull() }
    }

    // --- Converters for DwellingLoadCalculationEntity ---

    @TypeConverter
    fun fromDwellingType(type: DwellingType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toDwellingType(value: String?): DwellingType? {
        return value?.let { enumValueOf<DwellingType>(it) }
    }

    @TypeConverter
    fun fromApplianceList(list: List<Appliance>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toApplianceList(value: String?): List<Appliance>? {
        val listType = object : TypeToken<List<Appliance>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringDoubleMap(map: Map<String, Double>?): String? {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun toStringDoubleMap(value: String?): Map<String, Double>? {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}

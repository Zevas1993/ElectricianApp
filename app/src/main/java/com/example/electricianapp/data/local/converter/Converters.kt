package com.example.electricianapp.data.local.converter // Corrected package

import androidx.room.TypeConverter
// Commented imports removed
import java.util.Date // Import Date for potential future use
import com.example.electricianapp.domain.model.dwellingload.Appliance // Import Appliance
import com.example.electricianapp.domain.model.dwellingload.DwellingType // Import DwellingType
import com.example.electricianapp.domain.model.conduitfill.Wire // Import Wire
import com.example.electricianapp.domain.model.conduitfill.WireDetail // Import WireDetail
import com.example.electricianapp.domain.model.boxfill.BoxComponent // Import BoxComponent
import com.example.electricianapp.domain.model.boxfill.BoxType // Import BoxType
import com.example.electricianapp.domain.model.boxfill.ComponentDetail // Import ComponentDetail
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

    // Removed commented out converters for ApplianceType and PhaseType

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

    // --- Converters for ConduitFillCalculationEntity ---

    @TypeConverter
    fun fromWireList(list: List<Wire>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toWireList(value: String?): List<Wire>? {
        val listType = object : TypeToken<List<Wire>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromWireDetailList(list: List<WireDetail>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toWireDetailList(value: String?): List<WireDetail>? {
        val listType = object : TypeToken<List<WireDetail>>() {}.type
        return Gson().fromJson(value, listType)
    }

    // --- Converters for BoxFill Entities ---

    @TypeConverter
    fun fromBoxType(type: BoxType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toBoxType(value: String?): BoxType? {
        return value?.let { enumValueOf<BoxType>(it) }
    }

    @TypeConverter
    fun fromBoxComponentList(list: List<BoxComponent>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toBoxComponentList(value: String?): List<BoxComponent>? {
        val listType = object : TypeToken<List<BoxComponent>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromComponentDetailList(list: List<ComponentDetail>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toComponentDetailList(value: String?): List<ComponentDetail>? {
        val listType = object : TypeToken<List<ComponentDetail>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

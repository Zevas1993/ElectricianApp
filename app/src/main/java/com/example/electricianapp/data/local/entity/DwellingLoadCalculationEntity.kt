package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.electricianapp.data.local.converter.Converters // Assuming converters will be here
import com.example.electricianapp.domain.model.dwellingload.Appliance
import com.example.electricianapp.domain.model.dwellingload.DwellingType

@Entity(tableName = "dwelling_load_calculations")
@TypeConverters(Converters::class) // Specify converters for complex types
data class DwellingLoadCalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculationName: String, // e.g., "Project Alpha - Unit 101"
    val timestamp: Long = System.currentTimeMillis(),

    // Input Fields
    val dwellingType: DwellingType,
    val squareFootage: Double,
    val smallApplianceCircuits: Int,
    val laundryCircuits: Int,
    val appliances: List<Appliance>, // Requires TypeConverter

    // Result Fields
    val totalConnectedLoad: Double,
    val totalDemandLoad: Double,
    val serviceSize: Int,
    val generalLightingLoad: Double,
    val smallApplianceLoad: Double,
    val laundryLoad: Double,
    val applianceLoads: Map<String, Double>, // Requires TypeConverter
    val demandFactors: Map<String, Double> // Requires TypeConverter
)

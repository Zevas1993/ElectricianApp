package com.example.electricianapp.data.repository.dwellingload

import com.example.electricianapp.data.local.dao.DwellingLoadDao // Corrected import path
import com.example.electricianapp.data.local.entity.DwellingLoadEntity // Corrected import path & name
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult
import com.example.electricianapp.domain.repository.dwellingload.DwellingLoadRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the DwellingLoadRepository interface
 */
class DwellingLoadRepositoryImpl @Inject constructor(
    private val dwellingLoadDao: DwellingLoadDao, // Corrected DAO name and type
    private val gson: Gson // Gson needs to be provided via Hilt
) : DwellingLoadRepository {

    override suspend fun saveCalculation(
        input: DwellingLoadInput,
        result: DwellingLoadResult
    ): Long {
        // Map domain models to the DwellingLoadEntity
        val entity = DwellingLoadEntity(
            // id = 0, // Let Room auto-generate
            timestamp = System.currentTimeMillis(),
            dwellingType = input.dwellingType.name,
            squareFootage = input.squareFootage,
            smallApplianceCircuits = input.smallApplianceCircuits,
            laundryCircuits = input.laundryCircuits,
            appliancesJson = gson.toJson(input.appliances), // Store list as JSON
            totalConnectedLoad = result.totalConnectedLoad,
            totalDemandLoad = result.totalDemandLoad,
            serviceSize = result.serviceSize,
            generalLightingLoad = result.generalLightingLoad,
            smallApplianceLoad = result.smallApplianceLoad,
            laundryLoad = result.laundryLoad,
            applianceLoadsJson = gson.toJson(result.applianceLoads), // Store map as JSON
            demandFactorsJson = gson.toJson(result.demandFactors) // Store map as JSON
            // userId = null // Set if needed
        )
        return dwellingLoadDao.insert(entity) // Use corrected DAO
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<DwellingLoadInput, DwellingLoadResult>>> {
        // Use the corrected DAO method name
        return dwellingLoadDao.getAllCalculations().map { entities ->
            entities.mapNotNull { entity -> // Use mapNotNull to skip potential mapping errors
                try {
                    val input = mapEntityToInput(entity)
                    val result = mapEntityToResult(entity)
                    Pair(input, result)
                } catch (e: Exception) {
                    // Log error or handle invalid data
                    null
                }
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<DwellingLoadInput, DwellingLoadResult>? {
        val entity = dwellingLoadDao.getCalculationById(id) ?: return null // Use corrected DAO
        return try {
            val input = mapEntityToInput(entity)
            val result = mapEntityToResult(entity)
            Pair(input, result)
        } catch (e: Exception) {
            // Log error or handle invalid data
            null
        }
    }

    override suspend fun deleteCalculation(id: Long) {
        // DAO now has deleteCalculationById, but let's keep this logic for safety
        dwellingLoadDao.getCalculationById(id)?.let { entity -> // Use corrected DAO
            dwellingLoadDao.delete(entity) // Use corrected DAO
        }
    }

    override suspend fun clearHistory() {
        dwellingLoadDao.deleteAll() // Use corrected DAO method name
    }

    /**
     * Map database entity to domain input model
     */
    private fun mapEntityToInput(entity: DwellingLoadEntity): DwellingLoadInput { // Use corrected Entity
        val dwellingType = com.example.electricianapp.domain.model.dwellingload.DwellingType.valueOf(entity.dwellingType)
        // Use TypeToken for lists with Gson
        val appliances: List<com.example.electricianapp.domain.model.dwellingload.Appliance> = gson.fromJson<List<com.example.electricianapp.domain.model.dwellingload.Appliance>>(
            entity.appliancesJson,
            object : com.google.gson.reflect.TypeToken<List<com.example.electricianapp.domain.model.dwellingload.Appliance>>() {}.type
        )

        return DwellingLoadInput(
            dwellingType = dwellingType,
            squareFootage = entity.squareFootage,
            smallApplianceCircuits = entity.smallApplianceCircuits, // Added field
            laundryCircuits = entity.laundryCircuits, // Added field
            appliances = appliances
        )
    }

    /**
     * Map database entity to domain result model
     */
    private fun mapEntityToResult(entity: DwellingLoadEntity): DwellingLoadResult { // Use corrected Entity
        // Use TypeToken for maps with Gson
        val applianceLoads: Map<String, Double> = gson.fromJson(
            entity.applianceLoadsJson,
            object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
        ) ?: emptyMap() // Provide default if JSON is null/invalid
        val demandFactors: Map<String, Double> = gson.fromJson(
            entity.demandFactorsJson,
            object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
        )

        return DwellingLoadResult(
            totalConnectedLoad = entity.totalConnectedLoad, // Use stored value
            totalDemandLoad = entity.totalDemandLoad, // Use stored value
            serviceSize = entity.serviceSize, // Use stored value
            generalLightingLoad = entity.generalLightingLoad, // Use stored value
            smallApplianceLoad = entity.smallApplianceLoad, // Use stored value
            laundryLoad = entity.laundryLoad, // Use stored value
            applianceLoads = applianceLoads, // Use stored value
            demandFactors = demandFactors // Use stored value
        )
    }
}

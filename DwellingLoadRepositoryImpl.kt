package com.example.electricalcalculator.data.repository.dwellingload

import com.example.electricalcalculator.data.database.dao.DwellingLoadCalculationDao
import com.example.electricalcalculator.data.database.entity.DwellingLoadCalculationEntity
import com.example.electricalcalculator.domain.model.dwellingload.DwellingLoadInput
import com.example.electricalcalculator.domain.model.dwellingload.DwellingLoadResult
import com.example.electricalcalculator.domain.repository.dwellingload.DwellingLoadRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the DwellingLoadRepository interface
 */
class DwellingLoadRepositoryImpl @Inject constructor(
    private val dwellingLoadCalculationDao: DwellingLoadCalculationDao,
    private val gson: Gson
) : DwellingLoadRepository {

    override suspend fun saveCalculation(
        input: DwellingLoadInput,
        result: DwellingLoadResult
    ): Long {
        val entity = DwellingLoadCalculationEntity(
            userId = null, // Set user ID if implementing user authentication
            dwellingType = input.dwellingType.name,
            squareFootage = input.squareFootage,
            appliancesJson = gson.toJson(input.appliances),
            totalLoad = result.totalConnectedLoad,
            demandLoad = result.totalDemandLoad,
            serviceSize = result.serviceSize
        )
        return dwellingLoadCalculationDao.insert(entity)
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<DwellingLoadInput, DwellingLoadResult>>> {
        return dwellingLoadCalculationDao.getCalculationsByUser(null).map { entities ->
            entities.map { entity ->
                val input = mapEntityToInput(entity)
                val result = mapEntityToResult(entity)
                Pair(input, result)
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<DwellingLoadInput, DwellingLoadResult>? {
        val entity = dwellingLoadCalculationDao.getCalculationById(id) ?: return null
        val input = mapEntityToInput(entity)
        val result = mapEntityToResult(entity)
        return Pair(input, result)
    }

    override suspend fun deleteCalculation(id: Long) {
        dwellingLoadCalculationDao.getCalculationById(id)?.let { entity ->
            dwellingLoadCalculationDao.delete(entity)
        }
    }

    override suspend fun clearHistory() {
        dwellingLoadCalculationDao.deleteAllByUser(null)
    }

    /**
     * Map database entity to domain input model
     */
    private fun mapEntityToInput(entity: DwellingLoadCalculationEntity): DwellingLoadInput {
        val dwellingType = com.example.electricalcalculator.domain.model.dwellingload.DwellingType.valueOf(entity.dwellingType)
        val appliances = gson.fromJson(
            entity.appliancesJson,
            Array<com.example.electricalcalculator.domain.model.dwellingload.Appliance>::class.java
        ).toList()

        return DwellingLoadInput(
            dwellingType = dwellingType,
            squareFootage = entity.squareFootage,
            appliances = appliances
        )
    }

    /**
     * Map database entity to domain result model
     * Note: This is a simplified mapping as the entity doesn't store all result details
     * In a real implementation, you might want to store more details or recalculate
     */
    private fun mapEntityToResult(entity: DwellingLoadCalculationEntity): DwellingLoadResult {
        // This is a simplified mapping - in a real implementation, you might want to
        // either store more details in the database or recalculate the result
        return DwellingLoadResult(
            totalConnectedLoad = entity.totalLoad,
            totalDemandLoad = entity.demandLoad,
            serviceSize = entity.serviceSize,
            generalLightingLoad = entity.squareFootage * 3.0, // Simplified - recalculate or store
            smallApplianceLoad = 3000.0, // Simplified - recalculate or store
            laundryLoad = 1500.0, // Simplified - recalculate or store
            applianceLoads = emptyMap(), // Simplified - recalculate or store
            demandFactors = emptyMap() // Simplified - recalculate or store
        )
    }
}

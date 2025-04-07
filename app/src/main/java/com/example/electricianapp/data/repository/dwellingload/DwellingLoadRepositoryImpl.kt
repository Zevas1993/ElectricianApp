package com.example.electricianapp.data.repository.dwellingload

import com.example.electricianapp.data.local.dao.DwellingLoadDao
import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity // Use new entity
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadInput
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult
import com.example.electricianapp.domain.repository.dwellingload.DwellingLoadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the DwellingLoadRepository interface
 */
class DwellingLoadRepositoryImpl @Inject constructor(
    private val dwellingLoadDao: DwellingLoadDao
    // No need for Gson here, TypeConverters handle it
) : DwellingLoadRepository {

    // TODO: Add a way to name the calculation (e.g., pass name as parameter)
    override suspend fun saveCalculation(
        input: DwellingLoadInput,
        result: DwellingLoadResult,
        calculationName: String // Added parameter for naming
    ): Long {
        // Map domain models to the DwellingLoadCalculationEntity
        val entity = DwellingLoadCalculationEntity(
            // id = 0, // Room auto-generates
            calculationName = calculationName, // Use provided name
            timestamp = System.currentTimeMillis(),
            dwellingType = input.dwellingType, // Store enum directly
            squareFootage = input.squareFootage,
            smallApplianceCircuits = input.smallApplianceCircuits,
            laundryCircuits = input.laundryCircuits,
            appliances = input.appliances, // Store list directly (via TypeConverter)
            totalConnectedLoad = result.totalConnectedLoad,
            totalDemandLoad = result.totalDemandLoad,
            serviceSize = result.serviceSize,
            generalLightingLoad = result.generalLightingLoad,
            smallApplianceLoad = result.smallApplianceLoad,
            laundryLoad = result.laundryLoad,
            applianceLoads = result.applianceLoads, // Store map directly (via TypeConverter)
            demandFactors = result.demandFactors // Store map directly (via TypeConverter)
        )
        return dwellingLoadDao.insertCalculation(entity) // Use correct DAO method
    }

    // Renamed to better reflect what's being returned (saved calculations)
    override fun getSavedCalculations(): Flow<List<DwellingLoadCalculationEntity>> {
        return dwellingLoadDao.getAllCalculations() // Return the flow of entities directly
    }

    override suspend fun getCalculationById(id: Long): DwellingLoadCalculationEntity? {
        return dwellingLoadDao.getCalculationById(id) // Return entity directly
    }

    override suspend fun deleteCalculation(calculation: DwellingLoadCalculationEntity) {
        dwellingLoadDao.deleteCalculation(calculation) // Use correct DAO method
    }

    override suspend fun clearHistory() {
        dwellingLoadDao.deleteAllCalculations() // Use correct DAO method
    }

    // Mapping functions are no longer needed here as the DAO returns the entity directly
    // and the ViewModel/UseCase will handle mapping if necessary for the UI.
}

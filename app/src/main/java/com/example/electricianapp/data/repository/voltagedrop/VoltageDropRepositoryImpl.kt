package com.example.electricianapp.data.repository.voltagedrop

import com.example.electricianapp.data.local.dao.VoltageDropDao
import com.example.electricianapp.data.local.entity.VoltageDropEntity
import com.example.electricianapp.domain.model.voltagedrop.VoltageDropInput
import com.example.electricianapp.domain.model.voltagedrop.VoltageDropResult
import com.example.electricianapp.domain.repository.voltagedrop.VoltageDropRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the VoltageDropRepository interface.
 * Handles database operations for voltage drop calculations.
 */
@Singleton
class VoltageDropRepositoryImpl @Inject constructor(
    private val voltageDropDao: VoltageDropDao
) : VoltageDropRepository {

    override suspend fun saveCalculation(input: VoltageDropInput, result: VoltageDropResult): Long {
        val entity = VoltageDropEntity(
            // Input fields
            systemType = input.systemType,
            conductorType = input.conductorType,
            conduitMaterial = input.conduitMaterial,
            temperatureRating = input.temperatureRating,
            wireSize = input.wireSize,
            lengthInFeet = input.lengthInFeet,
            loadInAmps = input.loadInAmps,
            voltageInVolts = input.voltageInVolts,
            powerFactor = input.powerFactor,
            
            // Result fields
            voltageDropInVolts = result.voltageDropInVolts,
            voltageDropPercentage = result.voltageDropPercentage,
            conductorResistance = result.conductorResistance,
            conductorReactance = result.conductorReactance,
            impedance = result.impedance,
            isWithinRecommendedLimits = result.isWithinRecommendedLimits,
            recommendedLimit = result.recommendedLimit,
            endVoltage = result.endVoltage
        )
        return voltageDropDao.insertCalculation(entity)
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<VoltageDropInput, VoltageDropResult>>> {
        return voltageDropDao.getAllCalculations().map { entities ->
            entities.map { entity ->
                val input = VoltageDropInput(
                    systemType = entity.systemType,
                    conductorType = entity.conductorType,
                    conduitMaterial = entity.conduitMaterial,
                    temperatureRating = entity.temperatureRating,
                    wireSize = entity.wireSize,
                    lengthInFeet = entity.lengthInFeet,
                    loadInAmps = entity.loadInAmps,
                    voltageInVolts = entity.voltageInVolts,
                    powerFactor = entity.powerFactor
                )
                
                val result = VoltageDropResult(
                    voltageDropInVolts = entity.voltageDropInVolts,
                    voltageDropPercentage = entity.voltageDropPercentage,
                    conductorResistance = entity.conductorResistance,
                    conductorReactance = entity.conductorReactance,
                    impedance = entity.impedance,
                    isWithinRecommendedLimits = entity.isWithinRecommendedLimits,
                    recommendedLimit = entity.recommendedLimit,
                    endVoltage = entity.endVoltage
                )
                
                Pair(input, result)
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<VoltageDropInput, VoltageDropResult>? {
        val entity = voltageDropDao.getCalculationById(id)
        return entity?.let {
            val input = VoltageDropInput(
                systemType = it.systemType,
                conductorType = it.conductorType,
                conduitMaterial = it.conduitMaterial,
                temperatureRating = it.temperatureRating,
                wireSize = it.wireSize,
                lengthInFeet = it.lengthInFeet,
                loadInAmps = it.loadInAmps,
                voltageInVolts = it.voltageInVolts,
                powerFactor = it.powerFactor
            )
            
            val result = VoltageDropResult(
                voltageDropInVolts = it.voltageDropInVolts,
                voltageDropPercentage = it.voltageDropPercentage,
                conductorResistance = it.conductorResistance,
                conductorReactance = it.conductorReactance,
                impedance = it.impedance,
                isWithinRecommendedLimits = it.isWithinRecommendedLimits,
                recommendedLimit = it.recommendedLimit,
                endVoltage = it.endVoltage
            )
            
            Pair(input, result)
        }
    }

    override suspend fun deleteCalculation(id: Long) {
        voltageDropDao.deleteCalculationById(id)
    }

    override suspend fun clearHistory() {
        voltageDropDao.clearAllCalculations()
    }
}

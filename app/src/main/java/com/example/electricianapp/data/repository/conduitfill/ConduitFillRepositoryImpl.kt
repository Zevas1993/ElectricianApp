package com.example.electricianapp.data.repository.conduitfill

import com.example.electricianapp.data.local.dao.ConduitFillDao
import com.example.electricianapp.data.local.entity.ConduitFillCalculationEntity
import com.example.electricianapp.domain.model.conduitfill.ConduitFillInput
import com.example.electricianapp.domain.model.conduitfill.ConduitFillResult
import com.example.electricianapp.domain.repository.conduitfill.ConduitFillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConduitFillRepositoryImpl @Inject constructor(
    private val conduitFillDao: ConduitFillDao
) : ConduitFillRepository {

    override suspend fun saveCalculation(input: ConduitFillInput, result: ConduitFillResult): Long {
        val entity = ConduitFillCalculationEntity(
            // Input fields
            conduitType = input.conduitType,
            conduitSize = input.conduitSize,
            wires = input.wires,
            // Result fields
            conduitAreaInSqInches = result.conduitAreaInSqInches,
            totalWireAreaInSqInches = result.totalWireAreaInSqInches,
            fillPercentage = result.fillPercentage,
            maximumAllowedFillPercentage = result.maximumAllowedFillPercentage,
            isWithinLimits = result.isWithinLimits,
            wireDetails = result.wireDetails
        )
        return conduitFillDao.insertCalculation(entity)
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<ConduitFillInput, ConduitFillResult>>> {
        return conduitFillDao.getAllCalculations().map { entities ->
            entities.map { entity ->
                val input = ConduitFillInput(
                    conduitType = entity.conduitType,
                    conduitSize = entity.conduitSize,
                    wires = entity.wires
                )
                val result = ConduitFillResult(
                    conduitType = entity.conduitType,
                    conduitSize = entity.conduitSize,
                    conduitAreaInSqInches = entity.conduitAreaInSqInches,
                    totalWireAreaInSqInches = entity.totalWireAreaInSqInches,
                    fillPercentage = entity.fillPercentage,
                    maximumAllowedFillPercentage = entity.maximumAllowedFillPercentage,
                    isWithinLimits = entity.isWithinLimits,
                    wireDetails = entity.wireDetails
                )
                Pair(input, result) // Consider adding ID if needed for deletion later
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<ConduitFillInput, ConduitFillResult>? {
        val entity = conduitFillDao.getCalculationById(id)
        return entity?.let {
            val input = ConduitFillInput(
                conduitType = it.conduitType,
                conduitSize = it.conduitSize,
                wires = it.wires
            )
            val result = ConduitFillResult(
                conduitType = it.conduitType,
                conduitSize = it.conduitSize,
                conduitAreaInSqInches = it.conduitAreaInSqInches,
                totalWireAreaInSqInches = it.totalWireAreaInSqInches,
                fillPercentage = it.fillPercentage,
                maximumAllowedFillPercentage = it.maximumAllowedFillPercentage,
                isWithinLimits = it.isWithinLimits,
                wireDetails = it.wireDetails
            )
            Pair(input, result)
        }
    }

    override suspend fun deleteCalculation(id: Long) {
        conduitFillDao.deleteCalculationById(id)
    }

    override suspend fun clearHistory() {
        conduitFillDao.clearAllCalculations()
    }
}

package com.example.electricianapp.data.repository.conduitfill

import com.example.electricianapp.data.local.dao.ConduitFillDao
import com.example.electricianapp.data.local.entity.ConduitFillCalculationEntity
import com.example.electricianapp.domain.model.conduitfill.ConduitFillInput
import com.example.electricianapp.domain.model.conduitfill.ConduitFillResult
import com.example.electricianapp.domain.model.conduitfill.Wire // Need Wire import
import com.example.electricianapp.domain.model.conduitfill.WireDetail // Need WireDetail import
import com.example.electricianapp.domain.repository.conduitfill.ConduitFillRepository
import com.google.gson.Gson // Need Gson import
import com.google.gson.reflect.TypeToken // Need TypeToken import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type // Need Type import
import javax.inject.Inject

class ConduitFillRepositoryImpl @Inject constructor(
    private val conduitFillDao: ConduitFillDao,
    private val gson: Gson // Inject Gson
) : ConduitFillRepository {

    // Define TypeToken for list conversions
    private val wireListType: Type = object : TypeToken<List<Wire>>() {}.type
    private val wireDetailListType: Type = object : TypeToken<List<WireDetail>>() {}.type

    override suspend fun saveCalculation(input: ConduitFillInput, result: ConduitFillResult): Long {
        val entity = ConduitFillCalculationEntity(
            // Input fields
            conduitType = input.conduitType,
            conduitSize = input.conduitSize,
            wiresJson = gson.toJson(input.wires, wireListType), // Correct field name
            // Result fields
            conduitAreaInSqInches = result.conduitAreaInSqInches,
            totalWireAreaInSqInches = result.totalWireAreaInSqInches,
            fillPercentage = result.fillPercentage,
            maximumAllowedFillPercentage = result.maximumAllowedFillPercentage,
            isWithinLimits = result.isWithinLimits,
            wireDetailsJson = gson.toJson(result.wireDetails, wireDetailListType) // Correct field name
        )
        return conduitFillDao.insertCalculation(entity)
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<ConduitFillInput, ConduitFillResult>>> {
        return conduitFillDao.getAllCalculations().map { entities ->
            entities.mapNotNull { entity -> // Use mapNotNull to skip entries with parsing errors
                try {
                    // Use correct entity fields for JSON strings
                    val wires: List<Wire> = gson.fromJson(entity.wiresJson, wireListType) ?: emptyList()
                    val wireDetails: List<WireDetail> = gson.fromJson(entity.wireDetailsJson, wireDetailListType) ?: emptyList()

                    val input = ConduitFillInput(
                        conduitType = entity.conduitType,
                        conduitSize = entity.conduitSize,
                        wires = wires // Use parsed list
                    )
                    val result = ConduitFillResult(
                        conduitType = entity.conduitType,
                        conduitSize = entity.conduitSize,
                        conduitAreaInSqInches = entity.conduitAreaInSqInches,
                        totalWireAreaInSqInches = entity.totalWireAreaInSqInches,
                        fillPercentage = entity.fillPercentage,
                        maximumAllowedFillPercentage = entity.maximumAllowedFillPercentage,
                        isWithinLimits = entity.isWithinLimits,
                        wireDetails = wireDetails // Use parsed list
                    )
                    Pair(input, result)
                } catch (e: Exception) {
                    // Log error or handle corrupted data
                    println("Error parsing ConduitFill history entry ${entity.id}: ${e.message}")
                    null // Skip this entry
                }
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<ConduitFillInput, ConduitFillResult>? {
        val entity = conduitFillDao.getCalculationById(id)
        return entity?.let {
            try {
                // Use correct entity fields for JSON strings
                val wires: List<Wire> = gson.fromJson(it.wiresJson, wireListType) ?: emptyList()
                val wireDetails: List<WireDetail> = gson.fromJson(it.wireDetailsJson, wireDetailListType) ?: emptyList()

                val input = ConduitFillInput(
                    conduitType = it.conduitType,
                    conduitSize = it.conduitSize,
                    wires = wires // Use parsed list
                )
                val result = ConduitFillResult(
                    conduitType = it.conduitType,
                    conduitSize = it.conduitSize,
                    conduitAreaInSqInches = it.conduitAreaInSqInches,
                    totalWireAreaInSqInches = it.totalWireAreaInSqInches,
                    fillPercentage = it.fillPercentage,
                    maximumAllowedFillPercentage = it.maximumAllowedFillPercentage,
                    isWithinLimits = it.isWithinLimits,
                    wireDetails = wireDetails // Use parsed list
                )
                Pair(input, result)
            } catch (e: Exception) {
                // Log error or handle corrupted data
                println("Error parsing ConduitFill entry ${it.id}: ${e.message}")
                null // Return null if parsing fails
            }
        }
    }

    override suspend fun deleteCalculation(id: Long) {
        conduitFillDao.deleteCalculationById(id)
    }

    override suspend fun clearHistory() {
        conduitFillDao.clearAllCalculations()
    }
}

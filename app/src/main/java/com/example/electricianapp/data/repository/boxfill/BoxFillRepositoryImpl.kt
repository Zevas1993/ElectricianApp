package com.example.electricianapp.data.repository.boxfill

import com.example.electricianapp.data.local.dao.BoxFillDao
import com.example.electricianapp.data.local.entity.BoxFillInputEntity
import com.example.electricianapp.data.local.entity.BoxFillResultEntity
import com.example.electricianapp.domain.model.boxfill.BoxComponent
import com.example.electricianapp.domain.model.boxfill.BoxFillInput
import com.example.electricianapp.domain.model.boxfill.BoxFillResult
import com.example.electricianapp.domain.model.boxfill.BoxType
import com.example.electricianapp.domain.model.boxfill.ComponentDetail
import com.example.electricianapp.domain.repository.boxfill.BoxFillRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BoxFillRepositoryImpl @Inject constructor(
    private val boxFillDao: BoxFillDao,
    private val gson: Gson // Inject Gson for JSON conversion
) : BoxFillRepository {

    // Define TypeToken for list conversions
    private val boxComponentListType = object : TypeToken<List<BoxComponent>>() {}.type
    private val componentDetailListType = object : TypeToken<List<ComponentDetail>>() {}.type

    override suspend fun saveCalculation(input: BoxFillInput, result: BoxFillResult): Long {
        val inputEntity = BoxFillInputEntity(
            boxType = input.boxType.name,
            boxDimensions = input.boxDimensions,
            boxVolumeInCubicInches = input.boxVolumeInCubicInches,
            componentsJson = gson.toJson(input.components, boxComponentListType)
        )
        val inputId = boxFillDao.insertInput(inputEntity)

        val resultEntity = BoxFillResultEntity(
            inputId = inputId,
            boxType = result.boxType.name,
            boxDimensions = result.boxDimensions,
            boxVolumeInCubicInches = result.boxVolumeInCubicInches,
            totalRequiredVolumeInCubicInches = result.totalRequiredVolumeInCubicInches,
            remainingVolumeInCubicInches = result.remainingVolumeInCubicInches,
            fillPercentage = result.fillPercentage,
            isWithinLimits = result.isWithinLimits,
            componentDetailsJson = gson.toJson(result.componentDetails, componentDetailListType)
        )
        boxFillDao.insertResult(resultEntity)
        return inputId // Return the ID of the input entry
    }

    override suspend fun getCalculationHistory(): Flow<List<Pair<BoxFillInput, BoxFillResult>>> {
        return boxFillDao.getCalculationHistory().map { historyEntries ->
            historyEntries.mapNotNull { entry ->
                mapHistoryEntryToDomain(entry)
            }
        }
    }

    override suspend fun getCalculationById(id: Long): Pair<BoxFillInput, BoxFillResult>? {
        val historyEntry = boxFillDao.getCalculationById(id)
        return historyEntry?.let { mapHistoryEntryToDomain(it) }
    }

    override suspend fun deleteCalculation(id: Long) {
        boxFillDao.deleteCalculation(id)
    }

    override suspend fun clearHistory() {
        boxFillDao.clearHistory()
    }

    // Helper function to map DAO history entry to domain models
    private fun mapHistoryEntryToDomain(entry: BoxFillDao.BoxFillHistoryEntry): Pair<BoxFillInput, BoxFillResult>? {
        val inputEntity = entry.input
        val resultEntity = entry.result ?: return null // Skip if result is missing

        // Use try-catch for robustness during JSON parsing
        val components: List<BoxComponent> = try {
            gson.fromJson(inputEntity.componentsJson, boxComponentListType) ?: emptyList()
        } catch (e: Exception) {
            // Log error or handle corrupted data
            println("Error parsing BoxComponent list: ${e.message}")
            emptyList()
        }

        val componentDetails: List<ComponentDetail> = try {
            gson.fromJson(resultEntity.componentDetailsJson, componentDetailListType) ?: emptyList()
        } catch (e: Exception) {
             // Log error or handle corrupted data
            println("Error parsing ComponentDetail list: ${e.message}")
            emptyList()
        }

        // Use try-catch for enum parsing
        val boxTypeInput = try { BoxType.valueOf(inputEntity.boxType) } catch (e: IllegalArgumentException) { BoxType.DEVICE_BOX /* Default or handle error */ }
        val boxTypeResult = try { BoxType.valueOf(resultEntity.boxType) } catch (e: IllegalArgumentException) { BoxType.DEVICE_BOX /* Default or handle error */ }


        val input = BoxFillInput(
            boxType = boxTypeInput,
            boxDimensions = inputEntity.boxDimensions,
            boxVolumeInCubicInches = inputEntity.boxVolumeInCubicInches,
            components = components
        )

        val result = BoxFillResult(
            boxType = boxTypeResult,
            boxDimensions = resultEntity.boxDimensions,
            boxVolumeInCubicInches = resultEntity.boxVolumeInCubicInches,
            totalRequiredVolumeInCubicInches = resultEntity.totalRequiredVolumeInCubicInches,
            remainingVolumeInCubicInches = resultEntity.remainingVolumeInCubicInches,
            fillPercentage = resultEntity.fillPercentage,
            isWithinLimits = resultEntity.isWithinLimits,
            componentDetails = componentDetails
        )

        return Pair(input, result)
    }
}

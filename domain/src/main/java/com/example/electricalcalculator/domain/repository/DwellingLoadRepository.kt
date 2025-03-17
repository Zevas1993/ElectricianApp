package com.example.electricalcalculator.domain.repository

import com.example.electricalcalculator.domain.model.Appliance
import com.example.electricalcalculator.domain.model.DwellingUnit
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing DwellingLoad calculations.
 */
interface DwellingLoadRepository {
    /**
     * Get all dwelling units from the data source.
     */
    fun getAllDwellingUnits(): Flow<List<DwellingUnit>>
    
    /**
     * Get a dwelling unit by its ID.
     */
    suspend fun getDwellingUnitById(id: Long): DwellingUnit?
    
    /**
     * Insert a new dwelling unit into the data source.
     * @return the ID of the newly inserted dwelling unit
     */
    suspend fun insertDwellingUnit(dwellingUnit: DwellingUnit): Long
    
    /**
     * Update an existing dwelling unit in the data source.
     */
    suspend fun updateDwellingUnit(dwellingUnit: DwellingUnit)
    
    /**
     * Delete a dwelling unit from the data source.
     */
    suspend fun deleteDwellingUnit(dwellingUnit: DwellingUnit)
    
    /**
     * Delete a dwelling unit by its ID.
     */
    suspend fun deleteDwellingUnitById(id: Long)
    
    /**
     * Get all appliances from the data source.
     */
    fun getAllAppliances(): Flow<List<Appliance>>
    
    /**
     * Get an appliance by its ID.
     */
    suspend fun getApplianceById(id: Long): Appliance?
    
    /**
     * Insert a new appliance into the data source.
     * @return the ID of the newly inserted appliance
     */
    suspend fun insertAppliance(appliance: Appliance): Long
    
    /**
     * Update an existing appliance in the data source.
     */
    suspend fun updateAppliance(appliance: Appliance)
    
    /**
     * Delete an appliance from the data source.
     */
    suspend fun deleteAppliance(appliance: Appliance)
    
    /**
     * Delete an appliance by its ID.
     */
    suspend fun deleteApplianceById(id: Long)
    
    /**
     * Add an appliance to a dwelling unit.
     */
    suspend fun addApplianceToDwellingUnit(dwellingUnitId: Long, applianceId: Long)
    
    /**
     * Remove an appliance from a dwelling unit.
     */
    suspend fun removeApplianceFromDwellingUnit(dwellingUnitId: Long, applianceId: Long)
}

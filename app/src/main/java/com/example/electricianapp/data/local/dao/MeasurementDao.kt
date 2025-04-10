package com.example.electricianapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.electricianapp.data.local.entity.DistanceMeasurementEntity
import com.example.electricianapp.data.local.entity.MeasurementDeviceEntity
import com.example.electricianapp.data.local.entity.MeasurementProjectEntity
import com.example.electricianapp.data.local.entity.PowerQualityMeasurementEntity
import com.example.electricianapp.data.local.entity.ProjectDistanceMeasurementCrossRef
import com.example.electricianapp.data.local.entity.ProjectPowerQualityMeasurementCrossRef
import com.example.electricianapp.data.local.entity.ProjectThermalImageCrossRef
import com.example.electricianapp.data.local.entity.ThermalImageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for measurement-related entities
 */
@Dao
interface MeasurementDao {
    
    // Device operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: MeasurementDeviceEntity)
    
    @Update
    suspend fun updateDevice(device: MeasurementDeviceEntity)
    
    @Delete
    suspend fun deleteDevice(device: MeasurementDeviceEntity)
    
    @Query("DELETE FROM measurement_devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: String)
    
    @Query("SELECT * FROM measurement_devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): MeasurementDeviceEntity?
    
    @Query("SELECT * FROM measurement_devices ORDER BY name ASC")
    fun getAllDevices(): Flow<List<MeasurementDeviceEntity>>
    
    @Query("SELECT * FROM measurement_devices WHERE isConnected = 1 ORDER BY name ASC")
    fun getConnectedDevices(): Flow<List<MeasurementDeviceEntity>>
    
    @Query("SELECT * FROM measurement_devices WHERE macAddress = :macAddress")
    suspend fun getDeviceByMacAddress(macAddress: String): MeasurementDeviceEntity?
    
    // Distance measurement operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistanceMeasurement(measurement: DistanceMeasurementEntity)
    
    @Update
    suspend fun updateDistanceMeasurement(measurement: DistanceMeasurementEntity)
    
    @Delete
    suspend fun deleteDistanceMeasurement(measurement: DistanceMeasurementEntity)
    
    @Query("DELETE FROM distance_measurements WHERE id = :measurementId")
    suspend fun deleteDistanceMeasurementById(measurementId: String)
    
    @Query("SELECT * FROM distance_measurements WHERE id = :measurementId")
    suspend fun getDistanceMeasurementById(measurementId: String): DistanceMeasurementEntity?
    
    @Query("SELECT * FROM distance_measurements WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getDistanceMeasurementsByDeviceId(deviceId: String): Flow<List<DistanceMeasurementEntity>>
    
    @Query("SELECT * FROM distance_measurements WHERE jobId = :jobId ORDER BY timestamp DESC")
    fun getDistanceMeasurementsByJobId(jobId: String): Flow<List<DistanceMeasurementEntity>>
    
    // Thermal image operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThermalImage(thermalImage: ThermalImageEntity)
    
    @Update
    suspend fun updateThermalImage(thermalImage: ThermalImageEntity)
    
    @Delete
    suspend fun deleteThermalImage(thermalImage: ThermalImageEntity)
    
    @Query("DELETE FROM thermal_images WHERE id = :imageId")
    suspend fun deleteThermalImageById(imageId: String)
    
    @Query("SELECT * FROM thermal_images WHERE id = :imageId")
    suspend fun getThermalImageById(imageId: String): ThermalImageEntity?
    
    @Query("SELECT * FROM thermal_images WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getThermalImagesByDeviceId(deviceId: String): Flow<List<ThermalImageEntity>>
    
    @Query("SELECT * FROM thermal_images WHERE jobId = :jobId ORDER BY timestamp DESC")
    fun getThermalImagesByJobId(jobId: String): Flow<List<ThermalImageEntity>>
    
    // Power quality measurement operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPowerQualityMeasurement(measurement: PowerQualityMeasurementEntity)
    
    @Update
    suspend fun updatePowerQualityMeasurement(measurement: PowerQualityMeasurementEntity)
    
    @Delete
    suspend fun deletePowerQualityMeasurement(measurement: PowerQualityMeasurementEntity)
    
    @Query("DELETE FROM power_quality_measurements WHERE id = :measurementId")
    suspend fun deletePowerQualityMeasurementById(measurementId: String)
    
    @Query("SELECT * FROM power_quality_measurements WHERE id = :measurementId")
    suspend fun getPowerQualityMeasurementById(measurementId: String): PowerQualityMeasurementEntity?
    
    @Query("SELECT * FROM power_quality_measurements WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getPowerQualityMeasurementsByDeviceId(deviceId: String): Flow<List<PowerQualityMeasurementEntity>>
    
    @Query("SELECT * FROM power_quality_measurements WHERE jobId = :jobId ORDER BY timestamp DESC")
    fun getPowerQualityMeasurementsByJobId(jobId: String): Flow<List<PowerQualityMeasurementEntity>>
    
    // Measurement project operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurementProject(project: MeasurementProjectEntity)
    
    @Update
    suspend fun updateMeasurementProject(project: MeasurementProjectEntity)
    
    @Delete
    suspend fun deleteMeasurementProject(project: MeasurementProjectEntity)
    
    @Query("DELETE FROM measurement_projects WHERE id = :projectId")
    suspend fun deleteMeasurementProjectById(projectId: String)
    
    @Query("SELECT * FROM measurement_projects WHERE id = :projectId")
    suspend fun getMeasurementProjectById(projectId: String): MeasurementProjectEntity?
    
    @Query("SELECT * FROM measurement_projects ORDER BY modifiedDate DESC")
    fun getAllMeasurementProjects(): Flow<List<MeasurementProjectEntity>>
    
    @Query("SELECT * FROM measurement_projects WHERE jobId = :jobId ORDER BY modifiedDate DESC")
    fun getMeasurementProjectsByJobId(jobId: String): Flow<List<MeasurementProjectEntity>>
    
    @Query("SELECT * FROM measurement_projects WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY modifiedDate DESC")
    fun searchMeasurementProjects(query: String): Flow<List<MeasurementProjectEntity>>
    
    // Cross-reference operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectDistanceMeasurementCrossRef(crossRef: ProjectDistanceMeasurementCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectThermalImageCrossRef(crossRef: ProjectThermalImageCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectPowerQualityMeasurementCrossRef(crossRef: ProjectPowerQualityMeasurementCrossRef)
    
    @Query("DELETE FROM project_distance_measurements WHERE projectId = :projectId")
    suspend fun deleteProjectDistanceMeasurementCrossRefsByProjectId(projectId: String)
    
    @Query("DELETE FROM project_thermal_images WHERE projectId = :projectId")
    suspend fun deleteProjectThermalImageCrossRefsByProjectId(projectId: String)
    
    @Query("DELETE FROM project_power_quality_measurements WHERE projectId = :projectId")
    suspend fun deleteProjectPowerQualityMeasurementCrossRefsByProjectId(projectId: String)
    
    @Query("SELECT * FROM distance_measurements WHERE id IN (SELECT measurementId FROM project_distance_measurements WHERE projectId = :projectId)")
    suspend fun getDistanceMeasurementsByProjectId(projectId: String): List<DistanceMeasurementEntity>
    
    @Query("SELECT * FROM thermal_images WHERE id IN (SELECT imageId FROM project_thermal_images WHERE projectId = :projectId)")
    suspend fun getThermalImagesByProjectId(projectId: String): List<ThermalImageEntity>
    
    @Query("SELECT * FROM power_quality_measurements WHERE id IN (SELECT measurementId FROM project_power_quality_measurements WHERE projectId = :projectId)")
    suspend fun getPowerQualityMeasurementsByProjectId(projectId: String): List<PowerQualityMeasurementEntity>
    
    // Transaction methods
    
    @Transaction
    suspend fun updateMeasurementProjectWithMeasurements(
        project: MeasurementProjectEntity,
        distanceMeasurementIds: List<String>,
        thermalImageIds: List<String>,
        powerQualityMeasurementIds: List<String>
    ) {
        updateMeasurementProject(project)
        
        // Update distance measurements
        deleteProjectDistanceMeasurementCrossRefsByProjectId(project.id)
        distanceMeasurementIds.forEach { measurementId ->
            insertProjectDistanceMeasurementCrossRef(
                ProjectDistanceMeasurementCrossRef(project.id, measurementId)
            )
        }
        
        // Update thermal images
        deleteProjectThermalImageCrossRefsByProjectId(project.id)
        thermalImageIds.forEach { imageId ->
            insertProjectThermalImageCrossRef(
                ProjectThermalImageCrossRef(project.id, imageId)
            )
        }
        
        // Update power quality measurements
        deleteProjectPowerQualityMeasurementCrossRefsByProjectId(project.id)
        powerQualityMeasurementIds.forEach { measurementId ->
            insertProjectPowerQualityMeasurementCrossRef(
                ProjectPowerQualityMeasurementCrossRef(project.id, measurementId)
            )
        }
    }
    
    @Transaction
    suspend fun deleteMeasurementProjectWithReferences(projectId: String) {
        deleteProjectDistanceMeasurementCrossRefsByProjectId(projectId)
        deleteProjectThermalImageCrossRefsByProjectId(projectId)
        deleteProjectPowerQualityMeasurementCrossRefsByProjectId(projectId)
        deleteMeasurementProjectById(projectId)
    }
}

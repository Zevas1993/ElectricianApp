package com.example.electricianapp.domain.repository.measurements

import com.example.electricianapp.domain.model.measurements.BluetoothDeviceInfo
import com.example.electricianapp.domain.model.measurements.DistanceMeasurement
import com.example.electricianapp.domain.model.measurements.MeasurementDevice
import com.example.electricianapp.domain.model.measurements.MeasurementProject
import com.example.electricianapp.domain.model.measurements.PowerQualityMeasurement
import com.example.electricianapp.domain.model.measurements.ThermalImage
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for measurement operations
 */
interface MeasurementRepository {
    
    // Device operations
    suspend fun saveDevice(device: MeasurementDevice): String
    suspend fun updateDevice(device: MeasurementDevice)
    suspend fun deleteDevice(deviceId: String)
    suspend fun getDeviceById(deviceId: String): MeasurementDevice?
    fun getAllDevices(): Flow<List<MeasurementDevice>>
    fun getConnectedDevices(): Flow<List<MeasurementDevice>>
    
    // Bluetooth operations
    suspend fun startBluetoothScan(): Boolean
    suspend fun stopBluetoothScan()
    fun getDiscoveredDevices(): Flow<List<BluetoothDeviceInfo>>
    suspend fun connectToDevice(address: String): Boolean
    suspend fun disconnectFromDevice(address: String): Boolean
    suspend fun isDeviceConnected(address: String): Boolean
    suspend fun getBondedDevices(): List<BluetoothDeviceInfo>
    
    // Distance measurement operations
    suspend fun saveDistanceMeasurement(measurement: DistanceMeasurement): String
    suspend fun updateDistanceMeasurement(measurement: DistanceMeasurement)
    suspend fun deleteDistanceMeasurement(measurementId: String)
    suspend fun getDistanceMeasurementById(measurementId: String): DistanceMeasurement?
    fun getDistanceMeasurementsByDeviceId(deviceId: String): Flow<List<DistanceMeasurement>>
    fun getDistanceMeasurementsByJobId(jobId: String): Flow<List<DistanceMeasurement>>
    suspend fun requestDistanceMeasurement(deviceId: String): DistanceMeasurement?
    
    // Thermal image operations
    suspend fun saveThermalImage(thermalImage: ThermalImage): String
    suspend fun updateThermalImage(thermalImage: ThermalImage)
    suspend fun deleteThermalImage(imageId: String)
    suspend fun getThermalImageById(imageId: String): ThermalImage?
    fun getThermalImagesByDeviceId(deviceId: String): Flow<List<ThermalImage>>
    fun getThermalImagesByJobId(jobId: String): Flow<List<ThermalImage>>
    suspend fun captureThermalImage(deviceId: String, title: String = "", description: String = ""): ThermalImage?
    suspend fun importThermalImage(imageUrl: String, title: String = "", description: String = ""): ThermalImage?
    
    // Power quality measurement operations
    suspend fun savePowerQualityMeasurement(measurement: PowerQualityMeasurement): String
    suspend fun updatePowerQualityMeasurement(measurement: PowerQualityMeasurement)
    suspend fun deletePowerQualityMeasurement(measurementId: String)
    suspend fun getPowerQualityMeasurementById(measurementId: String): PowerQualityMeasurement?
    fun getPowerQualityMeasurementsByDeviceId(deviceId: String): Flow<List<PowerQualityMeasurement>>
    fun getPowerQualityMeasurementsByJobId(jobId: String): Flow<List<PowerQualityMeasurement>>
    suspend fun requestPowerQualityMeasurement(deviceId: String): PowerQualityMeasurement?
    
    // Measurement project operations
    suspend fun saveMeasurementProject(project: MeasurementProject): String
    suspend fun updateMeasurementProject(project: MeasurementProject)
    suspend fun deleteMeasurementProject(projectId: String)
    suspend fun getMeasurementProjectById(projectId: String): MeasurementProject?
    fun getAllMeasurementProjects(): Flow<List<MeasurementProject>>
    fun getMeasurementProjectsByJobId(jobId: String): Flow<List<MeasurementProject>>
    fun searchMeasurementProjects(query: String): Flow<List<MeasurementProject>>
}

package com.example.electricianapp.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.example.electricianapp.domain.model.measurements.BluetoothDeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing Bluetooth connections and communication
 */
@Singleton
class BluetoothService @Inject constructor(
    private val context: Context
) {
    private val bluetoothManager: BluetoothManager? = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDeviceInfo>> = _discoveredDevices.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val connectedSockets = mutableMapOf<String, BluetoothSocket>()
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    // Standard UUID for Serial Port Profile (SPP)
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    // BroadcastReceiver for Bluetooth device discovery
    private val receiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    
                    device?.let {
                        val deviceName = if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                            it.name
                        } else {
                            "Unknown Device"
                        }
                        
                        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                        val deviceClass = intent.getIntExtra(BluetoothDevice.EXTRA_CLASS, 0)
                        val isBonded = it.bondState == BluetoothDevice.BOND_BONDED
                        
                        val deviceInfo = BluetoothDeviceInfo(
                            name = deviceName,
                            address = it.address,
                            rssi = rssi,
                            deviceClass = deviceClass,
                            isBonded = isBonded
                        )
                        
                        val currentDevices = _discoveredDevices.value.toMutableList()
                        if (!currentDevices.any { d -> d.address == deviceInfo.address }) {
                            currentDevices.add(deviceInfo)
                            _discoveredDevices.value = currentDevices
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    _isScanning.value = true
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
            }
        }
    }
    
    init {
        // Register for broadcasts when a device is discovered
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)
    }
    
    /**
     * Start scanning for Bluetooth devices
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    suspend fun startScan(): Boolean = withContext(Dispatchers.IO) {
        if (bluetoothAdapter == null) {
            return@withContext false
        }
        
        if (!bluetoothAdapter.isEnabled) {
            return@withContext false
        }
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return@withContext false
        }
        
        // Clear previous results
        _discoveredDevices.value = emptyList()
        
        // Start discovery
        return@withContext bluetoothAdapter.startDiscovery()
    }
    
    /**
     * Stop scanning for Bluetooth devices
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    suspend fun stopScan() = withContext(Dispatchers.IO) {
        if (bluetoothAdapter == null) {
            return@withContext
        }
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return@withContext
        }
        
        bluetoothAdapter.cancelDiscovery()
    }
    
    /**
     * Get a list of bonded (paired) Bluetooth devices
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun getBondedDevices(): List<BluetoothDeviceInfo> = withContext(Dispatchers.IO) {
        if (bluetoothAdapter == null) {
            return@withContext emptyList()
        }
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return@withContext emptyList()
        }
        
        return@withContext bluetoothAdapter.bondedDevices.map { device ->
            BluetoothDeviceInfo(
                name = device.name,
                address = device.address,
                rssi = 0, // Not available for bonded devices
                deviceClass = device.bluetoothClass.deviceClass,
                isBonded = true
            )
        }
    }
    
    /**
     * Connect to a Bluetooth device
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    suspend fun connectToDevice(address: String): Boolean = withContext(Dispatchers.IO) {
        if (bluetoothAdapter == null) {
            return@withContext false
        }
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return@withContext false
        }
        
        // Cancel discovery because it's resource intensive
        bluetoothAdapter.cancelDiscovery()
        
        try {
            val device = bluetoothAdapter.getRemoteDevice(address)
            val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            
            // Connect to the remote device
            socket.connect()
            
            // Store the socket for later use
            connectedSockets[address] = socket
            
            return@withContext true
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Disconnect from a Bluetooth device
     */
    suspend fun disconnectFromDevice(address: String): Boolean = withContext(Dispatchers.IO) {
        val socket = connectedSockets[address] ?: return@withContext false
        
        try {
            socket.close()
            connectedSockets.remove(address)
            return@withContext true
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Check if a device is connected
     */
    fun isDeviceConnected(address: String): Boolean {
        return connectedSockets.containsKey(address)
    }
    
    /**
     * Send data to a connected device
     */
    suspend fun sendData(address: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val socket = connectedSockets[address] ?: return@withContext false
        
        try {
            val outputStream = socket.outputStream
            outputStream.write(data)
            return@withContext true
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Read data from a connected device
     */
    suspend fun readData(address: String): ByteArray? = withContext(Dispatchers.IO) {
        val socket = connectedSockets[address] ?: return@withContext null
        
        try {
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            val bytes = inputStream.read(buffer)
            
            return@withContext buffer.copyOfRange(0, bytes)
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
    }
    
    /**
     * Listen for data from a connected device
     */
    fun listenForData(address: String, callback: (ByteArray) -> Unit) {
        val socket = connectedSockets[address] ?: return
        
        coroutineScope.launch {
            try {
                val inputStream = socket.inputStream
                val buffer = ByteArray(1024)
                
                while (true) {
                    val bytes = inputStream.read(buffer)
                    if (bytes > 0) {
                        val receivedData = buffer.copyOfRange(0, bytes)
                        callback(receivedData)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Clean up resources when the service is no longer needed
     */
    fun cleanup() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
        
        // Close all connected sockets
        connectedSockets.values.forEach { socket ->
            try {
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        connectedSockets.clear()
    }
}

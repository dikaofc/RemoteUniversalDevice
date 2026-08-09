package com.example.network.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.domain.model.DeviceType
import com.example.domain.model.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class BluetoothDeviceDiscovery(private val context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val btAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    fun discoverBluetoothDevices(): Flow<List<DiscoveredDevice>> = callbackFlow {
        val discoveredMap = mutableMapOf<String, DiscoveredDevice>()

        if (btAdapter == null || !btAdapter.isEnabled) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val bondedDevices = btAdapter.bondedDevices
            bondedDevices?.forEach { dev ->
                val name = dev.name ?: "Bluetooth Remote"
                val address = dev.address
                val deviceType = when {
                    name.lowercase().contains("tv") -> DeviceType.TV
                    name.lowercase().contains("soundbar") || name.lowercase().contains("audio") -> DeviceType.SOUNDBAR
                    else -> DeviceType.MEDIA_PLAYER
                }

                val discovered = DiscoveredDevice(
                    id = "bt_$address",
                    name = name,
                    brand = "Bluetooth Device",
                    deviceType = deviceType,
                    ipAddress = "",
                    port = 0,
                    serviceType = "bluetooth",
                    macAddress = address
                )
                discoveredMap[address] = discovered
            }
            trySend(discoveredMap.values.toList())
        } catch (e: SecurityException) {
            trySend(emptyList())
        }

        awaitClose { }
    }.flowOn(Dispatchers.IO)
}

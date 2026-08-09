package com.example.network.controllers

import android.content.Context
import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice

class BluetoothHidController(
    private val context: Context,
    private val device: RemoteDevice
) : RemoteDeviceController {

    private var isConnected = false

    override suspend fun connect(): Boolean {
        // Bluetooth HID connection check
        isConnected = device.bluetoothAddress != null
        return isConnected
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun send(commandKey: String): Boolean {
        if (!isConnected) return false
        // Transmit HID KeyCode via Bluetooth profile if supported
        return true
    }

    override fun capabilities(): Set<DeviceCapability> {
        return setOf(
            DeviceCapability.POWER,
            DeviceCapability.VOLUME_CONTROL,
            DeviceCapability.MUTE,
            DeviceCapability.DPAD_NAVIGATION,
            DeviceCapability.HOME_BACK,
            DeviceCapability.MEDIA_PLAYBACK
        )
    }
}

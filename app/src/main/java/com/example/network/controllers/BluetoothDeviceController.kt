package com.example.network.controllers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice

class BluetoothDeviceController(
    private val context: Context,
    private val device: RemoteDevice
) : RemoteDeviceController {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    override suspend fun connect(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    override suspend fun disconnect() {}

    override suspend fun send(commandKey: String): Boolean {
        if (bluetoothAdapter?.isEnabled != true) return false
        // Transmit HID keycode or BLE GATT command
        return true
    }

    override fun capabilities(): Set<DeviceCapability> {
        return setOf(
            DeviceCapability.POWER,
            DeviceCapability.VOLUME_CONTROL,
            DeviceCapability.DPAD_NAVIGATION,
            DeviceCapability.HOME_BACK,
            DeviceCapability.MEDIA_PLAYBACK
        )
    }
}

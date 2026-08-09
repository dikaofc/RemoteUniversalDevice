package com.example.transport

import android.content.Context
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.RemoteTransport
import com.example.domain.transport.TransportCapability
import com.example.domain.transport.TransportType
import com.example.hardware.HardwareCapabilityDetector
import com.example.network.controllers.BluetoothDeviceController

class BluetoothTransport(
    private val context: Context,
    override val type: TransportType = TransportType.BLUETOOTH
) : RemoteTransport {

    private val detector = HardwareCapabilityDetector(context)
    private var activeController: BluetoothDeviceController? = null

    override suspend fun connect(device: RemoteDevice): Result<Unit> {
        if (!isAvailable()) {
            return Result.failure(Exception("Bluetooth is disabled or unavailable on this phone."))
        }
        val controller = BluetoothDeviceController(context, device)
        activeController = controller
        val success = controller.connect()
        return if (success) Result.success(Unit) else Result.failure(Exception("Failed to connect via Bluetooth"))
    }

    override suspend fun disconnect() {
        activeController?.disconnect()
        activeController = null
    }

    override suspend fun send(command: RemoteCommand): Result<Unit> {
        val controller = activeController ?: return Result.failure(Exception("Bluetooth controller not connected"))
        val success = controller.send(command.key)
        return if (success) Result.success(Unit) else Result.failure(Exception("Command execution failed via Bluetooth"))
    }

    override fun isAvailable(): Boolean {
        return if (type == TransportType.BLE) {
            detector.detectCapabilities().isBleSupported && detector.detectCapabilities().isBluetoothEnabled
        } else {
            detector.detectCapabilities().isBluetoothEnabled
        }
    }

    override fun capabilities(): Set<TransportCapability> {
        return setOf(
            TransportCapability.POWER,
            TransportCapability.VOLUME,
            TransportCapability.NAVIGATION,
            TransportCapability.MEDIA
        )
    }
}

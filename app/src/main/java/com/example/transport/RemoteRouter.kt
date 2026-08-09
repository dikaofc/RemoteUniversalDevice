package com.example.transport

import android.content.Context
import com.example.domain.model.DeviceType
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.ConnectionStatus
import com.example.domain.transport.RemoteTransport
import com.example.domain.transport.TransportType
import com.example.hardware.HardwareCapabilityDetector

data class ResolvedTransportResult(
    val transport: RemoteTransport?,
    val status: ConnectionStatus,
    val userMessage: String
)

class RemoteRouter(private val context: Context) {

    private val detector = HardwareCapabilityDetector(context)
    private val irTransport = IrTransport(context)
    private val wifiTransport = WifiTransport(context)
    private val bluetoothTransport = BluetoothTransport(context, TransportType.BLUETOOTH)
    private val bleTransport = BluetoothTransport(context, TransportType.BLE)
    private val externalIrTransport = ExternalIrTransport(context)

    fun resolveTransport(device: RemoteDevice): ResolvedTransportResult {
        val hw = detector.detectCapabilities()

        // 1. Manual transport preference override if specified by user
        device.preferredTransport?.let { pref ->
            val transport = getTransportForType(pref)
            if (transport != null && transport.isAvailable()) {
                val status = getStatusForType(pref)
                return ResolvedTransportResult(transport, status, "Using preferred transport: ${pref.displayName}")
            }
        }

        val brandLower = device.brand.lowercase()
        val protocolLower = device.protocolId.lowercase()
        val isSmartTv = device.deviceType == DeviceType.TV && (
                brandLower.contains("samsung") || brandLower.contains("lg") ||
                        brandLower.contains("android") || brandLower.contains("google") ||
                        brandLower.contains("roku") || brandLower.contains("smart") ||
                        protocolLower in listOf("tizen", "webos", "android_tv", "roku")
                ) || device.deviceType == DeviceType.MEDIA_PLAYER

        // 2. Transport Priority Order
        val priorityList: List<TransportType> = when {
            isSmartTv -> listOf(TransportType.WIFI, TransportType.BLUETOOTH, TransportType.IR, TransportType.EXTERNAL_IR)
            device.deviceType == DeviceType.AC -> listOf(TransportType.IR, TransportType.EXTERNAL_IR, TransportType.WIFI)
            device.deviceType == DeviceType.SOUNDBAR -> listOf(TransportType.BLUETOOTH, TransportType.WIFI, TransportType.IR, TransportType.EXTERNAL_IR)
            device.ipAddress != null -> listOf(TransportType.WIFI, TransportType.IR, TransportType.BLUETOOTH, TransportType.EXTERNAL_IR)
            device.bluetoothAddress != null -> listOf(TransportType.BLUETOOTH, TransportType.BLE, TransportType.IR)
            else -> listOf(TransportType.IR, TransportType.EXTERNAL_IR, TransportType.WIFI, TransportType.BLUETOOTH)
        }

        for (type in priorityList) {
            val transport = getTransportForType(type) ?: continue
            if (transport.isAvailable()) {
                // Verify device actually supports this transport
                if (isTransportCompatibleWithDevice(type, device)) {
                    val status = getStatusForType(type)
                    return ResolvedTransportResult(transport, status, "Ready via ${type.displayName}")
                }
            }
        }

        // 3. No fallback available - Return explicit hardware limitation notice
        return if (!hw.hasIrEmitter && !isSmartTv && device.ipAddress == null && device.bluetoothAddress == null) {
            ResolvedTransportResult(
                transport = null,
                status = ConnectionStatus.IR_UNAVAILABLE,
                userMessage = "This ${device.deviceType.displayName} requires infrared control. Your phone does not have an IR transmitter. Use a compatible external IR adapter."
            )
        } else if (!hw.isWifiEnabled && (isSmartTv || device.ipAddress != null)) {
            ResolvedTransportResult(
                transport = null,
                status = ConnectionStatus.OFFLINE,
                userMessage = "Wi-Fi is turned off. Please enable Wi-Fi to control this device."
            )
        } else {
            ResolvedTransportResult(
                transport = null,
                status = ConnectionStatus.UNSUPPORTED,
                userMessage = "No compatible control transport found for this device on your phone."
            )
        }
    }

    private fun isTransportCompatibleWithDevice(type: TransportType, device: RemoteDevice): Boolean {
        return when (type) {
            TransportType.WIFI -> device.ipAddress != null || device.connectionType == com.example.domain.model.ConnectionType.WIFI || device.connectionType == com.example.domain.model.ConnectionType.HYBRID
            TransportType.BLUETOOTH, TransportType.BLE -> device.bluetoothAddress != null || device.connectionType == com.example.domain.model.ConnectionType.BLUETOOTH
            TransportType.IR, TransportType.EXTERNAL_IR -> true
        }
    }

    private fun getTransportForType(type: TransportType): RemoteTransport? {
        return when (type) {
            TransportType.IR -> irTransport
            TransportType.WIFI -> wifiTransport
            TransportType.BLUETOOTH -> bluetoothTransport
            TransportType.BLE -> bleTransport
            TransportType.EXTERNAL_IR -> externalIrTransport
        }
    }

    private fun getStatusForType(type: TransportType): ConnectionStatus {
        return when (type) {
            TransportType.IR -> ConnectionStatus.READY_IR
            TransportType.WIFI -> ConnectionStatus.CONNECTED_WIFI
            TransportType.BLUETOOTH, TransportType.BLE -> ConnectionStatus.CONNECTED_BLUETOOTH
            TransportType.EXTERNAL_IR -> ConnectionStatus.READY_EXTERNAL_IR
        }
    }
}

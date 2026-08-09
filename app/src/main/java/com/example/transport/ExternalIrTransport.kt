package com.example.transport

import android.content.Context
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.ConnectionStatus
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.RemoteTransport
import com.example.domain.transport.TransportCapability
import com.example.domain.transport.TransportType

class ExternalIrTransport(
    private val context: Context
) : RemoteTransport {

    override val type: TransportType = TransportType.EXTERNAL_IR
    private var isConnected = false
    private var isKnownProtocol = true

    override suspend fun connect(device: RemoteDevice): Result<Unit> {
        // External IR adapter connection check
        if (!isKnownProtocol) {
            return Result.failure(Exception("External IR adapter detected, but its protocol is unsupported."))
        }
        isConnected = true
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun send(command: RemoteCommand): Result<Unit> {
        if (!isKnownProtocol) {
            return Result.failure(Exception("External IR adapter detected, but its protocol is unsupported."))
        }
        return Result.success(Unit)
    }

    override fun isAvailable(): Boolean {
        return false // Checked dynamically by HardwareCapabilityDetector
    }

    override fun capabilities(): Set<TransportCapability> {
        return setOf(
            TransportCapability.POWER,
            TransportCapability.VOLUME,
            TransportCapability.CHANNEL,
            TransportCapability.NAVIGATION,
            TransportCapability.SOURCE,
            TransportCapability.TEMPERATURE,
            TransportCapability.FAN,
            TransportCapability.SWING
        )
    }
}

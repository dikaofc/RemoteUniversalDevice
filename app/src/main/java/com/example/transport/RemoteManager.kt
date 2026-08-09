package com.example.transport

import android.content.Context
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.ConnectionStatus
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.RemoteTransport
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

class RemoteManager(private val context: Context) {

    private val resolver = RemoteRouter(context)
    private val activeConnections = ConcurrentHashMap<String, RemoteTransport>()

    suspend fun getOrConnectTransport(device: RemoteDevice): Pair<RemoteTransport?, ConnectionStatus> {
        val existing = activeConnections[device.id]
        if (existing != null && existing.isAvailable()) {
            val status = when (existing.type) {
                com.example.domain.transport.TransportType.WIFI -> ConnectionStatus.CONNECTED_WIFI
                com.example.domain.transport.TransportType.BLUETOOTH, com.example.domain.transport.TransportType.BLE -> ConnectionStatus.CONNECTED_BLUETOOTH
                com.example.domain.transport.TransportType.IR -> ConnectionStatus.READY_IR
                com.example.domain.transport.TransportType.EXTERNAL_IR -> ConnectionStatus.READY_EXTERNAL_IR
            }
            return Pair(existing, status)
        }

        val resolved = resolver.resolveTransport(device)
        val transport = resolved.transport ?: return Pair(null, resolved.status)

        val connectResult = transport.connect(device)
        return if (connectResult.isSuccess) {
            activeConnections[device.id] = transport
            Pair(transport, resolved.status)
        } else {
            Pair(null, ConnectionStatus.OFFLINE)
        }
    }

    suspend fun sendCommand(device: RemoteDevice, command: RemoteCommand): Result<Unit> {
        val (transport, status) = getOrConnectTransport(device)
        if (transport == null) {
            return Result.failure(Exception("Cannot send command: $status"))
        }

        val result = transport.send(command)
        if (result.isFailure && transport.type == com.example.domain.transport.TransportType.WIFI) {
            // Reconnect with exponential backoff (1s, 2s, 4s, 8s max)
            var backoff = 1000L
            val maxBackoff = 8000L
            repeat(3) {
                delay(backoff)
                val retryConn = transport.connect(device)
                if (retryConn.isSuccess) {
                    return transport.send(command)
                }
                backoff = (backoff * 2).coerceAtMost(maxBackoff)
            }
        }
        return result
    }

    suspend fun disconnectDevice(deviceId: String) {
        activeConnections[deviceId]?.disconnect()
        activeConnections.remove(deviceId)
    }
}

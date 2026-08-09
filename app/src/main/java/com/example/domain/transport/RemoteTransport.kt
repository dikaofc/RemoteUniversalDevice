package com.example.domain.transport

import com.example.domain.model.RemoteDevice

interface RemoteTransport {
    val type: TransportType

    suspend fun connect(device: RemoteDevice): Result<Unit>
    suspend fun disconnect()
    suspend fun send(command: RemoteCommand): Result<Unit>
    fun isAvailable(): Boolean
    fun capabilities(): Set<TransportCapability>
}

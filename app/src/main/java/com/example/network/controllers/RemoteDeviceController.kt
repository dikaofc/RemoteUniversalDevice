package com.example.network.controllers

import com.example.domain.model.DeviceCapability

interface RemoteDeviceController {
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun send(commandKey: String): Boolean
    fun capabilities(): Set<DeviceCapability>
}

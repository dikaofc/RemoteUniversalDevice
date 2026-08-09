package com.example.network.controllers

import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AndroidTvController(
    private val device: RemoteDevice,
    private val client: OkHttpClient
) : RemoteDeviceController {

    override suspend fun connect(): Boolean {
        return device.ipAddress != null
    }

    override suspend fun disconnect() {}

    override suspend fun send(commandKey: String): Boolean = withContext(Dispatchers.IO) {
        val ip = device.ipAddress ?: return@withContext false
        val port = device.port ?: 8008
        val url = "http://$ip:$port/apps/AndroidTv"

        val jsonPayload = """{"command": "$commandKey"}"""
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
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

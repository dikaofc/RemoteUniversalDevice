package com.example.network.controllers

import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class RokuController(
    private val device: RemoteDevice,
    private val client: OkHttpClient
) : RemoteDeviceController {

    override suspend fun connect(): Boolean {
        return device.ipAddress != null
    }

    override suspend fun disconnect() {}

    override suspend fun send(commandKey: String): Boolean = withContext(Dispatchers.IO) {
        val ip = device.ipAddress ?: return@withContext false
        val rokuKey = mapToRokuKey(commandKey)
        val url = "http://$ip:8060/keypress/$rokuKey"

        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("text/plain".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
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

    private fun mapToRokuKey(key: String): String {
        return when (key.lowercase()) {
            "power" -> "Power"
            "volume_up", "vol_up" -> "VolumeUp"
            "volume_down", "vol_down" -> "VolumeDown"
            "mute" -> "VolumeMute"
            "up" -> "Up"
            "down" -> "Down"
            "left" -> "Left"
            "right" -> "Right"
            "ok", "enter" -> "Select"
            "home" -> "Home"
            "back" -> "Back"
            "play", "pause" -> "Play"
            "stop" -> "Stop"
            else -> "Home"
        }
    }
}

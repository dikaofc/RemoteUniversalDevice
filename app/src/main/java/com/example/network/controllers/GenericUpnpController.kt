package com.example.network.controllers

import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class GenericUpnpController(
    private val device: RemoteDevice,
    private val client: OkHttpClient
) : RemoteDeviceController {

    override suspend fun connect(): Boolean {
        return device.ipAddress != null
    }

    override suspend fun disconnect() {}

    override suspend fun send(commandKey: String): Boolean = withContext(Dispatchers.IO) {
        val ip = device.ipAddress ?: return@withContext false
        val port = device.port ?: 8080
        val soapAction = getSoapAction(commandKey) ?: return@withContext false
        val soapBody = getSoapBody(commandKey) ?: return@withContext false

        val url = "http://$ip:$port/upnp/control/RenderingControl"
        val request = Request.Builder()
            .url(url)
            .addHeader("SOAPACTION", soapAction)
            .post(soapBody.toRequestBody("text/xml; charset=\"utf-8\"".toMediaType()))
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
            DeviceCapability.MEDIA_PLAYBACK
        )
    }

    private fun getSoapAction(key: String): String? {
        return when (key.lowercase()) {
            "volume_up", "volume_down", "mute" -> "\"urn:schemas-upnp-org:service:RenderingControl:1#SetVolume\""
            "play", "pause", "stop" -> "\"urn:schemas-upnp-org:service:AVTransport:1#Play\""
            else -> null
        }
    }

    private fun getSoapBody(key: String): String? {
        return when (key.lowercase()) {
            "play" -> """<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:Play xmlns:u="urn:schemas-upnp-org:service:AVTransport:1"><InstanceID>0</InstanceID><Speed>1</Speed></u:Play></s:Body></s:Envelope>"""
            "pause" -> """<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:Pause xmlns:u="urn:schemas-upnp-org:service:AVTransport:1"><InstanceID>0</InstanceID></u:Pause></s:Body></s:Envelope>"""
            "stop" -> """<?xml version="1.0"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><s:Body><u:Stop xmlns:u="urn:schemas-upnp-org:service:AVTransport:1"><InstanceID>0</InstanceID></u:Stop></s:Body></s:Envelope>"""
            else -> null
        }
    }
}

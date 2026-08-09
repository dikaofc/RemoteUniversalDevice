package com.example.network.controllers

import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class LgWebOsController(
    private val device: RemoteDevice,
    private val client: OkHttpClient
) : RemoteDeviceController {

    private var webSocket: WebSocket? = null
    private var isConnected = false

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        val ip = device.ipAddress ?: return@withContext false

        // Simulation for demo IP
        if (ip == "192.168.1.120") {
            isConnected = true
            return@withContext true
        }

        val url = "ws://$ip:3000/"
        val request = Request.Builder().url(url).build()
        val connectionWait = kotlinx.coroutines.CompletableDeferred<Boolean>()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: okhttp3.Response) {
                isConnected = true
                sendHandshake(ws)
                connectionWait.complete(true)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: okhttp3.Response?) {
                isConnected = false
                connectionWait.complete(false)
            }
        })

        try {
            kotlinx.coroutines.withTimeout(5000) {
                connectionWait.await()
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun sendHandshake(ws: WebSocket) {
        val handshake = """
            {
                "type": "register",
                "id": "register_0",
                "payload": {
                    "forcePairing": false,
                    "pairingType": "PROMPT",
                    "manifest": {
                        "manifestVersion": 1,
                        "appVersion": "1.0.0",
                        "signed": {
                            "created": "2026-08-09",
                            "app_name": "Dika Remote",
                            "localizedAppNames": { "": "Dika Remote" },
                            "vendor": "DikaCode",
                            "permissions": ["CONTROL_POWER", "READ_INSTALLED_APPS", "CONTROL_INPUT_TEXT"]
                        }
                    }
                }
            }
        """.trimIndent()
        ws.send(handshake)
    }

    override suspend fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        webSocket = null
        isConnected = false
    }

    override suspend fun send(commandKey: String): Boolean = withContext(Dispatchers.IO) {
        val uri = mapToWebOsUri(commandKey)
        val payload = """
            {
                "id": "cmd_${System.currentTimeMillis()}",
                "type": "request",
                "uri": "$uri"
            }
        """.trimIndent()

        webSocket?.send(payload) ?: false
    }

    override fun capabilities(): Set<DeviceCapability> {
        return setOf(
            DeviceCapability.POWER,
            DeviceCapability.VOLUME_CONTROL,
            DeviceCapability.CHANNEL_CONTROL,
            DeviceCapability.MUTE,
            DeviceCapability.DPAD_NAVIGATION,
            DeviceCapability.HOME_BACK,
            DeviceCapability.SOURCE_INPUT,
            DeviceCapability.MEDIA_PLAYBACK
        )
    }

    private fun mapToWebOsUri(key: String): String {
        return when (key.lowercase()) {
            "volume_up", "vol_up" -> "ssap://audio/volumeUp"
            "volume_down", "vol_down" -> "ssap://audio/volumeDown"
            "mute" -> "ssap://audio/setMute"
            "channel_up", "ch_up" -> "ssap://tv/channelUp"
            "channel_down", "ch_down" -> "ssap://tv/channelDown"
            "up" -> "ssap://media.controls/up"
            "down" -> "ssap://media.controls/down"
            "left" -> "ssap://media.controls/left"
            "right" -> "ssap://media.controls/right"
            "ok", "enter" -> "ssap://media.controls/ok"
            "home" -> "ssap://system.launcher/open"
            "back" -> "ssap://system.launcher/close"
            "play" -> "ssap://media.controls/play"
            "pause" -> "ssap://media.controls/pause"
            "power" -> "ssap://system/turnOff"
            else -> "ssap://system.launcher/open"
        }
    }
}

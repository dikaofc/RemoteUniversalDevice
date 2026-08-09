package com.example.network.controllers

import com.example.domain.model.DeviceCapability
import com.example.domain.model.RemoteDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.util.Base64
import java.util.concurrent.TimeUnit

class SamsungTizenController(
    private val device: RemoteDevice,
    private val client: OkHttpClient
) : RemoteDeviceController {

    private var webSocket: WebSocket? = null
    private var isConnected = false

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        val ip = device.ipAddress ?: return@withContext false
        
        // Simulation for demo IP
        if (ip == "192.168.1.105") {
            isConnected = true
            return@withContext true
        }

        val appName = "Dika Remote"
        val encodedName = Base64.encodeToString(appName.toByteArray(), Base64.NO_WRAP)
        val url = "ws://$ip:8001/api/v2/channels/samsung.remote.control?name=$encodedName"

        val request = Request.Builder().url(url).build()
        val connectionWait = kotlinx.coroutines.CompletableDeferred<Boolean>()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: okhttp3.Response) {
                isConnected = true
                connectionWait.complete(true)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: okhttp3.Response?) {
                isConnected = false
                connectionWait.complete(false)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
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

    override suspend fun disconnect() {
        webSocket?.close(1000, "Disconnected by user")
        webSocket = null
        isConnected = false
    }

    override suspend fun send(commandKey: String): Boolean = withContext(Dispatchers.IO) {
        if (commandKey.lowercase() == "power" && !isConnected) {
            // Try Wake-on-LAN if device has MAC address
            device.macAddress?.let { mac ->
                com.example.network.utils.WakeOnLanHelper.sendMagicPacket(mac)
            }
            // Even if no MAC, we try to connect if it's just in network standby
            if (!isConnected) {
                connect()
            }
        }

        val tizenKey = mapToTizenKey(commandKey)
        val payload = """
            {
                "method": "ms.remote.control",
                "params": {
                    "Cmd": "Click",
                    "DataOfCmd": "$tizenKey",
                    "Option": "false",
                    "TypeOfRemote": "SendRemoteKey"
                }
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
            DeviceCapability.MEDIA_PLAYBACK,
            DeviceCapability.NUMPAD
        )
    }

    private fun mapToTizenKey(key: String): String {
        return when (key.lowercase()) {
            "power" -> "KEY_POWER"
            "volume_up", "vol_up" -> "KEY_VOLUP"
            "volume_down", "vol_down" -> "KEY_VOLDOWN"
            "mute" -> "KEY_MUTE"
            "channel_up", "ch_up" -> "KEY_CHUP"
            "channel_down", "ch_down" -> "KEY_CHDOWN"
            "up" -> "KEY_UP"
            "down" -> "KEY_DOWN"
            "left" -> "KEY_LEFT"
            "right" -> "KEY_RIGHT"
            "ok", "enter" -> "KEY_ENTER"
            "home" -> "KEY_HOME"
            "back" -> "KEY_RETURN"
            "source", "input" -> "KEY_SOURCE"
            "play" -> "KEY_PLAY"
            "pause" -> "KEY_PAUSE"
            "stop" -> "KEY_STOP"
            else -> "KEY_HOME"
        }
    }
}

package com.example.transport

import android.content.Context
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.RemoteTransport
import com.example.domain.transport.TransportCapability
import com.example.domain.transport.TransportType
import com.example.hardware.HardwareCapabilityDetector
import com.example.network.controllers.AndroidTvController
import com.example.network.controllers.GenericUpnpController
import com.example.network.controllers.LgWebOsController
import com.example.network.controllers.RemoteDeviceController
import com.example.network.controllers.RokuController
import com.example.network.controllers.SamsungTizenController
import okhttp3.OkHttpClient

class WifiTransport(
    private val context: Context
) : RemoteTransport {

    override val type: TransportType = TransportType.WIFI
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val detector = HardwareCapabilityDetector(context)
    private var activeController: RemoteDeviceController? = null

    override suspend fun connect(device: RemoteDevice): Result<Unit> {
        if (!isAvailable()) {
            return Result.failure(Exception("Wi-Fi network is unavailable or disconnected."))
        }
        
        return try {
            val controller = createController(device)
            activeController = controller
            val success = controller.connect()
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to connect to Wi-Fi device at ${device.ipAddress}"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Connection timed out. Please check if the device is on the same network."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disconnect() {
        activeController?.disconnect()
        activeController = null
    }

    override suspend fun send(command: RemoteCommand): Result<Unit> {
        val controller = activeController ?: return Result.failure(Exception("Wi-Fi controller not connected"))
        
        var attempts = 0
        val maxRetries = 2
        var lastError: Exception? = null

        while (attempts <= maxRetries) {
            try {
                val success = controller.send(command.key)
                if (success) return Result.success(Unit)
                attempts++
            } catch (e: java.net.SocketTimeoutException) {
                lastError = Exception("Network timeout while sending command")
                attempts++
            } catch (e: Exception) {
                lastError = e
                attempts++
            }
            if (attempts <= maxRetries) {
                kotlinx.coroutines.delay(500) // Backoff
            }
        }
        
        return Result.failure(lastError ?: Exception("Command execution failed via Wi-Fi after $maxRetries retries"))
    }

    override fun isAvailable(): Boolean {
        return detector.detectCapabilities().isWifiEnabled
    }

    override fun capabilities(): Set<TransportCapability> {
        return setOf(
            TransportCapability.POWER,
            TransportCapability.VOLUME,
            TransportCapability.CHANNEL,
            TransportCapability.NAVIGATION,
            TransportCapability.SOURCE,
            TransportCapability.MEDIA
        )
    }

    private fun createController(device: RemoteDevice): RemoteDeviceController {
        val brandLower = device.brand.lowercase()
        val protocolLower = device.protocolId.lowercase()
        return when {
            brandLower.contains("samsung") || protocolLower == "tizen" -> SamsungTizenController(device, client)
            brandLower.contains("lg") || protocolLower == "webos" -> LgWebOsController(device, client)
            brandLower.contains("roku") || protocolLower == "roku" -> RokuController(device, client)
            brandLower.contains("upnp") || protocolLower == "upnp" -> GenericUpnpController(device, client)
            else -> AndroidTvController(device, client)
        }
    }
}

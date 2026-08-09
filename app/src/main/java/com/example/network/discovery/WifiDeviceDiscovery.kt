package com.example.network.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.example.domain.model.DeviceType
import com.example.domain.model.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class WifiDeviceDiscovery(private val context: Context) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager

    private val targetServices = listOf(
        "_samsung-remote._tcp.",
        "_webos-second-screen._tcp.",
        "_androidtv._tcp.",
        "_googlecast._tcp.",
        "_roku-ecp._tcp.",
        "_http._tcp."
    )

    fun discoverWifiDevices(timeoutMs: Long = 10000L): Flow<List<DiscoveredDevice>> = callbackFlow {
        val discoveredMap = mutableMapOf<String, DiscoveredDevice>()

        val listeners = mutableListOf<NsdManager.DiscoveryListener>()

        targetServices.forEach { serviceType ->
            val listener = object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    nsdManager?.stopServiceDiscovery(this)
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    nsdManager?.stopServiceDiscovery(this)
                }

                override fun onDiscoveryStarted(serviceType: String) {}

                override fun onDiscoveryStopped(serviceType: String) {}

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    nsdManager?.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            val ip = serviceInfo.host?.hostAddress ?: return
                            val name = serviceInfo.serviceName ?: "Smart Device"
                            val port = serviceInfo.port
                            val brand = detectBrandFromService(serviceInfo.serviceType, name)
                            val deviceType = if (brand == "Roku" || brand == "Apple") DeviceType.MEDIA_PLAYER else DeviceType.TV

                            val device = DiscoveredDevice(
                                id = "wifi_$ip",
                                name = name,
                                brand = brand,
                                deviceType = deviceType,
                                ipAddress = ip,
                                port = port,
                                serviceType = serviceInfo.serviceType
                            )

                            synchronized(discoveredMap) {
                                discoveredMap[ip] = device
                                trySend(discoveredMap.values.toList())
                            }
                        }
                    })
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
            }

            try {
                nsdManager?.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
                listeners.add(listener)
            } catch (e: Exception) {
                // Ignore service registration issues
            }
        }

        // SSDP UPnP multicast scan in parallel
        Thread {
            sendSsdpMulticast { device ->
                synchronized(discoveredMap) {
                    discoveredMap[device.ipAddress] = device
                    trySend(discoveredMap.values.toList())
                }
            }
        }.start()

        awaitClose {
            listeners.forEach { listener ->
                try {
                    nsdManager?.stopServiceDiscovery(listener)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun detectBrandFromService(serviceType: String, name: String): String {
        val nameLower = name.lowercase()
        return when {
            nameLower.contains("samsung") || serviceType.contains("samsung") -> "Samsung"
            nameLower.contains("lg") || serviceType.contains("webos") -> "LG"
            nameLower.contains("android") || serviceType.contains("androidtv") -> "Android TV"
            nameLower.contains("google") || serviceType.contains("googlecast") -> "Google TV"
            nameLower.contains("roku") || serviceType.contains("roku") -> "Roku"
            else -> "Generic Smart TV"
        }
    }

    private fun sendSsdpMulticast(onDeviceFound: (DiscoveredDevice) -> Unit) {
        try {
            val ssdpRequest = "M-SEARCH * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "MAN: \"ssdp:discover\"\r\n" +
                    "MX: 3\r\n" +
                    "ST: ssdp:all\r\n\r\n"

            val socket = DatagramSocket()
            socket.soTimeout = 3000
            val group = InetAddress.getByName("239.255.255.250")
            val packet = DatagramPacket(ssdpRequest.toByteArray(), ssdpRequest.length, group, 1900)
            socket.send(packet)

            val recvBuf = ByteArray(1024)
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 4000) {
                try {
                    val recvPacket = DatagramPacket(recvBuf, recvBuf.size)
                    socket.receive(recvPacket)
                    val ip = recvPacket.address.hostAddress ?: continue
                    val response = String(recvPacket.data, 0, recvPacket.length)
                    if (response.contains("200 OK") || response.contains("LOCATION:")) {
                        val brand = if (response.contains("Samsung")) "Samsung" else if (response.contains("LG")) "LG" else "UPnP Device"
                        val device = DiscoveredDevice(
                            id = "ssdp_$ip",
                            name = "$brand TV",
                            brand = brand,
                            deviceType = DeviceType.TV,
                            ipAddress = ip,
                            port = 8080,
                            serviceType = "ssdp"
                        )
                        onDeviceFound(device)
                    }
                } catch (e: Exception) {
                    break
                }
            }
            socket.close()
        } catch (e: Exception) {
            // Socket error or permission
        }
    }
}

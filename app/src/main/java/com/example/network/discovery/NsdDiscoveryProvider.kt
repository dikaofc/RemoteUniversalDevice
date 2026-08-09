package com.example.network.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.example.domain.model.DeviceType
import com.example.domain.model.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.UUID

interface DeviceDiscoveryProvider {
    suspend fun discover(): List<DiscoveredDevice>
}

class NsdDiscoveryProvider(private val context: Context) : DeviceDiscoveryProvider {

    override suspend fun discover(): List<DiscoveredDevice> = withContext(Dispatchers.IO) {
        val discoveredList = mutableListOf<DiscoveredDevice>()
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager ?: return@withContext emptyList()

        val serviceTypes = listOf(
            "_samsungms._tcp.",
            "_lgsmarttv._tcp.",
            "_androidtv._tcp.",
            "_googlecast._tcp.",
            "_http._tcp.",
            "_roku._tcp.",
            "_spotify-connect._tcp.",
            "_dial._tcp.",
            "_airplay._tcp.",
            "_sonos._tcp."
        )

        val listeners = mutableListOf<NsdManager.DiscoveryListener>()

        serviceTypes.forEach { serviceType ->
            val listener = object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                    android.util.Log.e("DIKA_REMOTE", "Discovery failed for $serviceType: error $errorCode")
                }
                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {}
                override fun onDiscoveryStarted(serviceType: String?) {
                    android.util.Log.d("DIKA_REMOTE", "Discovery started for $serviceType")
                }
                override fun onDiscoveryStopped(serviceType: String?) {}

                override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                    android.util.Log.d("DIKA_REMOTE", "Service found: ${serviceInfo?.serviceName} type: ${serviceInfo?.serviceType}")
                    if (serviceInfo != null) {
                        try {
                            nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                                    android.util.Log.e("DIKA_REMOTE", "Resolve failed for ${serviceInfo?.serviceName}: error $errorCode")
                                }
                                override fun onServiceResolved(info: NsdServiceInfo?) {
                                    android.util.Log.d("DIKA_REMOTE", "Service resolved: ${info?.serviceName} at ${info?.host}")
                                    if (info != null) {
                                        val host = info.host?.hostAddress ?: return
                                        val name = info.serviceName ?: "Smart Device"
                                        val brand = when {
                                            name.contains("samsung", ignoreCase = true) || info.serviceType.contains("samsung") -> "Samsung"
                                            name.contains("lg", ignoreCase = true) || info.serviceType.contains("lg") -> "LG"
                                            name.contains("android", ignoreCase = true) -> "Android TV"
                                            name.contains("chromecast", ignoreCase = true) -> "Google"
                                            else -> "Generic Smart TV"
                                        }

                                        val dev = DiscoveredDevice(
                                            id = UUID.randomUUID().toString(),
                                            name = name,
                                            brand = brand,
                                            deviceType = DeviceType.TV,
                                            ipAddress = host,
                                            port = info.port,
                                            serviceType = info.serviceType
                                        )
                                        synchronized(discoveredList) {
                                            if (discoveredList.none { it.ipAddress == host }) {
                                                discoveredList.add(dev)
                                            }
                                        }
                                    }
                                }
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo?) {}
            }

            try {
                nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
                listeners.add(listener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Also run SSDP search for UPnP Smart TVs
        try {
            val ssdpDevices = runSsdpSearch()
            discoveredList.addAll(ssdpDevices)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        delay(3000L) // Scan duration

        // Stop listeners safely
        listeners.forEach { listener ->
            try {
                nsdManager.stopServiceDiscovery(listener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        synchronized(discoveredList) {
            discoveredList.distinctBy { it.ipAddress }
        }
    }

    private fun runSsdpSearch(): List<DiscoveredDevice> {
        val list = mutableListOf<DiscoveredDevice>()
        try {
            val socket = DatagramSocket()
            socket.soTimeout = 1500

            val targets = listOf("ssdp:all", "urn:schemas-upnp-org:device:MediaRenderer:1", "urn:dial-multiscreen-org:service:dial:1")
            targets.forEach { target ->
                val msearch = "M-SEARCH * HTTP/1.1\r\n" +
                        "HOST: 239.255.255.250:1900\r\n" +
                        "MAN: \"ssdp:discover\"\r\n" +
                        "MX: 2\r\n" +
                        "ST: $target\r\n\r\n"

                val sendData = msearch.toByteArray()
                val packet = DatagramPacket(
                    sendData, sendData.size,
                    InetAddress.getByName("239.255.255.250"), 1900
                )
                socket.send(packet)
            }

            val receiveBuf = ByteArray(1024)
            val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 1500) {
                try {
                    socket.receive(receivePacket)
                    val response = String(receivePacket.data, 0, receivePacket.length)
                    val ip = receivePacket.address.hostAddress ?: continue
                    var brand = "UPnP Smart TV"
                    if (response.contains("samsung", ignoreCase = true)) brand = "Samsung"
                    if (response.contains("lg", ignoreCase = true)) brand = "LG"
                    if (response.contains("sony", ignoreCase = true)) brand = "Sony"

                    list.add(
                        DiscoveredDevice(
                            id = UUID.randomUUID().toString(),
                            name = "$brand TV ($ip)",
                            brand = brand,
                            deviceType = DeviceType.TV,
                            ipAddress = ip,
                            port = 8001,
                            serviceType = "ssdp"
                        )
                    )
                } catch (e: Exception) {
                    break
                }
            }
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}

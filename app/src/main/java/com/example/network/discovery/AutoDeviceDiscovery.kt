package com.example.network.discovery

import android.content.Context
import com.example.domain.model.DeviceType
import com.example.domain.model.DiscoveredDevice
import com.example.domain.transport.TransportType
import com.example.hardware.HardwareCapabilityDetector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class AutoScanResult(
    val device: DiscoveredDevice,
    val transportType: TransportType,
    val isSupportedOnThisPhone: Boolean,
    val statusMessage: String
)

class AutoDeviceDiscovery(private val context: Context) {

    private val wifiDiscovery = WifiDeviceDiscovery(context)
    private val btDiscovery = BluetoothDeviceDiscovery(context)
    private val detector = HardwareCapabilityDetector(context)

    fun startParallelScan(): Flow<List<AutoScanResult>> {
        val hw = detector.detectCapabilities()

        val wifiFlow = wifiDiscovery.discoverWifiDevices()
        val btFlow = btDiscovery.discoverBluetoothDevices()

        return combine(wifiFlow, btFlow) { wifiList, btList ->
            val results = mutableListOf<AutoScanResult>()

            // 1. Wi-Fi Discovered Devices
            wifiList.forEach { dev ->
                results.add(
                    AutoScanResult(
                        device = dev,
                        transportType = TransportType.WIFI,
                        isSupportedOnThisPhone = hw.isWifiEnabled,
                        statusMessage = if (hw.isWifiEnabled) "Wi-Fi - Found" else "Wi-Fi disabled"
                    )
                )
            }

            // 2. Bluetooth Discovered Devices
            btList.forEach { dev ->
                results.add(
                    AutoScanResult(
                        device = dev,
                        transportType = TransportType.BLUETOOTH,
                        isSupportedOnThisPhone = hw.isBluetoothEnabled,
                        statusMessage = if (hw.isBluetoothEnabled) "Bluetooth - Found" else "Bluetooth disabled"
                    )
                )
            }

            // 3. Preset Standard IR-only Devices
            val irAc = DiscoveredDevice(
                id = "preset_ac_ir",
                name = "Standard Air Conditioner (AC)",
                brand = "Generic AC",
                deviceType = DeviceType.AC,
                ipAddress = "",
                port = 0,
                serviceType = "ir"
            )

            if (hw.hasIrEmitter) {
                results.add(
                    AutoScanResult(
                        device = irAc,
                        transportType = TransportType.IR,
                        isSupportedOnThisPhone = true,
                        statusMessage = "IR required - Ready via built-in IR"
                    )
                )
            } else {
                results.add(
                    AutoScanResult(
                        device = irAc,
                        transportType = TransportType.IR,
                        isSupportedOnThisPhone = false,
                        statusMessage = "IR required - Not available on this phone"
                    )
                )
            }

            results
        }
    }
}

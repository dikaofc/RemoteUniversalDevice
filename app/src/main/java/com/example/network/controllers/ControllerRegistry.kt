package com.example.network.controllers

import android.content.Context
import com.example.domain.model.ConnectionType
import com.example.domain.model.RemoteDevice
import com.example.ir.ConsumerIrTransmitter
import com.example.ir.IrProtocol
import com.example.ir.protocols.NecProtocol
import com.example.ir.protocols.PanasonicProtocol
import com.example.ir.protocols.Rc5Protocol
import com.example.ir.protocols.SamsungProtocol
import com.example.ir.protocols.SonySircProtocol
import okhttp3.OkHttpClient

class ControllerRegistry(private val context: Context) {

    private val httpClient = OkHttpClient.Builder().build()
    private val irTransmitter = ConsumerIrTransmitter(context)

    private val protocolMap: Map<String, IrProtocol> = mapOf(
        "nec" to NecProtocol(),
        "samsung" to SamsungProtocol(),
        "sony" to SonySircProtocol(),
        "rc5" to Rc5Protocol(),
        "panasonic" to PanasonicProtocol()
    )

    fun getController(device: RemoteDevice): RemoteDeviceController {
        return when (device.connectionType) {
            ConnectionType.WIFI -> {
                val brandLower = device.brand.lowercase()
                when {
                    brandLower.contains("samsung") || device.protocolId == "tizen" -> SamsungTizenController(device, httpClient)
                    brandLower.contains("lg") || device.protocolId == "webos" -> LgWebOsController(device, httpClient)
                    brandLower.contains("android") || brandLower.contains("google") || device.protocolId == "android_tv" -> AndroidTvController(device, httpClient)
                    else -> IrDeviceController(device, irTransmitter, protocolMap) // Fallback to IR if network adapter unlisted
                }
            }
            ConnectionType.BLUETOOTH -> BluetoothDeviceController(context, device)
            else -> IrDeviceController(device, irTransmitter, protocolMap)
        }
    }
}

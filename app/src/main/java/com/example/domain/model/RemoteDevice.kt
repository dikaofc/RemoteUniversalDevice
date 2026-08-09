package com.example.domain.model

import com.example.domain.transport.TransportType

data class RemoteDevice(
    val id: String,
    val name: String,
    val brand: String,
    val model: String = "Generic",
    val deviceType: DeviceType,
    val connectionType: ConnectionType,
    val protocolId: String, // e.g. "samsung", "nec", "sony", "lg_webos", "tizen"
    val roomId: String = "default_room",
    val isFavorite: Boolean = false,
    val ipAddress: String? = null,
    val port: Int? = null,
    val macAddress: String? = null,
    val bluetoothAddress: String? = null,
    val carrierFrequency: Int = 38000,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val commandMapJson: String = "{}", // Json mapping command keys to code values or protocol params
    val acStateJson: String? = null,
    val transports: List<TransportType> = emptyList(),
    val preferredTransport: TransportType? = null,
    val controllerClass: String? = null,
    val capabilities: DeviceCapabilities? = null,
    val createdAt: Long = System.currentTimeMillis()
)


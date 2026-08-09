package com.example.domain.model

data class RoomProfile(
    val id: String,
    val name: String,
    val icon: String = "home"
)

data class DiscoveredDevice(
    val id: String,
    val name: String,
    val brand: String,
    val deviceType: DeviceType,
    val ipAddress: String,
    val port: Int = 80,
    val serviceType: String, // e.g. "samsung_tizen", "lg_webos", "android_tv", "ssdp_device"
    val macAddress: String? = null
)

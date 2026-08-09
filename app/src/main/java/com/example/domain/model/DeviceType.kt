package com.example.domain.model

enum class DeviceType(val displayName: String) {
    TV("TV / Smart TV"),
    AC("Air Conditioner (AC)"),
    STB("Set-Top Box / Receiver"),
    MEDIA_PLAYER("Media Player / Streaming"),
    PROJECTOR("Projector"),
    SOUNDBAR("Soundbar / Audio"),
    FAN("Electric Fan"),
    CUSTOM("Custom Device")
}

enum class ConnectionType(val displayName: String) {
    IR("Infrared (IR)"),
    WIFI("Wi-Fi / LAN"),
    BLUETOOTH("Bluetooth"),
    HYBRID("Hybrid (Wi-Fi + IR)")
}

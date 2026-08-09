package com.example.domain.transport

enum class TransportType(val displayName: String) {
    IR("Infrared (IR)"),
    WIFI("Wi-Fi / LAN"),
    BLUETOOTH("Bluetooth Classic"),
    BLE("Bluetooth Low Energy"),
    EXTERNAL_IR("External IR Adapter")
}

enum class TransportCapability {
    POWER,
    VOLUME,
    CHANNEL,
    NAVIGATION,
    SOURCE,
    MEDIA,
    TEMPERATURE,
    FAN,
    SWING
}

enum class ConnectionStatus(val label: String) {
    CONNECTED_WIFI("Wi-Fi"),
    CONNECTED_BLUETOOTH("Bluetooth"),
    READY_IR("IR Mode"),
    READY_EXTERNAL_IR("External Hub"),
    IR_UNAVAILABLE("No IR Blaster"),
    OFFLINE("Offline"),
    PAIRING_REQUIRED("Needs Pairing"),
    UNSUPPORTED("Not Supported")
}

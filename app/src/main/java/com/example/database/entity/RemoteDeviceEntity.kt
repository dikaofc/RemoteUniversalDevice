package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_devices")
data class RemoteDeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val model: String,
    val deviceType: String,
    val connectionType: String,
    val protocolId: String,
    val roomId: String = "default_room",
    val isFavorite: Boolean = false,
    val ipAddress: String? = null,
    val port: Int? = null,
    val macAddress: String? = null,
    val bluetoothAddress: String? = null,
    val carrierFrequency: Int = 38000,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val commandMapJson: String = "{}",
    val acStateJson: String? = null,
    val transportsJson: String = "[]",
    val preferredTransport: String? = null,
    val controllerClass: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ir_codes")
data class IrCodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brand: String,
    val deviceType: String,
    val model: String,
    val protocol: String,
    val carrierFrequency: Int = 38000,
    val commandKey: String,
    val address: Int,
    val commandCode: Int
)

@Entity(tableName = "custom_buttons")
data class CustomButtonEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val label: String,
    val iconName: String,
    val commandKey: String,
    val posX: Int,
    val posY: Int,
    val widthDp: Int,
    val heightDp: Int
)

@Entity(tableName = "macros")
data class MacroEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val stepsJson: String
)

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String
)

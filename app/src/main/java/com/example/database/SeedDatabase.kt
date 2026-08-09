package com.example.database

import com.example.database.entity.IrCodeEntity
import com.example.database.entity.RemoteDeviceEntity
import com.example.database.entity.RoomEntity

object SeedDatabase {

    val defaultRooms = listOf(
        RoomEntity("room_living", "Living Room", "tv"),
        RoomEntity("room_bedroom", "Master Bedroom", "bed"),
        RoomEntity("room_office", "Office / Work Room", "laptop"),
        RoomEntity("room_kitchen", "Kitchen", "kitchen")
    )

    val defaultDevices = emptyList<RemoteDeviceEntity>()

    val supportedBrands = listOf(
        "Samsung", "LG", "Sony", "Panasonic", "Philips", "TCL", "Hisense", "Sharp", "Toshiba",
        "AQUOS", "Xiaomi", "MI", "Realme", "Coocaa", "Changhong", "Polytron", "Aqua", "Sanken",
        "Akari", "Nex", "Konka", "Skyworth", "Haier", "Hitachi", "JVC", "Daewoo", "Pioneer", "JBL",
        "Vizio", "Insignia", "RCA", "Sceptre", "Westinghouse", "Grundig", "Thomson", "Loewe", "Metz",
        "Telefunken", "Blaupunkt", "Dahua", "AOC", "BenQ", "Epson", "ViewSonic", "NEC", "Mitsubishi",
        "Sharp Indonesia", "TCL Indonesia", "Coocaa Indonesia", "Xiaomi Indonesia", "Miyako", "Niko",
        "Mito", "WCOM", "Advan", "Axioo", "Daikin", "Gree", "Midea", "Carrier", "AUX", "Whirlpool",
        "York", "McQuay", "Trane"
    )

    fun getInitialIrCodes(): List<IrCodeEntity> {
        val list = mutableListOf<IrCodeEntity>()
        val brands = listOf("Samsung", "LG", "Sony", "Panasonic", "Philips", "TCL", "Hisense", "Sharp", "Toshiba", "Xiaomi", "Polytron", "Coocaa")
        val commands = listOf(
            "power" to 0x02,
            "volume_up" to 0x07,
            "volume_down" to 0x0B,
            "mute" to 0x0F,
            "channel_up" to 0x12,
            "channel_down" to 0x13,
            "up" to 0x1A,
            "down" to 0x1B,
            "left" to 0x1C,
            "right" to 0x1D,
            "ok" to 0x25,
            "home" to 0x30,
            "back" to 0x31,
            "source" to 0x35
        )

        brands.forEach { brand ->
            commands.forEach { (cmd, code) ->
                list.add(
                    IrCodeEntity(
                        brand = brand,
                        deviceType = "TV",
                        model = "generic",
                        protocol = if (brand == "Samsung") "samsung" else "nec",
                        carrierFrequency = 38000,
                        commandKey = cmd,
                        address = 0x07,
                        commandCode = code
                    )
                )
            }
        }
        return list
    }
}

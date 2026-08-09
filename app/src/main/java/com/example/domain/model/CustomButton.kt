package com.example.domain.model

data class CustomButton(
    val id: String,
    val deviceId: String,
    val label: String,
    val iconName: String, // e.g. "power", "volume_up", "tv", "light", "star"
    val commandKey: String, // mapped command or macro ID
    val posX: Int = 0,
    val posY: Int = 0,
    val widthDp: Int = 80,
    val heightDp: Int = 60
)

package com.example.domain.model

enum class AcMode(val label: String) {
    COOL("Cool"),
    HEAT("Heat"),
    DRY("Dry"),
    FAN("Fan"),
    AUTO("Auto")
}

enum class FanSpeed(val label: String) {
    AUTO("Auto"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    TURBO("Turbo")
}

data class AirConditionerState(
    val power: Boolean = true,
    val mode: AcMode = AcMode.COOL,
    val temperature: Int = 24,
    val fanSpeed: FanSpeed = FanSpeed.AUTO,
    val swing: Boolean = false,
    val turbo: Boolean = false,
    val eco: Boolean = false,
    val sleep: Boolean = false
)

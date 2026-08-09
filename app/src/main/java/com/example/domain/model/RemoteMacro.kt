package com.example.domain.model

data class MacroStep(
    val deviceId: String,
    val commandKey: String,
    val delayMs: Long = 500L,
    val repeatCount: Int = 1
)

data class RemoteMacro(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String = "bolt",
    val steps: List<MacroStep> = emptyList()
)

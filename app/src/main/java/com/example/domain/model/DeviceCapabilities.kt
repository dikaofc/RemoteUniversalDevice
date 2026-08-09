package com.example.domain.model

import com.example.domain.transport.TransportType

data class DeviceCapabilities(
    val power: Boolean = true,
    val volume: Boolean = false,
    val channel: Boolean = false,
    val navigation: Boolean = false,
    val source: Boolean = false,
    val media: Boolean = false,
    val temperature: Boolean = false,
    val fan: Boolean = false,
    val swing: Boolean = false,
    val transports: Set<TransportType> = emptySet()
)

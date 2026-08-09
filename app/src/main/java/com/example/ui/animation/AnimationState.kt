package com.example.ui.animation

import androidx.compose.runtime.*

/**
 * State container for managing real-time button interaction states,
 * haptic synchronization triggers, error shakes, and transition states.
 */
@Stable
class RemoteButtonAnimationState {
    var isPressed by mutableStateOf(false)
        private set

    var errorTriggerCount by mutableStateOf(0)
        private set

    fun onPress() {
        isPressed = true
    }

    fun onRelease() {
        isPressed = false
    }

    fun triggerErrorShake() {
        errorTriggerCount++
    }
}

@Composable
fun rememberRemoteButtonAnimationState(): RemoteButtonAnimationState {
    return remember { RemoteButtonAnimationState() }
}

/**
 * Encapsulates status transition states (e.g. Disconnected -> Connecting -> Connected)
 */
enum class DeviceConnectionAnimationStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

@Stable
class ConnectionAnimationState(initialStatus: DeviceConnectionAnimationStatus = DeviceConnectionAnimationStatus.DISCONNECTED) {
    var status by mutableStateOf(initialStatus)
}

@Composable
fun rememberConnectionAnimationState(initialStatus: DeviceConnectionAnimationStatus = DeviceConnectionAnimationStatus.DISCONNECTED): ConnectionAnimationState {
    return remember(initialStatus) { ConnectionAnimationState(initialStatus) }
}

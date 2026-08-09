package com.example.domain.transport

sealed class RemoteCommand(val key: String) {
    object Power : RemoteCommand("POWER")
    object PowerOn : RemoteCommand("POWER_ON")
    object PowerOff : RemoteCommand("POWER_OFF")
    object VolumeUp : RemoteCommand("VOLUME_UP")
    object VolumeDown : RemoteCommand("VOLUME_DOWN")
    object Mute : RemoteCommand("MUTE")
    object ChannelUp : RemoteCommand("CHANNEL_UP")
    object ChannelDown : RemoteCommand("CHANNEL_DOWN")
    object Up : RemoteCommand("UP")
    object Down : RemoteCommand("DOWN")
    object Left : RemoteCommand("LEFT")
    object Right : RemoteCommand("RIGHT")
    object Ok : RemoteCommand("OK")
    object Back : RemoteCommand("BACK")
    object Home : RemoteCommand("HOME")
    object Menu : RemoteCommand("MENU")
    object Source : RemoteCommand("SOURCE")
    object Play : RemoteCommand("PLAY")
    object Pause : RemoteCommand("PAUSE")
    object Stop : RemoteCommand("STOP")
    object Rewind : RemoteCommand("REWIND")
    object FastForward : RemoteCommand("FAST_FORWARD")

    // AC Commands
    object TempUp : RemoteCommand("TEMP_UP")
    object TempDown : RemoteCommand("TEMP_DOWN")
    data class SetTemp(val temperature: Int) : RemoteCommand("SET_TEMP_$temperature")
    data class Mode(val modeName: String) : RemoteCommand("MODE_$modeName")
    data class FanSpeed(val speed: String) : RemoteCommand("FAN_$speed")
    data class Swing(val swingMode: String) : RemoteCommand("SWING_$swingMode")

    data class Custom(val commandKey: String) : RemoteCommand(commandKey)
}

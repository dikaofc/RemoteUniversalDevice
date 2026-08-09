package com.example.network.controllers

import com.example.domain.model.AirConditionerState
import com.example.domain.model.DeviceCapability
import com.example.domain.model.DeviceType
import com.example.domain.model.IrCommand
import com.example.domain.model.RemoteDevice
import com.example.ir.IrProtocol
import com.example.ir.IrTransmitter
import com.example.ir.protocols.AcProtocolAdapter

class IrDeviceController(
    private val device: RemoteDevice,
    private val transmitter: IrTransmitter,
    private val protocolMap: Map<String, IrProtocol>
) : RemoteDeviceController {

    private val acAdapter = AcProtocolAdapter()

    override suspend fun connect(): Boolean {
        return transmitter.isAvailable()
    }

    override suspend fun disconnect() {}

    override suspend fun send(commandKey: String): Boolean {
        if (!transmitter.isAvailable()) return false

        // Check if AC stateful send
        if (device.deviceType == DeviceType.AC) {
            val acState = parseAcState(device.acStateJson)
            val signal = acAdapter.generateAcSignal(device.brand, acState)
            transmitter.transmit(signal.carrierFrequency, signal.pattern)
            return true
        }

        // Standard IR protocol lookup
        val protocol = protocolMap[device.protocolId] ?: protocolMap["nec"] ?: return false
        val cmdCode = parseCommandCode(commandKey, device.commandMapJson)
        val irCmd = IrCommand(name = commandKey, address = 0x07, commandCode = cmdCode)

        val signal = protocol.encode(irCmd)
        transmitter.transmit(signal.carrierFrequency, signal.pattern)
        return true
    }

    override fun capabilities(): Set<DeviceCapability> {
        return when (device.deviceType) {
            DeviceType.TV -> setOf(
                DeviceCapability.POWER,
                DeviceCapability.VOLUME_CONTROL,
                DeviceCapability.CHANNEL_CONTROL,
                DeviceCapability.MUTE,
                DeviceCapability.DPAD_NAVIGATION,
                DeviceCapability.HOME_BACK,
                DeviceCapability.SOURCE_INPUT,
                DeviceCapability.NUMPAD,
                DeviceCapability.MEDIA_PLAYBACK
            )
            DeviceType.AC -> setOf(
                DeviceCapability.POWER,
                DeviceCapability.AC_TEMPERATURE,
                DeviceCapability.AC_MODE,
                DeviceCapability.AC_FAN_SPEED,
                DeviceCapability.AC_SWING,
                DeviceCapability.AC_TURBO_ECO
            )
            DeviceType.STB, DeviceType.MEDIA_PLAYER -> setOf(
                DeviceCapability.POWER,
                DeviceCapability.VOLUME_CONTROL,
                DeviceCapability.CHANNEL_CONTROL,
                DeviceCapability.DPAD_NAVIGATION,
                DeviceCapability.HOME_BACK,
                DeviceCapability.MEDIA_PLAYBACK,
                DeviceCapability.NUMPAD
            )
            else -> setOf(
                DeviceCapability.POWER,
                DeviceCapability.VOLUME_CONTROL,
                DeviceCapability.DPAD_NAVIGATION,
                DeviceCapability.HOME_BACK
            )
        }
    }

    private fun parseCommandCode(key: String, jsonMap: String): Int {
        return when (key.lowercase()) {
            "power" -> 0x02
            "volume_up", "vol_up" -> 0x07
            "volume_down", "vol_down" -> 0x0B
            "mute" -> 0x0F
            "channel_up", "ch_up" -> 0x12
            "channel_down", "ch_down" -> 0x13
            "up" -> 0x1A
            "down" -> 0x1B
            "left" -> 0x1C
            "right" -> 0x1D
            "ok", "enter" -> 0x25
            "home" -> 0x30
            "back" -> 0x31
            "source", "input" -> 0x35
            "menu" -> 0x3A
            else -> 0x01
        }
    }

    private fun parseAcState(json: String?): AirConditionerState {
        return AirConditionerState() // default standard state
    }
}

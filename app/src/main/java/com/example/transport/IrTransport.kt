package com.example.transport

import android.content.Context
import com.example.domain.model.RemoteDevice
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.RemoteTransport
import com.example.domain.transport.TransportCapability
import com.example.domain.transport.TransportType
import com.example.hardware.HardwareCapabilityDetector
import com.example.ir.ConsumerIrTransmitter
import com.example.ir.IrProtocol
import com.example.ir.protocols.NecProtocol
import com.example.ir.protocols.PanasonicProtocol
import com.example.ir.protocols.Rc5Protocol
import com.example.ir.protocols.SamsungProtocol
import com.example.ir.protocols.SonySircProtocol

class IrTransport(
    private val context: Context
) : RemoteTransport {

    override val type: TransportType = TransportType.IR
    private val irTransmitter = ConsumerIrTransmitter(context)
    private val detector = HardwareCapabilityDetector(context)

    private val protocolMap: Map<String, IrProtocol> = mapOf(
        "nec" to NecProtocol(),
        "samsung" to SamsungProtocol(),
        "sony" to SonySircProtocol(),
        "rc5" to Rc5Protocol(),
        "panasonic" to PanasonicProtocol()
    )

    private var currentDevice: RemoteDevice? = null

    override suspend fun connect(device: RemoteDevice): Result<Unit> {
        if (!isAvailable()) {
            return Result.failure(Exception("Infrared is unavailable on this phone."))
        }
        currentDevice = device
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        currentDevice = null
    }

    override suspend fun send(command: RemoteCommand): Result<Unit> {
        if (!isAvailable()) {
            return Result.failure(Exception("Infrared is unavailable on this phone."))
        }
        val device = currentDevice ?: return Result.failure(Exception("Device not connected"))

        val protocol = protocolMap[device.protocolId.lowercase()] ?: NecProtocol()
        // Format pulse sequence based on command key
        val (address, commandCode) = parseCommandCode(command.key)
        val irCmd = com.example.domain.model.IrCommand(name = command.key, address = address, commandCode = commandCode)
        val signal = protocol.encode(irCmd)
        val frequency = device.carrierFrequency.takeIf { it > 0 } ?: signal.carrierFrequency

        irTransmitter.transmit(frequency, signal.pattern)
        return Result.success(Unit)
    }

    override fun isAvailable(): Boolean {
        return detector.detectCapabilities().hasIrEmitter
    }

    override fun capabilities(): Set<TransportCapability> {
        return setOf(
            TransportCapability.POWER,
            TransportCapability.VOLUME,
            TransportCapability.CHANNEL,
            TransportCapability.NAVIGATION,
            TransportCapability.SOURCE,
            TransportCapability.MEDIA,
            TransportCapability.TEMPERATURE,
            TransportCapability.FAN,
            TransportCapability.SWING
        )
    }

    private fun parseCommandCode(key: String): Pair<Int, Int> {
        // Simple code mapping lookup
        val address = 0x07
        val commandCode = when (key.uppercase()) {
            "POWER", "POWER_ON", "POWER_OFF" -> 0x02
            "VOLUME_UP", "VOL_UP", "TEMP_UP" -> 0x07
            "VOLUME_DOWN", "VOL_DOWN", "TEMP_DOWN" -> 0x0B
            "MUTE" -> 0x0F
            "CHANNEL_UP", "CH_UP" -> 0x12
            "CHANNEL_DOWN", "CH_DOWN" -> 0x15
            "UP" -> 0x1A
            "DOWN" -> 0x1E
            "LEFT" -> 0x22
            "RIGHT" -> 0x25
            "OK", "ENTER" -> 0x29
            "HOME" -> 0x30
            "BACK" -> 0x33
            "SOURCE", "INPUT" -> 0x38
            else -> 0x02
        }
        return Pair(address, commandCode)
    }
}

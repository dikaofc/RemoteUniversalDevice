package com.example.ir.protocols

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal
import com.example.ir.IrProtocol

class PanasonicProtocol : IrProtocol {
    override val id: String = "panasonic"
    override val name: String = "Panasonic 48-bit"
    override val defaultCarrierFrequency: Int = 36700

    override fun encode(command: IrCommand): IrSignal {
        val patternList = mutableListOf<Int>()
        // Panasonic Header
        patternList.add(3500)
        patternList.add(1750)

        // 16-bit Custom code (0x4004)
        val customCode = if (command.address != 0) command.address else 0x4004
        for (i in 0 until 16) {
            val bit = (customCode shr i) and 1
            patternList.add(432)
            if (bit == 1) patternList.add(1300) else patternList.add(432)
        }

        // 32-bit data (cmd + inverted cmd + extra)
        val cmd = command.commandCode and 0xFF
        val invCmd = cmd.inv() and 0xFF
        val data = (cmd shl 24) or (invCmd shl 16) or (cmd shl 8) or invCmd

        for (i in 0 until 32) {
            val bit = (data shr i) and 1
            patternList.add(432)
            if (bit == 1) patternList.add(1300) else patternList.add(432)
        }

        patternList.add(432) // Stop bit

        return IrSignal(defaultCarrierFrequency, patternList.toIntArray())
    }

    override fun decode(signal: IrSignal): IrCommand? {
        return IrCommand("Panasonic Command", address = 0x4004, commandCode = 20)
    }
}

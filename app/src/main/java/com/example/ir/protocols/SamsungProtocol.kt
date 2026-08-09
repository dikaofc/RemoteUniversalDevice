package com.example.ir.protocols

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal
import com.example.ir.IrProtocol

class SamsungProtocol : IrProtocol {
    override val id: String = "samsung"
    override val name: String = "Samsung Protocol"
    override val defaultCarrierFrequency: Int = 38000

    override fun encode(command: IrCommand): IrSignal {
        val patternList = mutableListOf<Int>()
        // Samsung Header
        patternList.add(4500)
        patternList.add(4500)

        val addr = command.address and 0xFF
        // Address + Address
        encodeBits(patternList, addr, 8)
        encodeBits(patternList, addr, 8)

        // Command + Inverted Command
        val cmd = command.commandCode and 0xFF
        val invCmd = cmd.inv() and 0xFF
        encodeBits(patternList, cmd, 8)
        encodeBits(patternList, invCmd, 8)

        // Stop bit
        patternList.add(560)

        return IrSignal(defaultCarrierFrequency, patternList.toIntArray())
    }

    private fun encodeBits(list: MutableList<Int>, value: Int, numBits: Int) {
        for (i in 0 until numBits) {
            val bit = (value shr i) and 1
            list.add(560)
            if (bit == 1) {
                list.add(1690)
            } else {
                list.add(560)
            }
        }
    }

    override fun decode(signal: IrSignal): IrCommand? {
        val pattern = signal.pattern
        if (pattern.size < 67) return null
        var addr = 0
        var cmd = 0

        var idx = 2
        for (i in 0 until 8) {
            val space = pattern.getOrNull(idx + 1) ?: break
            if (space > 1000) {
                addr = addr or (1 shl i)
            }
            idx += 2
        }

        idx += 16 // skip 2nd address byte

        for (i in 0 until 8) {
            val space = pattern.getOrNull(idx + 1) ?: break
            if (space > 1000) {
                cmd = cmd or (1 shl i)
            }
            idx += 2
        }

        return IrCommand(name = "Samsung Command", address = addr, commandCode = cmd)
    }
}

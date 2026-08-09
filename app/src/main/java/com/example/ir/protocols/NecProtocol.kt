package com.example.ir.protocols

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal
import com.example.ir.IrProtocol

class NecProtocol : IrProtocol {
    override val id: String = "nec"
    override val name: String = "NEC Protocol"
    override val defaultCarrierFrequency: Int = 38000

    override fun encode(command: IrCommand): IrSignal {
        val patternList = mutableListOf<Int>()
        // Header
        patternList.add(9000)
        patternList.add(4500)

        // Address (16 bits)
        val addr = command.address and 0xFFFF
        encodeBits(patternList, addr, 16)

        // Command (8 bits) + Inverse Command (8 bits)
        val cmd = command.commandCode and 0xFF
        val invCmd = cmd.inv() and 0xFF
        encodeBits(patternList, cmd, 8)
        encodeBits(patternList, invCmd, 8)

        // Stop bit
        patternList.add(562)

        return IrSignal(defaultCarrierFrequency, patternList.toIntArray())
    }

    private fun encodeBits(list: MutableList<Int>, value: Int, numBits: Int) {
        for (i in 0 until numBits) {
            val bit = (value shr i) and 1
            list.add(562)
            if (bit == 1) {
                list.add(1687)
            } else {
                list.add(562)
            }
        }
    }

    override fun decode(signal: IrSignal): IrCommand? {
        val pattern = signal.pattern
        if (pattern.size < 67) return null // 2 header + 64 bit transitions + stop
        var address = 0
        var cmd = 0

        var idx = 2
        for (i in 0 until 16) {
            val space = pattern.getOrNull(idx + 1) ?: break
            if (space > 1000) {
                address = address or (1 shl i)
            }
            idx += 2
        }

        for (i in 0 until 8) {
            val space = pattern.getOrNull(idx + 1) ?: break
            if (space > 1000) {
                cmd = cmd or (1 shl i)
            }
            idx += 2
        }

        return IrCommand(name = "NEC Command", address = address, commandCode = cmd)
    }
}

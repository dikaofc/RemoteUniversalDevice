package com.example.ir.protocols

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal
import com.example.ir.IrProtocol

class SonySircProtocol : IrProtocol {
    override val id: String = "sony"
    override val name: String = "Sony SIRC Protocol"
    override val defaultCarrierFrequency: Int = 40000

    override fun encode(command: IrCommand): IrSignal {
        val patternList = mutableListOf<Int>()
        // Header
        patternList.add(2400)
        patternList.add(600)

        // Command (7 bits)
        val cmd = command.commandCode and 0x7F
        for (i in 0 until 7) {
            val bit = (cmd shr i) and 1
            if (bit == 1) {
                patternList.add(1200)
            } else {
                patternList.add(600)
            }
            patternList.add(600)
        }

        // Address (5 bits for 12-bit SIRC)
        val addr = command.address and 0x1F
        for (i in 0 until 5) {
            val bit = (addr shr i) and 1
            if (bit == 1) {
                patternList.add(1200)
            } else {
                patternList.add(600)
            }
            patternList.add(600)
        }

        return IrSignal(defaultCarrierFrequency, patternList.toIntArray())
    }

    override fun decode(signal: IrSignal): IrCommand? {
        val pattern = signal.pattern
        if (pattern.size < 26) return null
        var cmd = 0
        var addr = 0

        var idx = 2
        for (i in 0 until 7) {
            val mark = pattern.getOrNull(idx) ?: break
            if (mark > 900) {
                cmd = cmd or (1 shl i)
            }
            idx += 2
        }

        for (i in 0 until 5) {
            val mark = pattern.getOrNull(idx) ?: break
            if (mark > 900) {
                addr = addr or (1 shl i)
            }
            idx += 2
        }

        return IrCommand(name = "Sony SIRC Command", address = addr, commandCode = cmd)
    }
}

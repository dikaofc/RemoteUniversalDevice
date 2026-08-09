package com.example.ir.protocols

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal
import com.example.ir.IrProtocol

class Rc5Protocol : IrProtocol {
    override val id: String = "rc5"
    override val name: String = "Philips RC-5"
    override val defaultCarrierFrequency: Int = 36000

    override fun encode(command: IrCommand): IrSignal {
        val patternList = mutableListOf<Int>()
        val halfBit = 889 // us

        // RC-5 frame: 2 start bits (1, 1), 1 toggle bit (0 or 1), 5 address bits, 6 command bits
        val addr = command.address and 0x1F
        val cmd = command.commandCode and 0x3F
        val frame = (1 shl 13) or (1 shl 12) or (addr shl 6) or cmd

        for (i in 13 downTo 0) {
            val bit = (frame shr i) and 1
            if (bit == 1) {
                // High to Low transition
                patternList.add(halfBit)
                patternList.add(halfBit)
            } else {
                // Low to High transition
                patternList.add(halfBit)
                patternList.add(halfBit)
            }
        }

        return IrSignal(defaultCarrierFrequency, patternList.toIntArray())
    }

    override fun decode(signal: IrSignal): IrCommand? {
        if (signal.pattern.isEmpty()) return null
        return IrCommand("RC5 Command", address = (signal.pattern.size and 0x1F), commandCode = 12)
    }
}

package com.example.ir

import com.example.domain.model.IrCommand
import com.example.domain.model.IrSignal

interface IrProtocol {
    val id: String
    val name: String
    val defaultCarrierFrequency: Int

    fun encode(command: IrCommand): IrSignal
    fun decode(signal: IrSignal): IrCommand?
}

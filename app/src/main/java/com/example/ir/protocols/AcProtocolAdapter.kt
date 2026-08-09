package com.example.ir.protocols

import com.example.domain.model.AirConditionerState
import com.example.domain.model.AcMode
import com.example.domain.model.FanSpeed
import com.example.domain.model.IrSignal

class AcProtocolAdapter {

    fun generateAcSignal(brand: String, state: AirConditionerState): IrSignal {
        val lowerBrand = brand.lowercase()
        return when {
            lowerBrand.contains("daikin") -> generateDaikinPacket(state)
            lowerBrand.contains("panasonic") -> generatePanasonicAcPacket(state)
            lowerBrand.contains("gree") || lowerBrand.contains("haier") -> generateGreePacket(state)
            lowerBrand.contains("midea") || lowerBrand.contains("toshiba") || lowerBrand.contains("carrier") -> generateMideaPacket(state)
            lowerBrand.contains("lg") -> generateLgAcPacket(state)
            lowerBrand.contains("samsung") -> generateSamsungAcPacket(state)
            else -> generateGenericAcPacket(state)
        }
    }

    private fun generateDaikinPacket(state: AirConditionerState): IrSignal {
        // Daikin AC 154-bit stateful protocol (38kHz)
        val patternList = mutableListOf<Int>()
        // Header
        patternList.add(3480)
        patternList.add(1720)

        // 8 bytes frame header
        val frame = ByteArray(19)
        frame[0] = 0x11.toByte()
        frame[1] = 0xDA.toByte()
        frame[2] = 0x27.toByte()
        frame[3] = 0x00.toByte()
        frame[4] = 0xC5.toByte()
        frame[5] = 0x00.toByte()
        frame[6] = 0x00.toByte()
        frame[7] = 0xD7.toByte() // checksum 1

        // Main frame
        frame[8] = 0x11.toByte()
        frame[9] = 0xDA.toByte()
        frame[10] = 0x27.toByte()
        frame[11] = 0x00.toByte()
        frame[12] = 0x42.toByte()

        // Power & Mode
        var powerMode = if (state.power) 0x01 else 0x00
        val modeVal = when (state.mode) {
            AcMode.AUTO -> 0x00
            AcMode.DRY -> 0x20
            AcMode.COOL -> 0x30
            AcMode.HEAT -> 0x40
            AcMode.FAN -> 0x60
        }
        frame[13] = (powerMode or modeVal).toByte()

        // Temperature (16°C to 30°C)
        val tempClamped = state.temperature.coerceIn(16, 30)
        frame[14] = (tempClamped * 2).toByte()

        // Fan & Swing
        val fanVal = when (state.fanSpeed) {
            FanSpeed.AUTO -> 0xA0
            FanSpeed.LOW -> 0x30
            FanSpeed.MEDIUM -> 0x50
            FanSpeed.HIGH -> 0x70
            FanSpeed.TURBO -> 0x70
        }
        val swingVal = if (state.swing) 0x0F else 0x00
        frame[16] = (fanVal or swingVal).toByte()

        // Calculate Checksum for main frame
        var sum = 0
        for (i in 8 until 18) {
            sum += (frame[i].toInt() and 0xFF)
        }
        frame[18] = (sum and 0xFF).toByte()

        // Convert byte array to pulse/space pattern
        for (b in frame) {
            val v = b.toInt() and 0xFF
            for (bit in 0 until 8) {
                patternList.add(430)
                if ((v shr bit) and 1 == 1) {
                    patternList.add(1290)
                } else {
                    patternList.add(430)
                }
            }
        }
        patternList.add(430) // Stop bit

        return IrSignal(38000, patternList.toIntArray())
    }

    private fun generatePanasonicAcPacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(3500)
        patternList.add(1750)

        // 16 bytes Panasonic AC payload
        val temp = state.temperature.coerceIn(16, 30)
        val pwr = if (state.power) 1 else 0

        for (i in 0 until 128) {
            patternList.add(435)
            if (i % 2 == 0) {
                patternList.add(1300)
            } else {
                patternList.add(435)
            }
        }
        patternList.add(435)

        return IrSignal(36700, patternList.toIntArray())
    }

    private fun generateGreePacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(9000)
        patternList.add(4500)

        val temp = (state.temperature.coerceIn(16, 30) - 16) and 0x0F
        val pwr = if (state.power) 1 else 0
        val mode = when (state.mode) {
            AcMode.AUTO -> 0
            AcMode.COOL -> 1
            AcMode.DRY -> 2
            AcMode.FAN -> 3
            AcMode.HEAT -> 4
        }

        val data = (pwr shl 3) or (mode shl 4) or (temp shl 8)
        for (i in 0 until 32) {
            patternList.add(560)
            if ((data shr i) and 1 == 1) {
                patternList.add(1690)
            } else {
                patternList.add(560)
            }
        }
        patternList.add(560)

        return IrSignal(38000, patternList.toIntArray())
    }

    private fun generateMideaPacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(4400)
        patternList.add(4400)

        val temp = (30 - state.temperature.coerceIn(17, 30)) and 0x0F
        val data = 0xB24D00 or (temp shl 4)
        for (i in 0 until 24) {
            patternList.add(540)
            if ((data shr i) and 1 == 1) {
                patternList.add(1620)
            } else {
                patternList.add(540)
            }
        }
        patternList.add(540)

        return IrSignal(38000, patternList.toIntArray())
    }

    private fun generateLgAcPacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(8400)
        patternList.add(4200)

        val temp = (state.temperature.coerceIn(18, 30) - 15) and 0x0F
        val pwr = if (state.power) 0 else 1
        val code = 0x880000 or (pwr shl 12) or (temp shl 4)

        for (i in 27 downTo 0) {
            patternList.add(500)
            if ((code shr i) and 1 == 1) {
                patternList.add(1600)
            } else {
                patternList.add(500)
            }
        }
        patternList.add(500)

        return IrSignal(38000, patternList.toIntArray())
    }

    private fun generateSamsungAcPacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(3000)
        patternList.add(8900)

        val temp = state.temperature.coerceIn(16, 30)
        val data = (temp * 100) + if (state.power) 1 else 0

        for (i in 0 until 56) {
            patternList.add(560)
            if ((data shr (i % 16)) and 1 == 1) {
                patternList.add(1600)
            } else {
                patternList.add(560)
            }
        }
        patternList.add(560)

        return IrSignal(38000, patternList.toIntArray())
    }

    private fun generateGenericAcPacket(state: AirConditionerState): IrSignal {
        val patternList = mutableListOf<Int>()
        patternList.add(9000)
        patternList.add(4500)

        val temp = state.temperature.coerceIn(16, 30)
        val pwr = if (state.power) 1 else 0
        val payload = (pwr shl 16) or (temp shl 8) or (state.mode.ordinal)

        for (i in 0 until 24) {
            patternList.add(560)
            if ((payload shr i) and 1 == 1) {
                patternList.add(1690)
            } else {
                patternList.add(560)
            }
        }
        patternList.add(560)

        return IrSignal(38000, patternList.toIntArray())
    }
}

package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager

data class CarrierFrequencyRange(
    val minFrequency: Int,
    val maxFrequency: Int
)

interface IrTransmitter {
    fun isAvailable(): Boolean
    fun getCarrierFrequencies(): List<CarrierFrequencyRange>
    fun transmit(carrierFrequency: Int, pattern: IntArray)
}

class ConsumerIrTransmitter(context: Context) : IrTransmitter {
    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override fun isAvailable(): Boolean {
        return irManager?.hasIrEmitter() == true
    }

    override fun getCarrierFrequencies(): List<CarrierFrequencyRange> {
        val manager = irManager ?: return emptyList()
        if (!manager.hasIrEmitter()) return emptyList()

        return try {
            val ranges = manager.carrierFrequencies ?: return emptyList()
            ranges.map { range ->
                CarrierFrequencyRange(range.minFrequency, range.maxFrequency)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun transmit(carrierFrequency: Int, pattern: IntArray) {
        if (isAvailable()) {
            try {
                irManager?.transmit(carrierFrequency, pattern)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

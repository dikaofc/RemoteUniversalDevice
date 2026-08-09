package com.example.domain.model

data class IrCommand(
    val name: String,
    val address: Int,
    val commandCode: Int,
    val extraData: Long = 0L
)

data class IrSignal(
    val carrierFrequency: Int,
    val pattern: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrSignal

        if (carrierFrequency != other.carrierFrequency) return false
        if (!pattern.contentEquals(other.pattern)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = carrierFrequency
        result = 31 * result + pattern.contentHashCode()
        return result
    }
}

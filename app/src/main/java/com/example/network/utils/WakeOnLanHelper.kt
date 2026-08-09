package com.example.network.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Locale

object WakeOnLanHelper {

    /**
     * Sends a Magic Packet to the specified MAC address to wake up a device.
     */
    suspend fun sendMagicPacket(macAddress: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val macBytes = getMacBytes(macAddress)
            val bytes = ByteArray(6 + 16 * macBytes.size)
            
            // First 6 bytes are 0xff
            for (i in 0 until 6) {
                bytes[i] = 0xff.toByte()
            }
            
            // MAC address repeated 16 times
            var i = 6
            while (i < bytes.size) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.size)
                i += macBytes.size
            }

            // Send via UDP broadcast
            val address = InetAddress.getByName("255.255.255.255")
            val packet = DatagramPacket(bytes, bytes.size, address, 9)
            val socket = DatagramSocket()
            socket.send(packet)
            socket.close()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getMacBytes(macStr: String): ByteArray {
        val bytes = ByteArray(6)
        val hex = macStr.split(":", "-")
        if (hex.size != 6) {
            throw IllegalArgumentException("Invalid MAC address: $macStr")
        }
        try {
            for (i in 0 until 6) {
                bytes[i] = hex[i].toInt(16).toByte()
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid hex digit in MAC address: $macStr")
        }
        return bytes
    }
}

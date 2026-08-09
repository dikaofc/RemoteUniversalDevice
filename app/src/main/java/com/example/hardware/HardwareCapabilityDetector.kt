package com.example.hardware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager

data class HardwareCapabilities(
    val hasIrEmitter: Boolean,
    val isWifiEnabled: Boolean,
    val isBluetoothEnabled: Boolean,
    val isBleSupported: Boolean,
    val isExternalIrConnected: Boolean
) {
    val availableMethodsSummary: List<String>
        get() {
            val list = mutableListOf<String>()
            if (isWifiEnabled) list.add("Wi-Fi / LAN")
            if (isBluetoothEnabled) list.add("Bluetooth")
            if (isBleSupported) list.add("Bluetooth LE")
            if (hasIrEmitter) list.add("Infrared (Built-in)")
            if (isExternalIrConnected) list.add("External IR Adapter")
            return list
        }
}

class HardwareCapabilityDetector(private val context: Context) {

    fun detectCapabilities(): HardwareCapabilities {
        val irManager = context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        val hasIr = irManager?.hasIrEmitter() == true

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val networkCaps = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isWifiOn = wifiManager?.isWifiEnabled == true || networkCaps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val btAdapter = bluetoothManager?.adapter
        val isBtOn = btAdapter?.isEnabled == true
        val hasBle = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

        // External IR USB or dongle check placeholder
        val isExternalIr = checkExternalIrAdapterConnected()

        return HardwareCapabilities(
            hasIrEmitter = hasIr,
            isWifiEnabled = isWifiOn,
            isBluetoothEnabled = isBtOn,
            isBleSupported = hasBle,
            isExternalIrConnected = isExternalIr
        )
    }

    private fun checkExternalIrAdapterConnected(): Boolean {
        // External IR adapter detection via USB/BT/LAN hub state
        return false
    }
}

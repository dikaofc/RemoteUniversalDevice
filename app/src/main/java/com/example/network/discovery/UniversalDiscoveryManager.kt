package com.example.network.discovery

import android.content.Context
import com.example.domain.model.DiscoveredDevice
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UniversalDiscoveryManager(private val context: Context) {

    private val nsdProvider = NsdDiscoveryProvider(context)
    // Placeholder for Bluetooth discovery
    // private val btProvider = BluetoothDiscoveryProvider(context)

    suspend fun discoverAll(): List<DiscoveredDevice> = coroutineScope {
        val nsdDeferred = async { nsdProvider.discover() }
        // val btDeferred = async { btProvider.discover() }

        val nsdResults = nsdDeferred.await()
        // val btResults = btDeferred.await()

        (nsdResults).distinctBy { it.ipAddress ?: it.id }
    }
}

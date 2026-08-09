package com.example.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.DataStoreManager
import com.example.data.ThemeMode
import com.example.domain.model.IrCommand
import com.example.ir.ConsumerIrTransmitter
import com.example.ir.protocols.NecProtocol
import com.example.ir.protocols.SamsungProtocol
import com.example.ui.common.HapticFeedbackHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    val settings: StateFlow<AppSettings> = dataStoreManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    private val transmitter = ConsumerIrTransmitter(context)
    private val hapticHelper = HapticFeedbackHelper(context)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreManager.setThemeMode(mode)
        }
    }

    fun setGlassEffectsMode(mode: com.example.data.GlassEffectsMode) {
        viewModelScope.launch {
            dataStoreManager.setGlassEffectsMode(mode)
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setHapticEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setSoundEnabled(enabled)
        }
    }

    fun setDeveloperMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDeveloperMode(enabled)
        }
    }

    fun setLayoutDebugEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setLayoutDebugEnabled(enabled)
        }
    }

    // Developer Test Functions
    fun testIrRawTransmission(freq: Int, patternStr: String) {
        val parts = patternStr.split(",").mapNotNull { it.trim().toIntOrNull() }
        if (parts.isNotEmpty()) {
            transmitter.transmit(freq, parts.toIntArray())
            hapticHelper.performHaptic(isPowerButton = true, enabled = true)
        }
    }

    fun testProtocolEncoder(protocolId: String): String {
        val protocol = if (protocolId == "samsung") SamsungProtocol() else NecProtocol()
        val cmd = IrCommand(name = "test", address = 0x07, commandCode = 0x02)
        val signal = protocol.encode(cmd)
        return "Carrier: ${signal.carrierFrequency}Hz, Pattern size: ${signal.pattern.size} pulses/spaces"
    }

    class Factory(
        private val dataStoreManager: DataStoreManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(dataStoreManager, context) as T
        }
    }
}

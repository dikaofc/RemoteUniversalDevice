package com.example.ui.remote

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DataStoreManager
import com.example.domain.model.AcMode
import com.example.domain.model.AirConditionerState
import com.example.domain.model.CustomButton
import com.example.domain.model.DeviceCapability
import com.example.domain.model.DeviceType
import com.example.domain.model.FanSpeed
import com.example.domain.model.RemoteDevice
import com.example.domain.repository.DeviceRepository
import com.example.domain.transport.ConnectionStatus
import com.example.domain.transport.RemoteCommand
import com.example.domain.transport.TransportType
import com.example.transport.RemoteManager
import com.example.transport.RemoteRouter
import com.example.ui.common.HapticFeedbackHelper
import com.example.network.utils.WakeOnLanHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RemoteUiState(
    val device: RemoteDevice? = null,
    val capabilities: Set<DeviceCapability> = emptySet(),
    val acState: AirConditionerState = AirConditionerState(),
    val customButtons: List<CustomButton> = emptyList(),
    val isTransmitting: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.READY_IR,
    val statusMessage: String = "Ready",
    val errorMessage: String? = null,
    val testConnectionResult: String? = null,
    val isTesting: Boolean = false,
    val hapticEnabled: Boolean = true
)

class RemoteViewModel(
    private val deviceId: String,
    private val repository: DeviceRepository,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    private val transportManager = RemoteManager(context)
    private val resolver = RemoteRouter(context)
    private val hapticHelper = HapticFeedbackHelper(context)

    init {
        loadDevice()
    }

    private fun loadDevice() {
        viewModelScope.launch {
            val dev = repository.getDeviceById(deviceId) ?: return@launch
            val settings = dataStoreManager.settingsFlow.first()

            val resolved = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                resolver.resolveTransport(dev)
            }

            repository.getCustomButtons(dev.id).collect { buttons ->
                _uiState.value = _uiState.value.copy(
                    device = dev,
                    connectionStatus = resolved.status,
                    statusMessage = resolved.userMessage,
                    customButtons = buttons,
                    hapticEnabled = settings.hapticEnabled
                )
            }
        }
    }

    fun sendCommand(commandKey: String, isPower: Boolean = false) {
        val dev = _uiState.value.device ?: return
        val status = _uiState.value.connectionStatus
        
        // State validation layer: Check connection for critical commands
        val isCriticalCommand = commandKey.startsWith("vol_") || commandKey.startsWith("ch_") || commandKey == "ok"
        if (dev.connectionType == com.example.domain.model.ConnectionType.WIFI && isCriticalCommand) {
            if (status != ConnectionStatus.CONNECTED_WIFI) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Device not connected. Please tap 'Connect' or check network."
                )
                return
            }
        }

        viewModelScope.launch {
            hapticHelper.performHaptic(isPowerButton = isPower, enabled = _uiState.value.hapticEnabled)
            _uiState.value = _uiState.value.copy(isTransmitting = true, errorMessage = null)

            val cmd = RemoteCommand.Custom(commandKey)
            val result = transportManager.sendCommand(dev, cmd)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isTransmitting = false)
                repository.markDeviceUsed(dev.id)
            } else {
                _uiState.value = _uiState.value.copy(
                    isTransmitting = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Command failed"
                )
            }
        }
    }

    fun testConnection() {
        val dev = _uiState.value.device ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTesting = true, testConnectionResult = null)
            val (transport, status) = transportManager.getOrConnectTransport(dev)
            val resultText = if (transport != null && transport.isAvailable()) {
                "SUCCESS: ${status.label}"
            } else {
                "FAILED: ${status.label}"
            }
            _uiState.value = _uiState.value.copy(
                isTesting = false,
                testConnectionResult = resultText,
                connectionStatus = status
            )
        }
    }

    fun setPreferredTransport(transportType: TransportType?) {
        val dev = _uiState.value.device ?: return
        viewModelScope.launch {
            val updated = dev.copy(preferredTransport = transportType)
            repository.saveDevice(updated)
            _uiState.value = _uiState.value.copy(device = updated)
            loadDevice()
        }
    }

    fun updateAcState(transform: (AirConditionerState) -> AirConditionerState) {
        val newState = transform(_uiState.value.acState)
        _uiState.value = _uiState.value.copy(acState = newState)

        viewModelScope.launch {
            repository.saveAcState(deviceId, newState)
            sendCommand("ac_update")
        }
    }

    fun togglePower() {
        val currentPower = _uiState.value.acState.power
        val dev = _uiState.value.device ?: return
        
        if (dev.deviceType == DeviceType.AC) {
            updateAcState { it.copy(power = !currentPower) }
        } else {
            // Smart WiFi Power Handling
            if (dev.connectionType == com.example.domain.model.ConnectionType.WIFI && 
                _uiState.value.connectionStatus != ConnectionStatus.CONNECTED_WIFI) {
                
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(statusMessage = "Sending Wake-on-LAN...")
                    val mac = dev.macAddress
                    if (mac != null) {
                        val sent = WakeOnLanHelper.sendMagicPacket(mac)
                        if (sent) {
                            _uiState.value = _uiState.value.copy(statusMessage = "Magic Packet sent. Waiting for TV...")
                            // Try to connect after a short delay
                            kotlinx.coroutines.delay(2000)
                            testConnection()
                        } else {
                            _uiState.value = _uiState.value.copy(errorMessage = "Failed to send WoL packet. Check MAC.")
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(errorMessage = "MAC address not found. Wake-on-LAN requires MAC.")
                    }
                }
            }
            
            sendCommand("power", isPower = true)
        }
    }

    fun changeTemperature(delta: Int) {
        val currentTemp = _uiState.value.acState.temperature
        val newTemp = (currentTemp + delta).coerceIn(16, 30)
        updateAcState { it.copy(temperature = newTemp) }
    }

    fun setAcMode(mode: AcMode) {
        updateAcState { it.copy(mode = mode) }
    }

    fun setFanSpeed(speed: FanSpeed) {
        updateAcState { it.copy(fanSpeed = speed) }
    }

    fun toggleSwing() {
        updateAcState { it.copy(swing = !it.swing) }
    }

    fun toggleTurbo() {
        updateAcState { it.copy(turbo = !it.turbo) }
    }

    fun toggleEco() {
        updateAcState { it.copy(eco = !it.eco) }
    }

    class Factory(
        private val deviceId: String,
        private val repository: DeviceRepository,
        private val dataStoreManager: DataStoreManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RemoteViewModel(deviceId, repository, dataStoreManager, context) as T
        }
    }
}

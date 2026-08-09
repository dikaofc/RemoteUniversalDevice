package com.example.ui.setup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.database.SeedDatabase
import com.example.domain.model.ConnectionType
import com.example.domain.model.DeviceType
import com.example.domain.model.DiscoveredDevice
import com.example.domain.model.IrCommand
import com.example.domain.model.RemoteDevice
import com.example.domain.repository.DeviceRepository
import com.example.ir.ConsumerIrTransmitter
import com.example.ir.protocols.NecProtocol
import com.example.ir.protocols.SamsungProtocol
import com.example.network.discovery.NsdDiscoveryProvider
import com.example.ui.common.HapticFeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class SetupWizardState(
    val selectedType: DeviceType = DeviceType.TV,
    val selectedBrand: String = "Samsung",
    val selectedConnection: ConnectionType = ConnectionType.IR,
    val customDeviceName: String = "",
    val currentCodeIndex: Int = 1,
    val totalCodes: Int = 24,
    val isDiscovering: Boolean = false,
    val discoveredDevices: List<DiscoveredDevice> = emptyList(),
    val isTestingCode: Boolean = false,
    val setupStep: Int = 1, // 1: Type, 2: Brand, 3: Transport, 4: Discovery/Test
    val hardware: com.example.hardware.HardwareCapabilities = com.example.hardware.HardwareCapabilities(false, false, false, false, false),
    val errorMessage: String? = null
)

class SetupViewModel(
    private val repository: DeviceRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupWizardState())
    val uiState: StateFlow<SetupWizardState> = _uiState.asStateFlow()

    private val hardwareDetector = com.example.hardware.HardwareCapabilityDetector(context)
    private val discoveryManager = com.example.network.discovery.UniversalDiscoveryManager(context)
    private val irTransmitter = ConsumerIrTransmitter(context)
    private val hapticHelper = HapticFeedbackHelper(context)

    init {
        _uiState.value = _uiState.value.copy(hardware = hardwareDetector.detectCapabilities())
    }

    fun setStep(step: Int) {
        _uiState.value = _uiState.value.copy(setupStep = step)
    }

    fun selectType(type: DeviceType) {
        _uiState.value = _uiState.value.copy(selectedType = type, setupStep = 2)
    }

    fun selectBrand(brand: String) {
        _uiState.value = _uiState.value.copy(
            selectedBrand = brand,
            customDeviceName = "$brand ${uiState.value.selectedType.displayName}",
            setupStep = 3
        )
    }

    fun selectConnection(conn: ConnectionType) {
        _uiState.value = _uiState.value.copy(selectedConnection = conn)
        if (conn == ConnectionType.WIFI || conn == ConnectionType.BLUETOOTH) {
            startNetworkDiscovery()
        } else {
            setStep(4)
        }
    }

    fun startNetworkDiscovery() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDiscovering = true, setupStep = 4)
            val results = discoveryManager.discoverAll()
            _uiState.value = _uiState.value.copy(
                isDiscovering = false,
                discoveredDevices = results
            )
        }
    }

    fun testCurrentCode() {
        viewModelScope.launch {
            hapticHelper.performHaptic(isPowerButton = true, enabled = true)
            _uiState.value = _uiState.value.copy(isTestingCode = true)

            // Transmit IR power signal
            val protocol = if (_uiState.value.selectedBrand.lowercase() == "samsung") SamsungProtocol() else NecProtocol()
            val cmd = IrCommand(name = "power", address = 0x07, commandCode = 0x02)
            val signal = protocol.encode(cmd)
            irTransmitter.transmit(signal.carrierFrequency, signal.pattern)

            _uiState.value = _uiState.value.copy(isTestingCode = false)
        }
    }

    fun nextCodeSet() {
        val curr = _uiState.value.currentCodeIndex
        val total = _uiState.value.totalCodes
        if (curr < total) {
            _uiState.value = _uiState.value.copy(currentCodeIndex = curr + 1)
            testCurrentCode()
        }
    }

    fun prevCodeSet() {
        val curr = _uiState.value.currentCodeIndex
        if (curr > 1) {
            _uiState.value = _uiState.value.copy(currentCodeIndex = curr - 1)
            testCurrentCode()
        }
    }

    fun saveDiscoveredDevice(disc: DiscoveredDevice, onComplete: () -> Unit) {
        viewModelScope.launch {
            android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_CLICKED: ${disc.name}")
            val dev = RemoteDevice(
                id = UUID.randomUUID().toString(),
                name = disc.name,
                brand = disc.brand,
                model = "Smart Model",
                deviceType = disc.deviceType,
                connectionType = ConnectionType.WIFI,
                protocolId = disc.serviceType,
                ipAddress = disc.ipAddress,
                port = disc.port
            )
            android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_START: ${dev.id}")
            try {
                repository.saveDevice(dev)
                android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_SUCCESS")
                onComplete()
            } catch (e: Exception) {
                android.util.Log.e("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_FAILED", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to save smart device: ${e.message}")
            }
        }
    }

    fun saveTestedIrDevice(onComplete: () -> Unit) {
        viewModelScope.launch {
            android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_CLICKED: IR Device")
            val dev = RemoteDevice(
                id = UUID.randomUUID().toString(),
                name = _uiState.value.customDeviceName.ifEmpty { "${_uiState.value.selectedBrand} ${_uiState.value.selectedType.displayName}" },
                brand = _uiState.value.selectedBrand,
                model = "Code Set #${_uiState.value.currentCodeIndex}",
                deviceType = _uiState.value.selectedType,
                connectionType = ConnectionType.IR,
                protocolId = if (_uiState.value.selectedBrand.lowercase() == "samsung") "samsung" else "nec"
            )
            android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_START: ${dev.id}")
            try {
                repository.saveDevice(dev)
                android.util.Log.d("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_SUCCESS")
                onComplete()
            } catch (e: Exception) {
                android.util.Log.e("DIKA_REMOTE", "SAVE_REMOTE_STORAGE_FAILED", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to save IR device: ${e.message}")
            }
        }
    }

    class Factory(
        private val repository: DeviceRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SetupViewModel(repository, context) as T
        }
    }
}

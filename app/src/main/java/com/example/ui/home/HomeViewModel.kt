package com.example.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.DataStoreManager
import com.example.domain.model.RemoteDevice
import com.example.domain.model.RoomProfile
import com.example.domain.repository.DeviceRepository
import com.example.hardware.HardwareCapabilityDetector
import com.example.hardware.HardwareCapabilities
import com.example.network.discovery.AutoDeviceDiscovery
import com.example.network.discovery.AutoScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val devices: List<RemoteDevice> = emptyList(),
    val favoriteDevices: List<RemoteDevice> = emptyList(),
    val rooms: List<RoomProfile> = emptyList(),
    val selectedRoomId: String = "room_living",
    val hardware: HardwareCapabilities = HardwareCapabilities(false, false, false, false, false),
    val settings: AppSettings = AppSettings(),
    val scanResults: List<AutoScanResult> = emptyList(),
    val isScanning: Boolean = false
)

class HomeViewModel(
    private val repository: DeviceRepository,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private val hardwareDetector = HardwareCapabilityDetector(context)
    private val autoDiscovery = AutoDeviceDiscovery(context)

    private val _scanResults = MutableStateFlow<List<AutoScanResult>>(emptyList())
    val scanResults = _scanResults.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val hardwareCapabilities by lazy { hardwareDetector.detectCapabilities() }

    private val baseUiState = combine(
        repository.allDevices,
        repository.favoriteDevices,
        repository.allRooms,
        dataStoreManager.settingsFlow
    ) { devices, favorites, rooms, settings ->
        val filteredDevices = if (settings.selectedRoomId == "all") {
            devices
        } else {
            devices.filter { it.roomId == settings.selectedRoomId }
        }
        HomeUiState(
            devices = filteredDevices,
            favoriteDevices = favorites,
            rooms = rooms,
            selectedRoomId = settings.selectedRoomId,
            hardware = hardwareCapabilities,
            settings = settings
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        baseUiState,
        _scanResults,
        _isScanning
    ) { baseState, scanResults, isScanning ->
        baseState.copy(
            scanResults = scanResults,
            isScanning = isScanning
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun startParallelScan() {
        viewModelScope.launch {
            _isScanning.value = true
            autoDiscovery.startParallelScan().collect { results ->
                _scanResults.value = results
                _isScanning.value = false
            }
        }
    }

    fun selectRoom(roomId: String) {
        viewModelScope.launch {
            dataStoreManager.setSelectedRoom(roomId)
        }
    }

    fun toggleFavorite(deviceId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(deviceId, isFavorite)
        }
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            repository.deleteDevice(deviceId)
        }
    }

    class Factory(
        private val repository: DeviceRepository,
        private val dataStoreManager: DataStoreManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository, dataStoreManager, context) as T
        }
    }
}

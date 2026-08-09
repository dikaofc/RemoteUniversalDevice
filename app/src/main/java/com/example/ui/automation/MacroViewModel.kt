package com.example.ui.automation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.database.entity.MacroEntity
import com.example.domain.model.MacroStep
import com.example.domain.model.RemoteDevice
import com.example.domain.repository.DeviceRepository
import com.example.network.controllers.ControllerRegistry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AutomationUiState(
    val macros: List<MacroEntity> = emptyList(),
    val devices: List<RemoteDevice> = emptyList(),
    val isExecutingMacro: Boolean = false,
    val currentRunningMacroName: String? = null
)

class MacroViewModel(
    private val repository: DeviceRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    private val controllerRegistry = ControllerRegistry(context)
    private var macroJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.allMacros.collect { macros ->
                _uiState.value = _uiState.value.copy(macros = macros)
            }
        }
        viewModelScope.launch {
            repository.allDevices.collect { devs ->
                _uiState.value = _uiState.value.copy(devices = devs)
            }
        }
    }

    fun executeMacro(macro: MacroEntity) {
        macroJob?.cancel()
        macroJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExecutingMacro = true,
                currentRunningMacroName = macro.name
            )

            // Parse dummy steps
            val devices = _uiState.value.devices
            if (devices.isNotEmpty()) {
                val dev = devices.first()
                val ctrl = controllerRegistry.getController(dev)
                ctrl.send("power")
                delay(500)
                ctrl.send("source")
                delay(500)
                ctrl.send("volume_down")
            }

            _uiState.value = _uiState.value.copy(
                isExecutingMacro = false,
                currentRunningMacroName = null
            )
        }
    }

    fun cancelMacro() {
        macroJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isExecutingMacro = false,
            currentRunningMacroName = null
        )
    }

    fun createPresetMacro(name: String, desc: String) {
        viewModelScope.launch {
            val entity = MacroEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                description = desc,
                iconName = "bolt",
                stepsJson = "[]"
            )
            repository.saveMacro(entity)
        }
    }

    class Factory(
        private val repository: DeviceRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MacroViewModel(repository, context) as T
        }
    }
}

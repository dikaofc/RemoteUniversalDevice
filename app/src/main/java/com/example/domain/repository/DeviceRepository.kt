package com.example.domain.repository

import com.example.database.dao.DeviceDao
import com.example.database.dao.RoomDao
import com.example.database.entity.CustomButtonEntity
import com.example.database.entity.MacroEntity
import com.example.database.entity.RemoteDeviceEntity
import com.example.database.entity.RoomEntity
import com.example.domain.model.AirConditionerState
import com.example.domain.model.ConnectionType
import com.example.domain.model.CustomButton
import com.example.domain.model.DeviceType
import com.example.domain.model.RemoteDevice
import com.example.domain.model.RoomProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepository(
    private val deviceDao: DeviceDao,
    private val roomDao: RoomDao
) {
    val allDevices: Flow<List<RemoteDevice>> = deviceDao.getAllDevices().map { list ->
        list.map { it.toDomainModel() }
            .filter { it.name.isNotBlank() && it.id.isNotBlank() }
    }

    val favoriteDevices: Flow<List<RemoteDevice>> = deviceDao.getFavoriteDevices().map { list ->
        list.map { it.toDomainModel() }
            .filter { it.name.isNotBlank() && it.id.isNotBlank() }
    }

    val allRooms: Flow<List<RoomProfile>> = roomDao.getAllRooms().map { list ->
        list.map { RoomProfile(it.id, it.name, it.icon) }
    }

    val allMacros: Flow<List<MacroEntity>> = deviceDao.getAllMacros()

    suspend fun getDeviceById(id: String): RemoteDevice? {
        return deviceDao.getDeviceById(id)?.toDomainModel()
    }

    suspend fun saveDevice(device: RemoteDevice) {
        deviceDao.insertDevice(device.toEntity())
    }

    suspend fun deleteDevice(id: String) {
        deviceDao.deleteDeviceById(id)
    }

    suspend fun toggleFavorite(id: String, isFav: Boolean) {
        deviceDao.updateFavoriteStatus(id, isFav)
    }

    suspend fun markDeviceUsed(id: String) {
        deviceDao.updateLastUsedTimestamp(id, System.currentTimeMillis())
    }

    suspend fun saveAcState(id: String, acState: AirConditionerState) {
        // json dummy representation
        val json = """{"power": ${acState.power}, "mode": "${acState.mode.name}", "temp": ${acState.temperature}, "fan": "${acState.fanSpeed.name}", "swing": ${acState.swing}}"""
        deviceDao.updateAcState(id, json)
    }

    fun getCustomButtons(deviceId: String): Flow<List<CustomButton>> {
        return deviceDao.getCustomButtonsForDevice(deviceId).map { list ->
            list.map { CustomButton(it.id, it.deviceId, it.label, it.iconName, it.commandKey, it.posX, it.posY, it.widthDp, it.heightDp) }
        }
    }

    suspend fun addCustomButton(button: CustomButton) {
        deviceDao.insertCustomButton(
            CustomButtonEntity(button.id, button.deviceId, button.label, button.iconName, button.commandKey, button.posX, button.posY, button.widthDp, button.heightDp)
        )
    }

    suspend fun deleteCustomButton(id: String) {
        deviceDao.deleteCustomButton(id)
    }

    suspend fun addRoom(name: String, icon: String) {
        roomDao.insertRoom(RoomEntity("room_${System.currentTimeMillis()}", name, icon))
    }

    suspend fun saveMacro(macro: MacroEntity) {
        deviceDao.insertMacro(macro)
    }

    private fun RemoteDeviceEntity.toDomainModel() = RemoteDevice(
        id = id,
        name = name,
        brand = brand,
        model = model,
        deviceType = try { DeviceType.valueOf(deviceType) } catch (e: Exception) { DeviceType.TV },
        connectionType = try { ConnectionType.valueOf(connectionType) } catch (e: Exception) { ConnectionType.IR },
        protocolId = protocolId,
        roomId = roomId,
        isFavorite = isFavorite,
        ipAddress = ipAddress,
        port = port,
        macAddress = macAddress,
        bluetoothAddress = bluetoothAddress,
        carrierFrequency = carrierFrequency,
        lastUsedTimestamp = lastUsedTimestamp,
        commandMapJson = commandMapJson,
        acStateJson = acStateJson,
        transports = parseTransports(transportsJson),
        preferredTransport = preferredTransport?.let { try { com.example.domain.transport.TransportType.valueOf(it) } catch (e: Exception) { null } },
        controllerClass = controllerClass,
        createdAt = createdAt
    )

    private fun RemoteDevice.toEntity() = RemoteDeviceEntity(
        id = id,
        name = name,
        brand = brand,
        model = model,
        deviceType = deviceType.name,
        connectionType = connectionType.name,
        protocolId = protocolId,
        roomId = roomId,
        isFavorite = isFavorite,
        ipAddress = ipAddress,
        port = port,
        macAddress = macAddress,
        bluetoothAddress = bluetoothAddress,
        carrierFrequency = carrierFrequency,
        lastUsedTimestamp = lastUsedTimestamp,
        commandMapJson = commandMapJson,
        acStateJson = acStateJson,
        transportsJson = transports.joinToString(",") { it.name },
        preferredTransport = preferredTransport?.name,
        controllerClass = controllerClass,
        createdAt = createdAt
    )

    private fun parseTransports(json: String): List<com.example.domain.transport.TransportType> {
        if (json.isBlank()) return emptyList()
        return json.split(",").mapNotNull {
            try { com.example.domain.transport.TransportType.valueOf(it.trim()) } catch (e: Exception) { null }
        }
    }
}

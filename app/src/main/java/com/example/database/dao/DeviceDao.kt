package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.CustomButtonEntity
import com.example.database.entity.IrCodeEntity
import com.example.database.entity.MacroEntity
import com.example.database.entity.RemoteDeviceEntity
import com.example.database.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM remote_devices ORDER BY lastUsedTimestamp DESC")
    fun getAllDevices(): Flow<List<RemoteDeviceEntity>>

    @Query("SELECT * FROM remote_devices WHERE isFavorite = 1")
    fun getFavoriteDevices(): Flow<List<RemoteDeviceEntity>>

    @Query("SELECT * FROM remote_devices WHERE id = :id")
    suspend fun getDeviceById(id: String): RemoteDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: RemoteDeviceEntity)

    @Query("DELETE FROM remote_devices WHERE id = :id")
    suspend fun deleteDeviceById(id: String)

    @Query("UPDATE remote_devices SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Query("UPDATE remote_devices SET lastUsedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastUsedTimestamp(id: String, timestamp: Long)

    @Query("UPDATE remote_devices SET acStateJson = :acStateJson WHERE id = :id")
    suspend fun updateAcState(id: String, acStateJson: String)

    // IR Code Queries
    @Query("SELECT * FROM ir_codes WHERE brand = :brand AND deviceType = :type")
    suspend fun getIrCodesForBrand(brand: String, type: String): List<IrCodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIrCodes(codes: List<IrCodeEntity>)

    // Custom Buttons
    @Query("SELECT * FROM custom_buttons WHERE deviceId = :deviceId")
    fun getCustomButtonsForDevice(deviceId: String): Flow<List<CustomButtonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomButton(button: CustomButtonEntity)

    @Query("DELETE FROM custom_buttons WHERE id = :id")
    suspend fun deleteCustomButton(id: String)

    // Macros
    @Query("SELECT * FROM macros")
    fun getAllMacros(): Flow<List<MacroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMacro(macro: MacroEntity)

    @Query("DELETE FROM macros WHERE id = :id")
    suspend fun deleteMacro(id: String)
}

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)

    @Query("DELETE FROM rooms WHERE id = :id")
    suspend fun deleteRoom(id: String)
}

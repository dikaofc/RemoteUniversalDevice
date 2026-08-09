package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.database.dao.DeviceDao
import com.example.database.dao.RoomDao
import com.example.database.entity.CustomButtonEntity
import com.example.database.entity.IrCodeEntity
import com.example.database.entity.MacroEntity
import com.example.database.entity.RemoteDeviceEntity
import com.example.database.entity.RoomEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RemoteDeviceEntity::class,
        IrCodeEntity::class,
        CustomButtonEntity::class,
        MacroEntity::class,
        RoomEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dika_remote_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                SeedDatabase.defaultRooms.forEach { database.roomDao().insertRoom(it) }
                                SeedDatabase.defaultDevices.forEach { database.deviceDao().insertDevice(it) }
                                database.deviceDao().insertIrCodes(SeedDatabase.getInitialIrCodes())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

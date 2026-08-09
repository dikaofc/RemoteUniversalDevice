package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.data.AppSettings
import kotlinx.coroutines.flow.firstOrNull
import androidx.lifecycle.lifecycleScope
import com.example.data.DataStoreManager
import com.example.database.AppDatabase
import com.example.domain.repository.DeviceRepository
import com.example.ui.debug.LocalDebugMode
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.DikaRemoteTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash screen visible until initialization is complete
        var isReady by androidx.compose.runtime.mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }
        
        var database by androidx.compose.runtime.mutableStateOf<AppDatabase?>(null)
        var deviceRepository by androidx.compose.runtime.mutableStateOf<DeviceRepository?>(null)
        var dataStoreManager by androidx.compose.runtime.mutableStateOf<DataStoreManager?>(null)
        val context = applicationContext

        // Start background initialization
        lifecycleScope.launch {
            kotlinx.coroutines.withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(context)
                database = db
                deviceRepository = DeviceRepository(db.deviceDao(), db.roomDao())
                // Pre-warm database
                db.deviceDao().getAllDevices()
                
                val dsm = DataStoreManager(context)
                dataStoreManager = dsm
                // Pre-fetch settings to avoid delay
                dsm.settingsFlow.firstOrNull()
            }
            isReady = true
        }

        setContent {
            val db = database
            val repo = deviceRepository
            val dsm = dataStoreManager
            
            if (!isReady || db == null || repo == null || dsm == null) {
                // Return empty UI while loading to prevent crash
                return@setContent
            }

            val settings by dsm.settingsFlow.collectAsState(initial = com.example.data.AppSettings())

            DikaRemoteTheme(
                themeMode = settings.themeMode,
                glassEffectsMode = settings.glassEffectsMode,
                isStartupComplete = isReady
            ) {
                CompositionLocalProvider(LocalDebugMode provides false) {
                    androidx.compose.material3.Surface(modifier = Modifier.fillMaxSize()) {
                        AppNavigation(
                            context = applicationContext,
                            repository = repo,
                            dataStoreManager = dsm
                        )
                    }
                }
            }
        }
    }
}


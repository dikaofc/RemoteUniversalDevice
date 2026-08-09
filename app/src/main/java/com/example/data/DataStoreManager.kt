package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "dika_remote_settings")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED
}

enum class GlassEffectsMode {
    FULL, BALANCED, REDUCED, OFF
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val glassEffectsMode: GlassEffectsMode = GlassEffectsMode.FULL,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val buttonSizeMultiplier: Float = 1.0f,
    val developerModeEnabled: Boolean = false,
    val layoutDebugEnabled: Boolean = false,
    val selectedRoomId: String = "room_living"
)

class DataStoreManager(private val context: Context) {

    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val GLASS_MODE_KEY = stringPreferencesKey("glass_effects_mode")
    private val HAPTIC_KEY = booleanPreferencesKey("haptic_enabled")
    private val SOUND_KEY = booleanPreferencesKey("sound_enabled")
    private val DEV_MODE_KEY = booleanPreferencesKey("developer_mode")
    private val LAYOUT_DEBUG_KEY = booleanPreferencesKey("layout_debug")
    private val SELECTED_ROOM_KEY = stringPreferencesKey("selected_room")

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[THEME_KEY] ?: ThemeMode.DARK.name
        val theme = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.DARK }
        val glassModeStr = prefs[GLASS_MODE_KEY] ?: GlassEffectsMode.FULL.name
        val glassMode = try { GlassEffectsMode.valueOf(glassModeStr) } catch (e: Exception) { GlassEffectsMode.FULL }
        AppSettings(
            themeMode = theme,
            glassEffectsMode = glassMode,
            hapticEnabled = prefs[HAPTIC_KEY] ?: true,
            soundEnabled = prefs[SOUND_KEY] ?: false,
            developerModeEnabled = prefs[DEV_MODE_KEY] ?: false,
            layoutDebugEnabled = prefs[LAYOUT_DEBUG_KEY] ?: false,
            selectedRoomId = prefs[SELECTED_ROOM_KEY] ?: "room_living"
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = mode.name
        }
    }

    suspend fun setGlassEffectsMode(mode: GlassEffectsMode) {
        context.dataStore.edit { prefs ->
            prefs[GLASS_MODE_KEY] = mode.name
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HAPTIC_KEY] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_KEY] = enabled
        }
    }

    suspend fun setDeveloperMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DEV_MODE_KEY] = enabled
        }
    }

    suspend fun setLayoutDebugEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[LAYOUT_DEBUG_KEY] = enabled
        }
    }

    suspend fun setSelectedRoom(roomId: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_ROOM_KEY] = roomId
        }
    }
}

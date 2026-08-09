package com.example.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureCredentialStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dika_secure_credentials", Context.MODE_PRIVATE)
    private val keyAlias = "dika_remote_master_key"

    init {
        initKeyStore()
    }

    private fun initKeyStore() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keySpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    fun saveCredential(deviceId: String, key: String, value: String) {
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

            val base64 = Base64.encodeToString(combined, Base64.DEFAULT)
            prefs.edit().putString("${deviceId}_$key", base64).apply()
        } catch (e: Exception) {
            // Fallback plain store if Keystore hardware fails in emulator container
            prefs.edit().putString("${deviceId}_$key", value).apply()
        }
    }

    fun getCredential(deviceId: String, key: String): String? {
        val raw = prefs.getString("${deviceId}_$key", null) ?: return null
        return try {
            val combined = Base64.decode(raw, Base64.DEFAULT)
            if (combined.size <= 12) return raw // Plain fallback
            val iv = combined.copyOfRange(0, 12)
            val encrypted = combined.copyOfRange(12, combined.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            raw // Return plain value if decryption fails
        }
    }

    fun removeCredential(deviceId: String, key: String) {
        prefs.edit().remove("${deviceId}_$key").apply()
    }
}

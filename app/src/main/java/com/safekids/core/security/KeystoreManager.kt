package com.safekids.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages encryption keys using Android Keystore
 * Used for database encryption and sensitive data
 */
object KeystoreManager {
    
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val DB_KEY_ALIAS = "safekids_db_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    
    /**
     * Get or create database encryption key
     */
    fun getOrCreateDbKey(): ByteArray {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        
        val secretKey = if (keyStore.containsAlias(DB_KEY_ALIAS)) {
            keyStore.getKey(DB_KEY_ALIAS, null) as SecretKey
        } else {
            generateKey(DB_KEY_ALIAS)
        }
        
        return secretKey.encoded
    }
    
    /**
     * Generate a new AES key in Keystore
     */
    private fun generateKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()
        
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Encrypt data using Keystore key
     */
    fun encrypt(alias: String, data: ByteArray): Pair<ByteArray, ByteArray> {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        
        val secretKey = keyStore.getKey(alias, null) as SecretKey
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)
        
        return Pair(encrypted, iv)
    }
    
    /**
     * Decrypt data using Keystore key
     */
    fun decrypt(alias: String, encrypted: ByteArray, iv: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        
        val secretKey = keyStore.getKey(alias, null) as SecretKey
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        return cipher.doFinal(encrypted)
    }
    
    /**
     * Check if key exists in Keystore
     */
    fun keyExists(alias: String): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        return keyStore.containsAlias(alias)
    }
    
    /**
     * Delete key from Keystore
     */
    fun deleteKey(alias: String) {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
        keyStore.deleteEntry(alias)
    }
}

package com.safekids.core.security

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Password hashing utility using PBKDF2
 * Secure password storage for parent authentication
 */
@Singleton
class PasswordHasher @Inject constructor() {
    
    private companion object {
        const val ALGORITHM = "PBKDF2WithHmacSHA256"
        const val ITERATIONS = 120_000
        const val KEY_LENGTH = 256
        const val SALT_LENGTH = 16
    }
    
    /**
     * Generate random salt (returns Base64 string)
     */
    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt.toBase64()
    }
    
    /**
     * Hash password with salt using PBKDF2 (accepts and returns Base64 strings)
     */
    fun hashPassword(password: String, saltBase64: String): String {
        val salt = saltBase64.fromBase64()
        val passwordChars = password.toCharArray()
        
        val spec: KeySpec = PBEKeySpec(
            passwordChars,
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hash = factory.generateSecret(spec).encoded
        
        // Clear password from memory
        passwordChars.fill('0')
        
        return hash.toBase64()
    }
    
    /**
     * Verify password against stored hash (accepts Base64 strings)
     */
    fun verifyPassword(
        inputPassword: String,
        storedHashBase64: String,
        storedSaltBase64: String
    ): Boolean {
        val inputHash = hashPassword(inputPassword, storedSaltBase64)
        return inputHash == storedHashBase64
    }
    
    /**
     * Encode byte array to Base64 string for storage
     */
    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }
    
    /**
     * Decode Base64 string to byte array
     */
    private fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }
}

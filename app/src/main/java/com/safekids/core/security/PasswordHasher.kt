package com.safekids.core.security

import android.util.Base64
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password hashing utility using PBKDF2
 * Secure password storage for parent authentication
 */
object PasswordHasher {
    
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    
    /**
     * Generate random salt
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
    
    /**
     * Hash password with salt using PBKDF2
     */
    fun hashPassword(password: CharArray, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(
            password,
            salt,
            ITERATIONS,
            KEY_LENGTH
        )
        
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
    
    /**
     * Verify password against stored hash
     */
    fun verifyPassword(
        inputPassword: CharArray,
        storedSalt: ByteArray,
        storedHash: ByteArray
    ): Boolean {
        val inputHash = hashPassword(inputPassword, storedSalt)
        return inputHash.contentEquals(storedHash)
    }
    
    /**
     * Encode byte array to Base64 string for storage
     */
    fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.NO_WRAP)
    }
    
    /**
     * Decode Base64 string to byte array
     */
    fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.NO_WRAP)
    }
}

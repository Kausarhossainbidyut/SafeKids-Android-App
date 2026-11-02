package com.safekids.domain.repository

import com.safekids.domain.model.ParentUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    
    /**
     * Register a new parent user
     * @param name Full name of the parent
     * @param phone Phone number (unique identifier)
     * @param password Plain text password (will be hashed)
     * @return Result with registered user or error
     */
    suspend fun register(
        name: String,
        phone: String,
        password: String
    ): Result<ParentUser>
    
    /**
     * Login with phone and password
     * @param phone Phone number
     * @param password Plain text password
     * @return Result with logged-in user or error
     */
    suspend fun login(
        phone: String,
        password: String
    ): Result<ParentUser>
    
    /**
     * Logout current user
     */
    suspend fun logout()
    
    /**
     * Get currently logged-in user
     * @return Flow of current user or null
     */
    fun getCurrentUser(): Flow<ParentUser?>
    
    /**
     * Check if user is logged in
     * @return true if logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Verify password for current user
     * @param password Plain text password
     * @return true if password matches
     */
    suspend fun verifyPassword(password: String): Boolean
    
    /**
     * Change password for current user
     * @param oldPassword Current password
     * @param newPassword New password
     * @return Result with success or error
     */
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit>
    
    /**
     * Generate recovery codes for password reset
     * @return List of recovery codes
     */
    suspend fun generateRecoveryCodes(): List<String>
    
    /**
     * Verify recovery code and reset password
     * @param phone Phone number
     * @param recoveryCode Recovery code
     * @param newPassword New password
     * @return Result with success or error
     */
    suspend fun resetPasswordWithRecoveryCode(
        phone: String,
        recoveryCode: String,
        newPassword: String
    ): Result<Unit>
    
    /**
     * Enable biometric authentication
     * @param encryptedPassword Biometric-encrypted password
     * @return Result with success or error
     */
    suspend fun enableBiometric(encryptedPassword: String): Result<Unit>
    
    /**
     * Check if biometric is enabled
     * @return true if enabled
     */
    suspend fun isBiometricEnabled(): Boolean
    
    /**
     * Login with biometric
     * @param decryptedPassword Password decrypted from biometric
     * @return Result with logged-in user or error
     */
    suspend fun loginWithBiometric(decryptedPassword: String): Result<ParentUser>
}

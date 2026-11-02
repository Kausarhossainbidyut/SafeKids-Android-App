package com.safekids.domain.model

/**
 * Domain model for Parent User
 * Represents parent account in the system
 */
data class ParentUser(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Registration request data
 */
data class RegistrationData(
    val name: String,
    val phone: String,
    val password: String,
    val confirmPassword: String
)

/**
 * Login request data
 */
data class LoginData(
    val phone: String,
    val password: String
)

/**
 * Recovery code generated during registration
 */
data class RecoveryCode(
    val code: String,
    val formattedCode: String // e.g., "7G4K-9QXZ-2MPL"
) {
    companion object {
        fun generate(): RecoveryCode {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Excluding similar looking chars
            val code = (1..12)
                .map { chars.random() }
                .joinToString("")
            
            val formatted = code.chunked(4).joinToString("-")
            
            return RecoveryCode(code, formatted)
        }
    }
}

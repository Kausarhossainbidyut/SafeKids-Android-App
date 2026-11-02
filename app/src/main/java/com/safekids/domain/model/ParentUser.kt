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

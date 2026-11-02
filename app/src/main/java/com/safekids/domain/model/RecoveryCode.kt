package com.safekids.domain.model

data class RecoveryCode(
    val code: String,
    val createdAt: Long,
    val expiresAt: Long
)

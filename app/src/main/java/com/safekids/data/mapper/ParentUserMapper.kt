package com.safekids.data.mapper

import com.safekids.data.local.entity.ParentUserEntity
import com.safekids.domain.model.ParentUser

fun ParentUserEntity.toDomain(): ParentUser {
    return ParentUser(
        id = id,
        name = name,
        phone = phone,
        passwordHash = passwordHash,
        createdAt = createdAt,
        biometricEnabled = biometricEnabled
    )
}

fun ParentUser.toEntity(): ParentUserEntity {
    return ParentUserEntity(
        id = id,
        name = name,
        phone = phone,
        passwordHash = passwordHash,
        createdAt = createdAt,
        biometricEnabled = biometricEnabled
    )
}

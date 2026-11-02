package com.safekids.data.mapper

import com.safekids.data.local.entity.ParentUserEntity
import com.safekids.domain.model.ParentUser

/**
 * Maps ParentUserEntity (database) to ParentUser (domain)
 * Only exposes non-sensitive information to domain layer
 */
fun ParentUserEntity.toDomain(): ParentUser {
    return ParentUser(
        id = id,
        name = name,
        phone = phone,
        createdAt = createdAt
    )
}

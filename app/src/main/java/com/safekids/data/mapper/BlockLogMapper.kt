package com.safekids.data.mapper

import com.safekids.data.local.entity.BlockLogEntity
import com.safekids.domain.model.BlockLog
import com.safekids.domain.model.BlockReason
import com.safekids.domain.model.UnblockReason

/**
 * Maps BlockLogEntity (database) to BlockLog (domain)
 */
fun BlockLogEntity.toDomain(): BlockLog {
    return BlockLog(
        id = id,
        packageName = packageName,
        appName = appName,
        blockTimestamp = blockTimestamp,
        unblockTimestamp = unblockTimestamp,
        blockReason = BlockReason.fromString(blockReason),
        unblockReason = unblockReason?.let { UnblockReason.fromString(it) },
        unlockedByParent = unlockedByParent
    )
}

/**
 * Maps BlockLog (domain) to BlockLogEntity (database)
 */
fun BlockLog.toEntity(): BlockLogEntity {
    return BlockLogEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        blockTimestamp = blockTimestamp,
        unblockTimestamp = unblockTimestamp,
        blockReason = blockReason.name,
        unblockReason = unblockReason?.name,
        unlockedByParent = unlockedByParent,
        createdAt = System.currentTimeMillis()
    )
}

/**
 * Maps list of entities to domain models
 */
fun List<BlockLogEntity>.toDomainList(): List<BlockLog> {
    return map { it.toDomain() }
}

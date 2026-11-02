package com.safekids.data.mapper

import com.safekids.data.local.entity.AppUsageEntity
import com.safekids.domain.model.AppUsage

/**
 * Maps AppUsageEntity (database) to AppUsage (domain)
 */
fun AppUsageEntity.toDomain(): AppUsage {
    return AppUsage(
        id = id,
        packageName = packageName,
        appName = appName,
        usageTimeMillis = usageTimeMillis,
        date = date,
        launchCount = launchCount,
        lastUsedTimestamp = lastUsedTimestamp
    )
}

/**
 * Maps AppUsage (domain) to AppUsageEntity (database)
 */
fun AppUsage.toEntity(): AppUsageEntity {
    return AppUsageEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        usageTimeMillis = usageTimeMillis,
        date = date,
        launchCount = launchCount,
        lastUsedTimestamp = lastUsedTimestamp,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Maps list of entities to domain models
 */
fun List<AppUsageEntity>.toDomainList(): List<AppUsage> {
    return map { it.toDomain() }
}

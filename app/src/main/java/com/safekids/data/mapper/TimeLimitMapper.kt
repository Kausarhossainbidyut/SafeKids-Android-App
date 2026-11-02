package com.safekids.data.mapper

import com.safekids.data.local.entity.TimeLimitEntity
import com.safekids.domain.model.TimeLimit

/**
 * Maps TimeLimitEntity (database) to TimeLimit (domain)
 */
fun TimeLimitEntity.toDomain(): TimeLimit {
    return TimeLimit(
        id = id,
        packageName = packageName,
        appName = appName,
        dailyLimitMillis = dailyLimitMillis,
        weekdayLimitMillis = weekdayLimitMillis,
        weekendLimitMillis = weekendLimitMillis,
        currentUsageMillis = currentUsageMillis,
        lastResetTimestamp = lastResetTimestamp,
        warningThresholdPercent = warningThresholdPercent,
        isWarningShown = isWarningShown,
        limitExceeded = isLimitExceeded, // Map isLimitExceeded -> limitExceeded
        isEnabled = isEnabled
    )
}

/**
 * Maps TimeLimit (domain) to TimeLimitEntity (database)
 */
fun TimeLimit.toEntity(): TimeLimitEntity {
    return TimeLimitEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        dailyLimitMillis = dailyLimitMillis,
        weekdayLimitMillis = weekdayLimitMillis,
        weekendLimitMillis = weekendLimitMillis,
        currentUsageMillis = currentUsageMillis,
        lastResetTimestamp = lastResetTimestamp,
        warningThresholdPercent = warningThresholdPercent,
        isWarningShown = isWarningShown,
        isLimitExceeded = limitExceeded, // Map limitExceeded -> isLimitExceeded
        isEnabled = isEnabled,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Maps list of entities to domain models
 */
fun List<TimeLimitEntity>.toDomainList(): List<TimeLimit> {
    return map { it.toDomain() }
}

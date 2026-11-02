package com.safekids.data.mapper

import com.safekids.data.local.entity.InstalledAppEntity
import com.safekids.domain.model.AppCategory
import com.safekids.domain.model.InstalledApp

/**
 * Maps InstalledAppEntity (database) to InstalledApp (domain)
 */
fun InstalledAppEntity.toDomain(): InstalledApp {
    return InstalledApp(
        id = id,
        packageName = packageName,
        appName = appName,
        category = AppCategory.fromString(category),
        isSystemApp = isSystemApp,
        isBlocked = isBlocked,
        versionName = versionName,
        versionCode = versionCode,
        installedTimestamp = installedTimestamp
    )
}

/**
 * Maps InstalledApp (domain) to InstalledAppEntity (database)
 */
fun InstalledApp.toEntity(): InstalledAppEntity {
    return InstalledAppEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        category = category.name,
        isSystemApp = isSystemApp,
        isBlocked = isBlocked,
        versionName = versionName,
        versionCode = versionCode,
        installedTimestamp = installedTimestamp,
        firstDetectedAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Maps list of entities to domain models
 */
fun List<InstalledAppEntity>.toDomainList(): List<InstalledApp> {
    return map { it.toDomain() }
}

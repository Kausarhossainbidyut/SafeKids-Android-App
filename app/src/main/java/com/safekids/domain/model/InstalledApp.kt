package com.safekids.domain.model

/**
 * Domain model for Installed App
 * Represents an app installed on the device
 */
data class InstalledApp(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val category: AppCategory = AppCategory.OTHER,
    val isSystemApp: Boolean = false,
    val isBlocked: Boolean = false,
    val versionName: String? = null,
    val versionCode: Long = 0,
    val installedTimestamp: Long = 0
)

/**
 * App categories for organization
 */
enum class AppCategory(val displayName: String) {
    GAME("Games"),
    SOCIAL("Social Media"),
    EDUCATION("Education"),
    PRODUCTIVITY("Productivity"),
    ENTERTAINMENT("Entertainment"),
    COMMUNICATION("Communication"),
    SHOPPING("Shopping"),
    NEWS("News & Magazines"),
    PHOTO_VIDEO("Photo & Video"),
    MUSIC_AUDIO("Music & Audio"),
    HEALTH_FITNESS("Health & Fitness"),
    TOOLS("Tools"),
    OTHER("Other");
    
    companion object {
        fun fromString(value: String): AppCategory {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}

package com.safekids.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Block Logs
 * Stores history of when apps were blocked/unblocked
 */
@Entity(tableName = "block_logs")
data class BlockLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val packageName: String,
    
    val appName: String,
    
    val blockTimestamp: Long,
    
    val unblockTimestamp: Long? = null, // null if still blocked
    
    val blockReason: String, // "TIME_LIMIT_EXCEEDED", "MANUAL_BLOCK", "SCHEDULE_BLOCK"
    
    val unblockReason: String? = null, // "PARENT_OVERRIDE", "TIME_LIMIT_RESET", "MANUAL_UNBLOCK"
    
    val unlockedByParent: Boolean = false, // Whether parent used password to override
    
    val createdAt: Long = System.currentTimeMillis()
)

# Week 1-2 Remaining Implementation Files

আপনার Android Studio setup complete হলে এই files গুলো create করবেন।

---

## 9️⃣ Repository Layer

### File: `app/src/main/java/com/safekids/data/repository/AuthRepository.kt`

```kotlin
package com.safekids.data.repository

import com.safekids.core.security.PasswordHasher
import com.safekids.core.security.PasswordHasher.fromBase64
import com.safekids.core.security.PasswordHasher.toBase64
import com.safekids.data.local.dao.ParentUserDao
import com.safekids.data.local.entity.ParentUserEntity
import com.safekids.domain.model.LoginData
import com.safekids.domain.model.ParentUser
import com.safekids.domain.model.RecoveryCode
import com.safekids.domain.model.RegistrationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val parentUserDao: ParentUserDao
) {
    
    fun hasUsers(): Flow<Boolean> = parentUserDao.hasUsers()
    
    suspend fun register(data: RegistrationData): AuthResult<Pair<ParentUser, RecoveryCode>> {
        // Validation
        if (data.name.isBlank()) {
            return AuthResult.Error("নাম খালি রাখা যাবে না")
        }
        
        if (data.phone.length != 11 || !data.phone.startsWith("01")) {
            return AuthResult.Error("সঠিক ফোন নম্বর দিন (11 digits, 01 দিয়ে শুরু)")
        }
        
        if (data.password.length < 8) {
            return AuthResult.Error("পাসওয়ার্ড কমপক্ষে 8 অক্ষরের হতে হবে")
        }
        
        if (data.password != data.confirmPassword) {
            return AuthResult.Error("পাসওয়ার্ড মিলছে না")
        }
        
        // Check if phone already exists
        val existing = parentUserDao.findByPhone(data.phone)
        if (existing != null) {
            return AuthResult.Error("এই নম্বর দিয়ে আগেই অ্যাকাউন্ট আছে")
        }
        
        // Generate password hash
        val salt = PasswordHasher.generateSalt()
        val passwordHash = PasswordHasher.hashPassword(data.password.toCharArray(), salt)
        
        // Generate recovery code
        val recoveryCode = RecoveryCode.generate()
        val recoveryHash = PasswordHasher.hashPassword(
            recoveryCode.code.toCharArray(),
            salt
        )
        
        // Create entity
        val entity = ParentUserEntity(
            name = data.name.trim(),
            phone = data.phone.trim(),
            passwordSalt = salt.toBase64(),
            passwordHash = passwordHash.toBase64(),
            recoveryCodeHash = recoveryHash.toBase64()
        )
        
        // Insert to database
        val userId = parentUserDao.insert(entity)
        
        val user = ParentUser(
            id = userId,
            name = entity.name,
            phone = entity.phone,
            createdAt = entity.createdAt
        )
        
        return AuthResult.Success(user to recoveryCode)
    }
    
    suspend fun login(data: LoginData): AuthResult<ParentUser> {
        val entity = parentUserDao.findByPhone(data.phone)
            ?: return AuthResult.Error("ভুল নম্বর বা পাসওয়ার্ড")
        
        val salt = entity.passwordSalt.fromBase64()
        val storedHash = entity.passwordHash.fromBase64()
        
        val isValid = PasswordHasher.verifyPassword(
            data.password.toCharArray(),
            salt,
            storedHash
        )
        
        if (!isValid) {
            return AuthResult.Error("ভুল নম্বর বা পাসওয়ার্ড")
        }
        
        // Update last login
        parentUserDao.updateLastLogin(entity.id)
        
        val user = ParentUser(
            id = entity.id,
            name = entity.name,
            phone = entity.phone,
            createdAt = entity.createdAt
        )
        
        return AuthResult.Success(user)
    }
    
    suspend fun resetPassword(
        phone: String,
        recoveryCode: String,
        newPassword: String
    ): AuthResult<Unit> {
        val entity = parentUserDao.findByPhone(phone)
            ?: return AuthResult.Error("অ্যাকাউন্ট পাওয়া যায়নি")
        
        val salt = entity.passwordSalt.fromBase64()
        val storedRecoveryHash = entity.recoveryCodeHash?.fromBase64()
            ?: return AuthResult.Error("রিকভারি কোড সেট করা নেই")
        
        val isValidRecovery = PasswordHasher.verifyPassword(
            recoveryCode.replace("-", "").toCharArray(),
            salt,
            storedRecoveryHash
        )
        
        if (!isValidRecovery) {
            return AuthResult.Error("রিকভারি কোড মিলছে না")
        }
        
        if (newPassword.length < 8) {
            return AuthResult.Error("নতুন পাসওয়ার্ড কমপক্ষে 8 অক্ষরের হতে হবে")
        }
        
        // Generate new password hash
        val newSalt = PasswordHasher.generateSalt()
        val newHash = PasswordHasher.hashPassword(newPassword.toCharArray(), newSalt)
        
        parentUserDao.updatePassword(
            entity.id,
            newSalt.toBase64(),
            newHash.toBase64()
        )
        
        return AuthResult.Success(Unit)
    }
}
```

---

## 🔟 UI Layer - Theme

### File: `app/src/main/java/com/safekids/ui/theme/Color.kt`

```kotlin
package com.safekids.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// SafeKids Brand Colors
val SafeKidsPrimary = Color(0xFF2196F3) // Blue
val SafeKidsSecondary = Color(0xFF4CAF50) // Green
val SafeKidsError = Color(0xFFF44336) // Red
val SafeKidsWarning = Color(0xFFFF9800) // Orange
val SafeKidsSuccess = Color(0xFF4CAF50) // Green
```

### File: `app/src/main/java/com/safekids/ui/theme/Type.kt`

```kotlin
package com.safekids.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Typography with Bengali font support
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### File: `app/src/main/java/com/safekids/ui/theme/Theme.kt`

```kotlin
package com.safekids.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SafeKidsPrimary,
    secondary = SafeKidsSecondary,
    tertiary = Pink80,
    error = SafeKidsError
)

private val LightColorScheme = lightColorScheme(
    primary = SafeKidsPrimary,
    secondary = SafeKidsSecondary,
    tertiary = Pink40,
    error = SafeKidsError
)

@Composable
fun SafeKidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 1️⃣1️⃣ ViewModels

চলবে পরের message এ (character limit এর কারণে)...

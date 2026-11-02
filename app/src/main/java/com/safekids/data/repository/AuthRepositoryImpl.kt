package com.safekids.data.repository

import com.safekids.core.security.PasswordHasher
import com.safekids.data.local.dao.ParentUserDao
import com.safekids.data.local.entity.ParentUserEntity
import com.safekids.data.mapper.toDomain
import com.safekids.domain.model.ParentUser
import com.safekids.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val parentUserDao: ParentUserDao,
    private val passwordHasher: PasswordHasher
) : AuthRepository {

    private var currentUserId: String? = null

    override suspend fun register(
        name: String,
        phone: String,
        password: String
    ): Result<ParentUser> {
        return try {
            val existing = parentUserDao.getUserByPhone(phone)
            if (existing != null) {
                return Result.failure(Exception("Phone already registered"))
            }

            val hashedPassword = passwordHasher.hashPassword(password)
            val userId = java.util.UUID.randomUUID().toString()
            val user = ParentUserEntity(
                id = userId,
                name = name,
                phone = phone,
                passwordHash = hashedPassword,
                createdAt = System.currentTimeMillis()
            )

            parentUserDao.insert(user)
            currentUserId = userId

            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(phone: String, password: String): Result<ParentUser> {
        return try {
            val user = parentUserDao.getUserByPhone(phone)
                ?: return Result.failure(Exception("User not found"))

                return Result.failure(Exception("Invalid password"))
            }

            currentUserId = user.id
            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        currentUserId = null
    }

    override fun getCurrentUser(): Flow<ParentUser?> {
        return parentUserDao.observeUser()
            .map { it?.toDomain() }
    }

    override suspend fun isLoggedIn(): Boolean {
        return currentUserId != null
    }

    override suspend fun verifyPassword(password: String): Boolean {
        val userId = currentUserId ?: return false
        val user = parentUserDao.getUserById(userId) ?: return false
        return passwordHasher.verifyPassword(password, user.passwordHash)
    }
}

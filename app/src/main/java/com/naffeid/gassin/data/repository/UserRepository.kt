package com.naffeid.gassin.data.repository

import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.preference.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserRepository(
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: User) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<User> {
        return userPreference.getSession()
    }

    fun checkUserRole(role: String): Boolean {
        return runBlocking {
            val userRole = userPreference.getRole().first()
            userRole == role
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

}
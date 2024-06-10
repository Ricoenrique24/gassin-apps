package com.naffeid.gassin.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.naffeid.gassin.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: User) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.id
            preferences[NAME_KEY] = user.name
            preferences[USERNAME_KEY] = user.username
            preferences[EMAIL_KEY] = user.email
            preferences[PHONE_KEY] = user.phone
            preferences[ROLE_KEY] = user.role
            preferences[API_KEY] = user.apikey
            preferences[TOKEN_FCM_KEY] = user.tokenfcm
        }
    }

    fun getSession(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[ID_KEY] ?: 0,
                preferences[NAME_KEY] ?: "",
                preferences[USERNAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ROLE_KEY] ?: "",
                preferences[API_KEY] ?: "",
                preferences[TOKEN_FCM_KEY] ?: "",
            )
        }
    }

    fun getRole(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ROLE_KEY] ?: ""
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val ID_KEY = intPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val API_KEY = stringPreferencesKey("apikey")
        private val TOKEN_FCM_KEY = stringPreferencesKey("tokenfcm")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.naffeid.gassin.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.naffeid.gassin.data.model.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.employeeDataStore: DataStore<Preferences> by preferencesDataStore(name = "employee_data")

class EmployeePreference private constructor(private val employeeDataStore: DataStore<Preferences>) {

    suspend fun saveEmployee(employee: Employee) {
        employeeDataStore.edit { preferences ->
            preferences[ID_KEY] = employee.id
            preferences[NAME_KEY] = employee.name
            preferences[USERNAME_KEY] = employee.username
            preferences[EMAIL_KEY] = employee.email
            preferences[PHONE_KEY] = employee.phone
            preferences[ROLE_KEY] = employee.role
        }
    }

    fun getEmployee(): Flow<Employee> {
        return employeeDataStore.data.map { preferences ->
            Employee(
                preferences[ID_KEY] ?: 0,
                preferences[NAME_KEY] ?: "",
                preferences[USERNAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ROLE_KEY] ?: ""
            )
        }
    }

    suspend fun deleteEmployee() {
        employeeDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: EmployeePreference? = null
        private val ID_KEY = intPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val EMAIL_KEY = stringPreferencesKey("email")

        fun getInstance(employeeDataStore: DataStore<Preferences>): EmployeePreference {
            return INSTANCE ?: synchronized(this) {
                val instance = EmployeePreference(employeeDataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
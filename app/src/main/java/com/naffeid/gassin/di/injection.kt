package com.naffeid.gassin.di

import android.content.Context
import com.naffeid.gassin.data.preference.EmployeePreference
import com.naffeid.gassin.data.preference.StorePreference
import com.naffeid.gassin.data.preference.UserPreference
import com.naffeid.gassin.data.preference.dataStore
import com.naffeid.gassin.data.preference.employeeDataStore
import com.naffeid.gassin.data.preference.storeDataStore
import com.naffeid.gassin.data.remote.api.ApiConfig
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
    fun provideEmployeeRepository(context: Context): EmployeeRepository {
        val pref = EmployeePreference.getInstance(context.employeeDataStore)
        return EmployeeRepository.getInstance(pref)
    }
    fun provideStoreRepository(context: Context): StoreRepository {
        val prefStore = StorePreference.getInstance(context.storeDataStore)
        return StoreRepository.getInstance(prefStore)
    }

}
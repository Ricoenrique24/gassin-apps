package com.naffeid.gassin.di

import android.content.Context
import com.naffeid.gassin.data.preference.CustomerPreference
import com.naffeid.gassin.data.preference.EmployeePreference
import com.naffeid.gassin.data.preference.StorePreference
import com.naffeid.gassin.data.preference.UserPreference
import com.naffeid.gassin.data.preference.customerDataStore
import com.naffeid.gassin.data.preference.dataStore
import com.naffeid.gassin.data.preference.employeeDataStore
import com.naffeid.gassin.data.preference.storeDataStore
import com.naffeid.gassin.data.remote.api.ApiConfig
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference)
    }
    fun provideEmployeeRepository(context: Context): EmployeeRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val employeePreference = EmployeePreference.getInstance(context.employeeDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return EmployeeRepository.getInstance(employeePreference, apiService)
    }
    fun provideStoreRepository(context: Context): StoreRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val storePreference = StorePreference.getInstance(context.storeDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoreRepository.getInstance(storePreference, apiService)
    }
    fun provideCustomerRepository(context: Context): CustomerRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val customerPreference = CustomerPreference.getInstance(context.customerDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return CustomerRepository.getInstance(customerPreference, apiService)
    }
}
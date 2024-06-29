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
import com.naffeid.gassin.data.repository.DashboardRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.OperationTransactionRepository
import com.naffeid.gassin.data.repository.PurchaseTransactionRepository
import com.naffeid.gassin.data.repository.ResupplyTransactionRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.TransactionRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository(userPreference)
    }
    fun provideEmployeeRepository(context: Context): EmployeeRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val employeePreference = EmployeePreference.getInstance(context.employeeDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return EmployeeRepository(employeePreference, apiService)
    }
    fun provideStoreRepository(context: Context): StoreRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val storePreference = StorePreference.getInstance(context.storeDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return StoreRepository(storePreference, apiService)
    }
    fun provideCustomerRepository(context: Context): CustomerRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val customerPreference = CustomerPreference.getInstance(context.customerDataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return CustomerRepository(customerPreference, apiService)
    }
    fun providePurchaseTransactionRepository(context: Context): PurchaseTransactionRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return PurchaseTransactionRepository(apiService)
    }
    fun provideResupplyTransactionRepository(context: Context): ResupplyTransactionRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return ResupplyTransactionRepository(apiService)
    }
    fun provideTransactionRepository(context: Context): TransactionRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return TransactionRepository(apiService)
    }
    fun provideOperationTransactionRepository(context: Context): OperationTransactionRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return OperationTransactionRepository(apiService)
    }
    fun provideDashboardRepository(context: Context): DashboardRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.apikey)
        return DashboardRepository(apiService)
    }
}
package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.preference.StorePreference
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.LoginResponse
import com.naffeid.gassin.data.utils.Result
import kotlinx.coroutines.flow.Flow

class StoreRepository private constructor(
    private val storePreference: StorePreference,
    private val apiService: ApiService
) {

    // API Store
    fun showAllStore(): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllStore()
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun createNewStore(
        name: String,
        phone: String,
        address: String,
        linkMap: String,
        price: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewStore(name, phone, address, linkMap, price)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun showStore(
        id:String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showStore(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun updateStore(
        id:String,
        name: String,
        phone: String,
        address: String,
        linkMap: String,
        price: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.updateStore(id, name, phone, address, linkMap, price)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun deleteStore(id:String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deleteStore(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Store Preference
    suspend fun saveStore(store: Store) {
        storePreference.saveStore(store)
    }

    fun getStore(): Flow<Store> {
        return storePreference.getStore()
    }

    suspend fun deleteStore() {
        storePreference.deleteStore()
    }


    companion object {
        @Volatile
        private var instance: StoreRepository? = null
        fun getInstance(
            storePreference: StorePreference,
            apiService: ApiService
        ): StoreRepository =
            instance ?: synchronized(this) {
                instance ?: StoreRepository(storePreference, apiService)
            }.also { instance = it }
    }
}
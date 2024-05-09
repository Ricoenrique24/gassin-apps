package com.naffeid.gassin.data.repository

import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.preference.StorePreference
import kotlinx.coroutines.flow.Flow

class StoreRepository private constructor(
    private val storePreference: StorePreference
) {


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
            storePreference: StorePreference
        ): StoreRepository =
            instance ?: synchronized(this) {
                instance ?: StoreRepository(storePreference)
            }.also { instance = it }
    }
}
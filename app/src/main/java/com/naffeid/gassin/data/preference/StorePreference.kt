package com.naffeid.gassin.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.naffeid.gassin.data.model.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.storeDataStore: DataStore<Preferences> by preferencesDataStore(name = "store_data")

class StorePreference private constructor(private val storeDataStore: DataStore<Preferences>) {

    suspend fun saveStore(store: Store) {
        storeDataStore.edit { preferences ->
            preferences[ID_KEY] = store.id
            preferences[NAME_KEY] = store.name
            preferences[PHONE_KEY] = store.phone
            preferences[ADDRESS_KEY] = store.address
            preferences[LINK_MAP_KEY] = store.linkMap
            preferences[PRICE_KEY] = store.price
        }
    }

    fun getStore(): Flow<Store> {
        return storeDataStore.data.map { preferences ->
            Store(
                preferences[ID_KEY] ?: 0,
                preferences[NAME_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ADDRESS_KEY] ?: "",
                preferences[LINK_MAP_KEY] ?: "",
                preferences[PRICE_KEY] ?: "",
            )
        }
    }

    suspend fun deleteStore() {
        storeDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StorePreference? = null
        private val ID_KEY = intPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val LINK_MAP_KEY = stringPreferencesKey("link_map")
        private val PRICE_KEY = stringPreferencesKey("price")

        fun getInstance(storeDataStore: DataStore<Preferences>): StorePreference {
            return INSTANCE ?: synchronized(this) {
                val instance = StorePreference(storeDataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
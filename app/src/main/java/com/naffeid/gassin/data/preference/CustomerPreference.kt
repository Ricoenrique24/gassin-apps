package com.naffeid.gassin.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.naffeid.gassin.data.model.Customer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.customerDataStore: DataStore<Preferences> by preferencesDataStore(name = "customer_data")

class CustomerPreference private constructor(private val customerDataStore: DataStore<Preferences>) {

    suspend fun saveCustomer(customer: Customer) {
        customerDataStore.edit { preferences ->
            preferences[ID_KEY] = customer.id
            preferences[NAME_KEY] = customer.name
            preferences[PHONE_KEY] = customer.phone
            preferences[ADDRESS_KEY] = customer.address
            preferences[LINK_MAP_KEY] = customer.linkMap
            preferences[PRICE_KEY] = customer.price
        }
    }

    fun getCustomer(): Flow<Customer> {
        return customerDataStore.data.map { preferences ->
            Customer(
                preferences[ID_KEY] ?: 0,
                preferences[NAME_KEY] ?: "",
                preferences[PHONE_KEY] ?: "",
                preferences[ADDRESS_KEY] ?: "",
                preferences[LINK_MAP_KEY] ?: "",
                preferences[PRICE_KEY] ?: "",
            )
        }
    }

    suspend fun deleteCustomer() {
        customerDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CustomerPreference? = null
        private val ID_KEY = intPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val PHONE_KEY = stringPreferencesKey("phone")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val LINK_MAP_KEY = stringPreferencesKey("link_map")
        private val PRICE_KEY = stringPreferencesKey("price")

        fun getInstance(customerDataStore: DataStore<Preferences>): CustomerPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = CustomerPreference(customerDataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
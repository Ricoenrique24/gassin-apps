package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.preference.CustomerPreference
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.CustomerResponse
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.SingleCustomerResponse
import com.naffeid.gassin.data.utils.Result
import kotlinx.coroutines.flow.Flow

class CustomerRepository (
    private val customerPreference: CustomerPreference,
    private val apiService: ApiService
) {


    // API Customer
    fun showAllCustomer(): LiveData<Result<CustomerResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllCustomer()
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun createNewCustomer(
        name: String,
        phone: String,
        address: String,
        linkMap: String,
        price: String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewCustomer(name, phone, address, linkMap, price)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun showCustomer(
        id:String
    ): LiveData<Result<SingleCustomerResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showCustomer(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun updateCustomer(
        id:String,
        name: String,
        phone: String,
        address: String,
        linkMap: String,
        price: String
    ): LiveData<Result<SingleCustomerResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.updateCustomer(id, name, phone, address, linkMap, price)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun deleteCustomer(id:String): LiveData<Result<CustomerResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deleteCustomer(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun searchCustomer(query: String): LiveData<Result<CustomerResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.searchCustomer(query)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Customer Preference
    suspend fun deleteCustomer() {
        customerPreference.deleteCustomer()
    }

    suspend fun saveCustomer(customer: Customer) {
        customerPreference.saveCustomer(customer)
    }

    fun getCustomer(): Flow<Customer> {
        return customerPreference.getCustomer()
    }

}
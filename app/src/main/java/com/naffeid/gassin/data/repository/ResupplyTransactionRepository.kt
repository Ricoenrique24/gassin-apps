package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.ResupplyTransactionResponse
import com.naffeid.gassin.data.remote.response.SingleResupplyResponse
import com.naffeid.gassin.data.utils.Result

class ResupplyTransactionRepository(
    private val apiService: ApiService
) {

    // API Resupply Transaction
    fun showAllResupplyTransaction(): LiveData<Result<ResupplyTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllResupplyTransaction()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun createNewResupplyTransaction(
        idStore: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewResupplyTransaction(idStore, idUser, qty, totalPayment)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showResupplyTransaction(
        id: String
    ): LiveData<Result<SingleResupplyResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showResupplyTransaction(id)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateResupplyTransaction(
        id: String,
        idStore: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ): LiveData<Result<SingleResupplyResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.updateResupplyTransaction(id, idStore, idUser, qty, totalPayment)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun deleteResupplyTransaction(id: String): LiveData<Result<ResupplyTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deleteResupplyTransaction(id)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchResupplyTransaction(query: String): LiveData<Result<ResupplyTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.searchResupplyTransaction(query)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showFilteredResupplyTransaction(status: String, filterBy:String): LiveData<Result<ResupplyTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showFilteredResupplyTransaction(status, filterBy)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun cancelledResupplyTransaction(id: String): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.cancelledResupplyTransaction(id)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.SingleTransactionResponse
import com.naffeid.gassin.data.remote.response.TransactionResponse
import com.naffeid.gassin.data.utils.Result

class TransactionRepository(
    private val apiService: ApiService
) {
    // API Transaction
    fun showAllTransaction(): LiveData<Result<TransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllTransaction()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showTransaction(
        id: String,
        type:String
    ): LiveData<Result<SingleTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showTransaction(id, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showAllActiveTransaction(): LiveData<Result<TransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllActiveTransaction()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showAllActiveTransactionManager(): LiveData<Result<TransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllActiveTransactionManager()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun inProgressTransaction(
        id: String,
        type:String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.inProgressTransaction(id, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun completedTransaction(
        id: String,
        type:String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.completedTransaction(id, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun cancelledTransaction(
        id: String,
        type:String,
        note:String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.cancelledTransaction(id, type, note)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
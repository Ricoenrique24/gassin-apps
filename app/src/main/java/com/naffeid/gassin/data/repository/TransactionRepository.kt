package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
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
}
package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.OperationTransactionResponse
import com.naffeid.gassin.data.remote.response.SingleOperationResponse
import com.naffeid.gassin.data.utils.Result

class OperationTransactionRepository(
    private val apiService: ApiService
) {

    // API Operation Transaction
    fun showAllOperationTransaction(): LiveData<Result<OperationTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllOperationTransaction()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun createNewOperationTransaction(
        idTransaction: String,
        note: String,
        totalPayment: String,
        type: String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewOperationTransaction(idTransaction, note, totalPayment, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun showOperationTransaction(
        id: String,
        type: String,
    ): LiveData<Result<SingleOperationResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showOperationTransaction(id, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showOperationTransactionManager(
        id: String,
        type: String,
    ): LiveData<Result<SingleOperationResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showOperationTransactionManager(id, type)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateOperationTransaction(
        id: String,
        type: String,
        verified: Boolean
    ): LiveData<Result<SingleOperationResponse>> = liveData {
        emit(Result.Loading)
        try {
            val verifiedInt = if (verified) 1 else 0
            val client = apiService.updateOperationTransaction(id, type, verifiedInt)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchOperationTransaction(query: String): LiveData<Result<OperationTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.searchOperationTransaction(query)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
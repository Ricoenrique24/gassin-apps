package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.PurchaseTransactionResponse
import com.naffeid.gassin.data.remote.response.SinglePurchaseResponse
import com.naffeid.gassin.data.utils.Result

class PurchaseTransactionRepository(
    private val apiService: ApiService
) {

    // API Purchase Transaction
    fun showAllPurchaseTransaction(): LiveData<Result<PurchaseTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllPurchaseTransaction()
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun createNewPurchaseTransaction(
        idCustomer: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewPurchaseTransaction(idCustomer, idUser, qty, totalPayment)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun showPurchaseTransaction(
        id: String
    ): LiveData<Result<SinglePurchaseResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showPurchaseTransaction(id)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updatePurchaseTransaction(
        id: String,
        idCustomer: String,
        idUser: String,
        qty: String,
        totalPayment: String,
        status: String,
        note: String
    ): LiveData<Result<SinglePurchaseResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.updatePurchaseTransaction(id, idCustomer, idUser, qty, totalPayment, status, note)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun deletePurchaseTransaction(id: String): LiveData<Result<PurchaseTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deletePurchaseTransaction(id)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchPurchaseTransaction(query: String): LiveData<Result<PurchaseTransactionResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.searchPurchaseTransaction(query)
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
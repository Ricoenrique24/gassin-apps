package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.RevenueResponse
import com.naffeid.gassin.data.remote.response.StockResponse
import com.naffeid.gassin.data.utils.Result

class DashboardRepository (
    private val apiService: ApiService
) {


    // API Dashboard
    fun getAvailableStockQuantity(): LiveData<Result<StockResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.availableStockQuantity()
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun getRevenueToday(): LiveData<Result<RevenueResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.revenueToday()
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }

}
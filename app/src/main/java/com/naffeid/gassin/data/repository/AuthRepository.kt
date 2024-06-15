package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.LoginResponse
import com.naffeid.gassin.data.utils.Result

class AuthRepository(
    private val apiService: ApiService
) {
    fun login(
        userEmail: String,
        userPassword: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(userEmail, userPassword)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

}
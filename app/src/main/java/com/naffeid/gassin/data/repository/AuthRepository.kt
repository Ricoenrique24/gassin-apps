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
        userPassword: String,
        userFcmToken:String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(userEmail, userPassword, userFcmToken)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun showUser(
        id:String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showUser(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun updateUser(
        id: String,
        name: String,
        username: String,
        email: String,
        password: String?,
        phone: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = if (password != null) {
                apiService.updateUser(id, name, username, email, password, phone)
            } else {
                apiService.updateUserWithoutPassword(id, name, username, email, phone)
            }
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
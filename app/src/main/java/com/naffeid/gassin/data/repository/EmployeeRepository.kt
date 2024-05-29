package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.preference.EmployeePreference
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.LoginResponse
import com.naffeid.gassin.data.utils.Result
import kotlinx.coroutines.flow.Flow

class EmployeeRepository private constructor(
    private val employeePreference: EmployeePreference,
    private val apiService: ApiService
) {


    // API Employee
    fun showAllEmployee(): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showAllEmployee()
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun createNewEmployee(
        name: String,
        username: String,
        email: String,
        password: String,
        phone: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.createNewEmployee(name, username, email, password, phone)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun showEmployee(
        id:String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.showEmployee(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun updateEmployee(
        id:String,
        name: String,
        username: String,
        email: String,
        password: String,
        phone: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.updateEmployee(id, name, username, email, password, phone)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun deleteEmployee(id:String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deleteEmployee(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Employee Preference
    suspend fun deleteEmployee() {
        employeePreference.deleteEmployee()
    }

    suspend fun saveEmployee(employee: Employee) {
        employeePreference.saveEmployee(employee)
    }

    fun getEmployee(): Flow<Employee> {
        return employeePreference.getEmployee()
    }

    companion object {
        @Volatile
        private var instance: EmployeeRepository? = null
        fun getInstance(
            employeePreference: EmployeePreference,
            apiService: ApiService
        ): EmployeeRepository =
            instance ?: synchronized(this) {
                instance ?: EmployeeRepository(employeePreference, apiService)
            }.also { instance = it }
    }
}
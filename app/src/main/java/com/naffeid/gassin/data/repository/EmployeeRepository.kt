package com.naffeid.gassin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.preference.EmployeePreference
import com.naffeid.gassin.data.remote.api.ApiService
import com.naffeid.gassin.data.remote.response.EmployeeResponse
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.SingleEmployeeResponse
import com.naffeid.gassin.data.utils.Result
import kotlinx.coroutines.flow.Flow

class EmployeeRepository(
    private val employeePreference: EmployeePreference,
    private val apiService: ApiService
) {


    // API Employee
    fun showAllEmployee(): LiveData<Result<EmployeeResponse>> = liveData {
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
    ): LiveData<Result<MessageResponse>> = liveData {
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
    ): LiveData<Result<SingleEmployeeResponse>> = liveData {
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
        id: String,
        name: String,
        username: String,
        email: String,
        password: String?,
        phone: String
    ): LiveData<Result<SingleEmployeeResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = if (password != null) {
                apiService.updateEmployee(id, name, username, email, password, phone)
            } else {
                apiService.updateEmployeeWithoutPassword(id, name, username, email, phone)
            }
            emit(Result.Success(client))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun deleteEmployee(id:String): LiveData<Result<EmployeeResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.deleteEmployee(id)
            emit(Result.Success(client))
        } catch (e: Exception)
        {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchEmployee(query: String): LiveData<Result<EmployeeResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = apiService.searchEmployee(query)
            emit(Result.Success(client))
        } catch (e: Exception) {
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

}
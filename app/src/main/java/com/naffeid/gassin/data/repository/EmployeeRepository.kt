package com.naffeid.gassin.data.repository

import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.preference.EmployeePreference
import kotlinx.coroutines.flow.Flow

class EmployeeRepository private constructor(
    private val employeePreference: EmployeePreference
) {

    suspend fun saveEmployee(employee: Employee) {
        employeePreference.saveEmployee(employee)
    }

    fun getSession(): Flow<Employee> {
        return employeePreference.getEmployee()
    }

    suspend fun logout() {
        employeePreference.deleteEmployee()
    }


    companion object {
        @Volatile
        private var instance: EmployeeRepository? = null
        fun getInstance(
            employeePreference: EmployeePreference
        ): EmployeeRepository =
            instance ?: synchronized(this) {
                instance ?: EmployeeRepository(employeePreference)
            }.also { instance = it }
    }
}
package com.naffeid.gassin.ui.pages.manager.choose.employee

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.repository.EmployeeRepository
import kotlinx.coroutines.launch

class ChooseEmployeeViewModel(private val employeeRepository: EmployeeRepository) : ViewModel() {
    fun showAllEmployee() = employeeRepository.showAllEmployee()
    fun searchEmployee(query: String) = employeeRepository.searchEmployee(query)
    fun getEmployee(): LiveData<Employee> {
        return employeeRepository.getEmployee().asLiveData()
    }

    fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.saveEmployee(employee)
        }
    }
    fun deleteEmployee(){
        viewModelScope.launch {
            employeeRepository.deleteEmployee()
        }
    }
}
package com.naffeid.gassin.ui.pages.manager.employee.index

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.EmployeeRepository

class IndexEmployeeViewModel(private val employeeRepository: EmployeeRepository) : ViewModel() {
    fun showAllEmployee() = employeeRepository.showAllEmployee()
    fun searchEmployee(query: String) = employeeRepository.searchEmployee(query)
}
package com.naffeid.gassin.ui.pages.manager.employee.create

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.EmployeeRepository

class CreateEmployeeViewModel(private val employeeRepository: EmployeeRepository) : ViewModel() {
    fun createNewEmployee(name: String, username: String, email: String, password: String, phone: String) = employeeRepository.createNewEmployee(name, username, email, password, phone)
}
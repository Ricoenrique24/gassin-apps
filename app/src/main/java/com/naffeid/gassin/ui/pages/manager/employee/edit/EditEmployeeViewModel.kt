package com.naffeid.gassin.ui.pages.manager.employee.edit

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.EmployeeRepository

class EditEmployeeViewModel(private val employeeRepository: EmployeeRepository) : ViewModel() {
    fun showEmployee(id: String) = employeeRepository.showEmployee(id)
    fun updateEmployee(id:String, name: String, username: String, email: String, password: String?, phone: String) = employeeRepository.updateEmployee(id, name, username, email, password, phone)
}
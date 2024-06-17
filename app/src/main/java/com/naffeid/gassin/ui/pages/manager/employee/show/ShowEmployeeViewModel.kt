package com.naffeid.gassin.ui.pages.manager.employee.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.EmployeeRepository

class ShowEmployeeViewModel(private val employeeRepository: EmployeeRepository) : ViewModel() {
    fun showEmployee(id: String) = employeeRepository.showEmployee(id)
    fun deleteEmployee(id: String) = employeeRepository.deleteEmployee(id)
}
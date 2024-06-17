package com.naffeid.gassin.ui.pages.manager.customer.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.CustomerRepository

class ShowCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun showCustomer(id: String) = customerRepository.showCustomer(id)
    fun deleteCustomer(id: String) = customerRepository.deleteCustomer(id)
}
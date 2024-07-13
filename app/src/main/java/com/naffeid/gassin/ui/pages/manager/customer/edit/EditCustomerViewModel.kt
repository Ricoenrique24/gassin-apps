package com.naffeid.gassin.ui.pages.manager.customer.edit

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.CustomerRepository

class EditCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun showCustomer(id: String) = customerRepository.showCustomer(id)
    fun updateCustomer(id: String, name: String, phone: String, address: String, linkMap: String, price: String) = customerRepository.updateCustomer(id, name, phone, address, linkMap, price)
}
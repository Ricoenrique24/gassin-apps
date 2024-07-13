package com.naffeid.gassin.ui.pages.manager.customer.create

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.CustomerRepository

class CreateCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun createNewCustomer(name: String, phone: String, address: String, linkMap: String, price: String) = customerRepository.createNewCustomer(name, phone, address, linkMap, price)
}
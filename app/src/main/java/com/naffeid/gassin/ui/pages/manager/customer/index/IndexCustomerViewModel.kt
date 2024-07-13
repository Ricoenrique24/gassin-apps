package com.naffeid.gassin.ui.pages.manager.customer.index

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.StoreRepository

class IndexCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun showAllCustomer() = customerRepository.showAllCustomer()
    fun searchCustomer(query: String) = customerRepository.searchCustomer(query)
}
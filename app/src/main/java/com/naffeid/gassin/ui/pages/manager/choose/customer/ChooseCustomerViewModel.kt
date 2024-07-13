package com.naffeid.gassin.ui.pages.manager.choose.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class ChooseCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun showAllCustomer() = customerRepository.showAllCustomer()
    fun searchCustomer(query: String) = customerRepository.searchCustomer(query)

    fun getCustomer(): LiveData<Customer> {
        return customerRepository.getCustomer().asLiveData()
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            customerRepository.saveCustomer(customer)
        }
    }
    fun deleteCustomer(){
        viewModelScope.launch {
            customerRepository.deleteCustomer()
        }
    }
}
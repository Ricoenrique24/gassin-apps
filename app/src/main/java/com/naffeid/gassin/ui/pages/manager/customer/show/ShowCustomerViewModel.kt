package com.naffeid.gassin.ui.pages.manager.customer.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class ShowCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {
    fun showCustomer(id: String) = customerRepository.showCustomer(id)
    fun deleteCustomer(id: String) = customerRepository.deleteCustomer(id)
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
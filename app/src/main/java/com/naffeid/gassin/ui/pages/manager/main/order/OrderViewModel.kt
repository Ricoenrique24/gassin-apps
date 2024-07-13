package com.naffeid.gassin.ui.pages.manager.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.PurchaseTransactionRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class OrderViewModel(
    private val userRepository: UserRepository,
    private val purchaseTransactionRepository: PurchaseTransactionRepository,
    private val customerRepository: CustomerRepository,
    private val authRepository: AuthRepository
    ) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun checkUserRole(role: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isRoleMatch = userRepository.checkUserRole(role)
            callback(isRoleMatch)
        }
    }
    fun showUser(id:String) = authRepository.showUser(id)
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }
    fun showAllPurchaseTransaction() = purchaseTransactionRepository.showAllPurchaseTransaction()

    fun showCustomer(id:String) = customerRepository.showCustomer(id)
    fun searchPurchaseTransaction(query: String) = purchaseTransactionRepository.searchPurchaseTransaction(query)
    fun showFilteredPurchaseTransaction(status: String, filterBy:String) = purchaseTransactionRepository.showFilteredPurchaseTransaction(status, filterBy)
}
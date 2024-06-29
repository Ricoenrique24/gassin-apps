package com.naffeid.gassin.ui.pages.manager.main.cost

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.OperationTransactionRepository
import com.naffeid.gassin.data.repository.UserRepository

class CostViewModel(
    private val userRepository: UserRepository,
    private val operationTransactionRepository: OperationTransactionRepository
) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun showAllOperationTransaction() = operationTransactionRepository.showAllOperationTransaction()
    fun searchOperationTransaction(query:String) = operationTransactionRepository.searchOperationTransaction(query)

}
package com.naffeid.gassin.ui.pages.employee.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.TransactionRepository
import com.naffeid.gassin.data.repository.UserRepository

class HomeEmployeeViewModel(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun showAllTransaction() = transactionRepository.showAllTransaction()
}
package com.naffeid.gassin.ui.pages.manager.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.DashboardRepository
import com.naffeid.gassin.data.repository.TransactionRepository
import com.naffeid.gassin.data.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val dashboardRepository: DashboardRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun getAvailableStockQuantity() = dashboardRepository.getAvailableStockQuantity()
    fun getRevenueToday() = dashboardRepository.getRevenueToday()
    fun showAllActiveTransactionManager() = transactionRepository.showAllActiveTransactionManager()
}
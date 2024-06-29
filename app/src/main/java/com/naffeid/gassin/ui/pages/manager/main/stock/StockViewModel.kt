package com.naffeid.gassin.ui.pages.manager.main.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.ResupplyTransactionRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository

class StockViewModel(
    private val userRepository: UserRepository,
    private val resupplyTransactionRepository: ResupplyTransactionRepository,
    private val storeRepository: StoreRepository
    ) : ViewModel() {
        fun getSession(): LiveData<User> {
            return userRepository.getSession().asLiveData()
        }

        fun showAllResupplyTransaction() = resupplyTransactionRepository.showAllResupplyTransaction()

        fun showStore(id:String) = storeRepository.showStore(id)
        fun searchResupplyTransaction(query: String) = resupplyTransactionRepository.searchResupplyTransaction(query)
    fun showFilteredResupplyTransaction(status: String, filterBy:String) = resupplyTransactionRepository.showFilteredResupplyTransaction(status, filterBy)

}
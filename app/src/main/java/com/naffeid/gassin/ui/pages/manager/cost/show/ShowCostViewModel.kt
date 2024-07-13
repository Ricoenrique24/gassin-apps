package com.naffeid.gassin.ui.pages.manager.cost.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.OperationTransactionRepository
import com.naffeid.gassin.data.repository.UserRepository

class ShowCostViewModel(
    private val userRepository: UserRepository,
    private val operationTransactionRepository: OperationTransactionRepository
) : ViewModel() {
    fun showOperationTransactionManager(id:String, type:String) = operationTransactionRepository.showOperationTransactionManager(id, type)
    fun searchOperationTransaction(query:String) = operationTransactionRepository.searchOperationTransaction(query)
    fun updateOperationTransaction(idTransaction:String, typeTransaction:String, verified:Boolean) = operationTransactionRepository.updateOperationTransaction(idTransaction,typeTransaction,verified)
}
package com.naffeid.gassin.ui.pages.manager.resupplytransaction.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.ResupplyTransactionRepository

class ShowResupplyTransactionViewModel(
    private val resupplyTransactionRepository: ResupplyTransactionRepository
) : ViewModel() {
    fun showResupplyTransaction(id:String) = resupplyTransactionRepository.showResupplyTransaction(id)
    fun deleteResupplyTransaction(id: String) = resupplyTransactionRepository.deleteResupplyTransaction(id)
    fun cancelledResupplyTransaction(id: String) = resupplyTransactionRepository.cancelledResupplyTransaction(id)
}
package com.naffeid.gassin.ui.pages.employee.order.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.TransactionRepository

class ShowOrderEmployeeViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun showTransaction(id:String, type:String) = transactionRepository.showTransaction(id, type)
    fun inProgressTransaction(id:String, type:String) = transactionRepository.inProgressTransaction(id, type)
    fun completedTransaction(id:String, type:String) = transactionRepository.completedTransaction(id, type)
    fun cancelledTransaction(id:String, type:String, note:String) = transactionRepository.cancelledTransaction(id, type, note)
}
package com.naffeid.gassin.ui.pages.employee.order.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.OperationTransactionRepository
import com.naffeid.gassin.data.repository.TransactionRepository

class ShowOrderEmployeeViewModel(
    private val transactionRepository: TransactionRepository,
    private val operationTransactionRepository: OperationTransactionRepository
) : ViewModel() {
    fun showTransaction(id:String, type:String) = transactionRepository.showTransaction(id, type)
    fun inProgressTransaction(id:String, type:String) = transactionRepository.inProgressTransaction(id, type)
    fun completedTransaction(id:String, type:String) = transactionRepository.completedTransaction(id, type)
    fun cancelledTransaction(id:String, type:String, note:String) = transactionRepository.cancelledTransaction(id, type, note)
    fun createNewOperationTransaction(idTransaction:String, note:String, totalPayment:String, type:String) = operationTransactionRepository.createNewOperationTransaction(idTransaction,note, totalPayment, type)
    fun showOperationTransaction(id:String, type:String) = operationTransactionRepository.showOperationTransaction(id, type)
}
package com.naffeid.gassin.ui.pages.manager.purchasetransaction.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.PurchaseTransactionRepository

class ShowPurchaseTransactionViewModel(
    private val purchaseTransactionRepository: PurchaseTransactionRepository
) : ViewModel() {
    fun showPurchaseTransaction(id:String) = purchaseTransactionRepository.showPurchaseTransaction(id)
    fun deletePurchaseTransaction(id: String) = purchaseTransactionRepository.deletePurchaseTransaction(id)
    fun cancelledPurchaseTransaction(id: String) = purchaseTransactionRepository.cancelledPurchaseTransaction(id)
}
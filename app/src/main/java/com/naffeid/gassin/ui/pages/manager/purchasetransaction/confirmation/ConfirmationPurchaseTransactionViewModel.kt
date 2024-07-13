package com.naffeid.gassin.ui.pages.manager.purchasetransaction.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.PurchaseTransactionRepository

class ConfirmationPurchaseTransactionViewModel(
    private val purchaseTransactionRepository: PurchaseTransactionRepository,
    private val customerRepository: CustomerRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    fun getCustomer(): LiveData<Customer> {
        return customerRepository.getCustomer().asLiveData()
    }
    fun getEmployee(): LiveData<Employee> {
        return employeeRepository.getEmployee().asLiveData()
    }
    fun createNewPurchaseTransaction(
        idCustomer: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ) = purchaseTransactionRepository.createNewPurchaseTransaction(
        idCustomer,
        idUser,
        qty,
        totalPayment
    )
}
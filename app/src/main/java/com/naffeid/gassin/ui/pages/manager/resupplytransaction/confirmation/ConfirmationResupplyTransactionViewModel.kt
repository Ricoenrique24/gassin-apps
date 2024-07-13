package com.naffeid.gassin.ui.pages.manager.resupplytransaction.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.ResupplyTransactionRepository
import com.naffeid.gassin.data.repository.StoreRepository

class ConfirmationResupplyTransactionViewModel(
    private val resupplyTransactionRepository: ResupplyTransactionRepository,
    private val storeRepository: StoreRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    fun getStore(): LiveData<Store> {
        return storeRepository.getStore().asLiveData()
    }
    fun getEmployee(): LiveData<Employee> {
        return employeeRepository.getEmployee().asLiveData()
    }
    fun createNewResupplyTransaction(
        idStore: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ) = resupplyTransactionRepository.createNewResupplyTransaction(
        idStore,
        idUser,
        qty,
        totalPayment
    )
}
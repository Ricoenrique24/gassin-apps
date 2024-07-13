package com.naffeid.gassin.ui.pages.manager.resupplytransaction.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.ResupplyTransactionRepository
import com.naffeid.gassin.data.repository.StoreRepository

class CreateResupplyTransactionViewModel(
    private val resupplyTransactionRepository: ResupplyTransactionRepository,
    private val storeRepository: StoreRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity

    private val _totalPayment = MutableLiveData<Int>()
    val totalPayment: LiveData<Int> get() = _totalPayment

    private var gasPrice: Int = 0

    init {
        _quantity.value = 1
        updateTotalPayment()
    }

    fun increaseQuantity() {
        _quantity.value = (_quantity.value ?: 1) + 1
        updateTotalPayment()
    }

    fun decreaseQuantity() {
        val currentQty = _quantity.value ?: 1
        if (currentQty > 1) {
            _quantity.value = currentQty - 1
            updateTotalPayment()
        }
    }

    fun setQuantity(newQty: Int) {
        _quantity.value = newQty
        updateTotalPayment()
    }

    private fun updateTotalPayment() {
        _totalPayment.value = calculateTotalPayment()
    }

    private fun calculateTotalPayment(): Int {
        return (_quantity.value ?: 0) * gasPrice
    }

    fun updateGasPriceFromStorePrice(storePrice: String) {
        val price = storePrice.toDoubleOrNull() ?: 0
        gasPrice = price.toInt()
        updateTotalPayment()
    }
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
package com.naffeid.gassin.ui.pages.manager.purchasetransaction.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.DashboardRepository
import com.naffeid.gassin.data.repository.EmployeeRepository

class CreatePurchaseTransactionViewModel(
    private val customerRepository: CustomerRepository,
    private val employeeRepository: EmployeeRepository,
    private val dashboardRepository: DashboardRepository
    ) : ViewModel() {
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity

    private val _totalPayment = MutableLiveData<Int>()
    val totalPayment: LiveData<Int> get() = _totalPayment

    private var gasPrice: Int = 0

    private var stock: Int = 0

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

    fun setStock(newStock: Int) {
        stock = newStock
    }

    fun getStock(): Int {
        return stock
    }

    private fun updateTotalPayment() {
        _totalPayment.value = calculateTotalPayment()
    }

    private fun calculateTotalPayment(): Int {
        return (_quantity.value ?: 0) * gasPrice
    }

    fun updateGasPriceFromCustomerPrice(customerPrice: String) {
        val price = customerPrice.toDoubleOrNull() ?: 0
        gasPrice = price.toInt()
        updateTotalPayment()
    }
    fun getCustomer(): LiveData<Customer> {
        return customerRepository.getCustomer().asLiveData()
    }
    fun getEmployee(): LiveData<Employee> {
        return employeeRepository.getEmployee().asLiveData()
    }
    fun getAvailableStockQuantity() = dashboardRepository.getAvailableStockQuantity()
}
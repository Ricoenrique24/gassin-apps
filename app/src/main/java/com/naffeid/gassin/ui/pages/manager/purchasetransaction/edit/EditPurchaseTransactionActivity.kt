package com.naffeid.gassin.ui.pages.manager.purchasetransaction.edit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.remote.response.Purchase
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityEditPurchaseTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerActivity
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.show.ShowPurchaseTransactionActivity

class EditPurchaseTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPurchaseTransactionBinding
    private val viewModel by viewModels<EditPurchaseTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPurchaseTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val idPurchase = intent.getStringExtra("PURCHASE")
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData) {
            if (idPurchase != null) setupData(idPurchase, true)
        }
        if (!idPurchase.isNullOrBlank()) {
            setupData(idPurchase.toString(), updateData)
            setupTopBar(idPurchase.toString())
            validate(idPurchase.toString())
        }
    }

    private fun setupData(id: String, updateData: Boolean) {
        viewModel.showPurchaseTransaction(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val purchaseData = result.data.purchase
                    if (purchaseData != null) setupView(purchaseData, updateData)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(purchase: Purchase, updateData: Boolean) {
        setupCustomer(purchase, updateData)
        setupEmployee(purchase, updateData)
        setupQty(purchase)
        setupPayment()
    }

    private fun setupCustomer(purchase: Purchase, updateData: Boolean) {
        if (updateData) {
            viewModel.getCustomer().observe(this) { customer ->
                if (customer.isNotEmpty()) {
                    with(binding) {
                        tvCustomerName.text = customer.name
                        tvCustomerPhone.text = customer.phone
                    }
                } else {
                    with(binding) {
                        tvCustomerName.text = getString(R.string.pelanggan_belum_dipilih)
                        tvCustomerPhone.text = getString(R.string.pilih_pelanggan_terlebih_dahulu)
                    }
                }
            }
        } else {
            with(binding) {
                tvCustomerName.text = purchase.customer!!.name
                tvCustomerPhone.text = purchase.customer.phone
            }
        }
        binding.btnChangeCustomer.setOnClickListener {
            navigateToChooseCustomer(purchase.id.toString())
        }

    }

    private fun setupEmployee(purchase: Purchase, updateData: Boolean) {
        if (updateData) {
            viewModel.getEmployee().observe(this) { employee ->
                if (employee.isNotEmpty()) {
                    with(binding) {
                        tvEmployeeName.text = employee.name
                        tvEmployeePhone.text = employee.phone
                    }
                } else with(binding) {
                    tvEmployeeName.text = getString(R.string.karyawan_belum_dipilih)
                    tvEmployeePhone.text = getString(R.string.pilih_karyawan_terlebih_dahulu)
                }
            }
        } else {
            with(binding) {
                tvEmployeeName.text = purchase.user!!.name
                tvEmployeePhone.text = purchase.user.phone
            }
        }
        binding.btnChangeEmployee.setOnClickListener {
            navigateToChooseEmployee(purchase.id.toString())
        }
    }

    private fun setupQty(purchase: Purchase) {
        viewModel.setQuantity(purchase.qty!!.toInt())
        viewModel.quantity.observe(this) { qty ->
            binding.edQtyGas.setText(qty.toString())
        }

        binding.btnMinus.setOnClickListener {
            val newQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
            if (newQty > 1) {
                viewModel.setQuantity(newQty)
                viewModel.decreaseQuantity()
            } else {
                binding.edQtyGas.setText("1")
            }
        }

        binding.btnPlus.setOnClickListener {
            val newQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
            viewModel.setQuantity(newQty)
            viewModel.increaseQuantity()
        }

        binding.edQtyGas.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val newQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
                if (newQty > 0) {
                    viewModel.setQuantity(newQty)
                } else {
                    binding.edQtyGas.setText("1")
                }
            }
        }
    }

    private fun setupPayment() {
        viewModel.getCustomer().observe(this) { customer ->
            val price = customer.price.toDoubleOrNull() ?: 0.0
            binding.tvPriceOneGas.text = Rupiah.convertToRupiah(price)

            viewModel.updateGasPriceFromCustomerPrice(customer.price)

            viewModel.totalPayment.observe(this) { totalPayment ->
                val total = totalPayment.toDouble()
                binding.tvTotalPayment.text = Rupiah.convertToRupiah(total)
            }
        }
        viewModel.quantity.observe(this) { qty ->
            binding.tvQtyGasTotal.text = qty.toString()
        }
    }

    private fun validate(id: String) {
        binding.btnCreatePurchaseTransaction.setOnClickListener {
            viewModel.getCustomer().observe(this) { customer ->
                if (customer != null) {
                    viewModel.getEmployee().observe(this) { employee ->
                        if (employee != null) {
                            viewModel.quantity.observe(this) { quantity ->
                                viewModel.totalPayment.observe(this) { totalPayment ->
                                    if (quantity != 0) {
                                        val qty = quantity.toString()
                                        val paymentTotal = totalPayment.toString()
                                        confirmationPurchaseTransaction(
                                            id,
                                            customer.id.toString(),
                                            employee.id.toString(),
                                            qty,
                                            paymentTotal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun confirmationPurchaseTransaction(
        id: String,
        idCustomer: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ) {
        viewModel.updatePurchaseTransaction(id, idCustomer, idUser, qty, totalPayment)
            .observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(result.data.message.toString())
                        onBackPressed()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(result.error)
                        Log.e("error customer:", result.error.toString())
                    }
                }
            }
    }

    private fun navigateToChooseCustomer(id:String) {
        val intentToChooseCustomer = Intent(this@EditPurchaseTransactionActivity, ChooseCustomerActivity::class.java)
        intentToChooseCustomer.putExtra("FROM-EDIT-PURCHASE",true)
        intentToChooseCustomer.putExtra("PURCHASE", id)
        startActivity(intentToChooseCustomer)
        finish()
    }

    private fun navigateToChooseEmployee(id:String) {
        val intentToChooseEmployee = Intent(this@EditPurchaseTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-EDIT-PURCHASE",true)
        intentToChooseEmployee.putExtra("PURCHASE", id)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun setupTopBar(id:String) {
        binding.btnBack.setOnClickListener {
            val intentToShow = Intent(this@EditPurchaseTransactionActivity, ShowPurchaseTransactionActivity::class.java)
            intentToShow.putExtra("PURCHASE",id)
            startActivity(intentToShow)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    private fun Customer.isNotEmpty(): Boolean {
        return this != Customer(0, "", "", "", "", "")
    }

    private fun Employee.isNotEmpty(): Boolean {
        return this != Employee(0, "", "", "", "", "", "")
    }
}
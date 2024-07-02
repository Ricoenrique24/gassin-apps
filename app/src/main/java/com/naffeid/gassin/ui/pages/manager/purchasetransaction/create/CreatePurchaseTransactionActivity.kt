package com.naffeid.gassin.ui.pages.manager.purchasetransaction.create

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityCreatePurchaseTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerActivity
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.confirmation.ConfirmationPurchaseTransactionActivity

class CreatePurchaseTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePurchaseTransactionBinding
    private val viewModel by viewModels<CreatePurchaseTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePurchaseTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData){
            setupView()
            validate()
        }
        setupTopBar()
        setupView()
        validate()
    }

    private fun setupView() {
        setupCustomer()
        setupEmployee()
        setupQty()
        setupPayment()
    }

    private fun setupCustomer() {
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
        binding.btnChangeCustomer.setOnClickListener {
            navigateToChooseCustomer()
        }

    }

    private fun setupEmployee() {
        viewModel.getEmployee().observe(this){ employee ->
            if (employee.isNotEmpty()) {
                with(binding) {
                    tvEmployeeName.text = employee.name
                    tvEmployeePhone.text = employee.phone
                }
            } else with(binding){
                tvEmployeeName.text = getString(R.string.karyawan_belum_dipilih)
                tvEmployeePhone.text = getString(R.string.pilih_karyawan_terlebih_dahulu)
            }
        }
        binding.btnChangeEmployee.setOnClickListener {
            navigateToChooseEmployee()
        }
    }

    private fun setupQty() {
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

    private fun validate() {
        binding.btnCreatePurchaseTransaction.setOnClickListener {
            viewModel.getCustomer().observe(this){ customer ->
                if (customer != null){
                    viewModel.getEmployee().observe(this){ employee ->
                        if (employee != null){
                            viewModel.quantity.observe(this) { quantity ->
                                viewModel.totalPayment.observe(this) { totalPayment ->
                                    if (quantity != 0) {
                                        val qty = quantity.toString()
                                        val paymentTotal =  totalPayment.toString()
                                        confirmationPurchaseTransaction(qty, paymentTotal)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun confirmationPurchaseTransaction(qty: String, totalPayment: String) {
        val intentToConfirm = Intent(this@CreatePurchaseTransactionActivity, ConfirmationPurchaseTransactionActivity::class.java)
        intentToConfirm.putExtra("QUANTITY-PURCHASE", qty)
        intentToConfirm.putExtra("TOTAL-PURCHASE", totalPayment)
        startActivity(intentToConfirm)
    }

    private fun navigateToHome() {
        val intentToHome = Intent(this@CreatePurchaseTransactionActivity, ManagerMainActivity::class.java)
        startActivity(intentToHome)
        finish()
    }

    private fun navigateToChooseCustomer() {
        val intentToChooseCustomer= Intent(this@CreatePurchaseTransactionActivity, ChooseCustomerActivity::class.java)
        intentToChooseCustomer.putExtra("FROM-CREATE-PURCHASE",true)
        startActivity(intentToChooseCustomer)
        finish()
    }

    private fun navigateToChooseEmployee() {
        val intentToChooseEmployee = Intent(this@CreatePurchaseTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-CREATE-PURCHASE",true)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            val intentToHome = Intent(this@CreatePurchaseTransactionActivity, ManagerMainActivity::class.java)
            startActivity(intentToHome)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Toast.makeText(this, string, LENGTH_LONG).show()
    }

    private fun Customer.isNotEmpty(): Boolean {
        return this != Customer(0, "", "", "", "", "")
    }

    private fun Employee.isNotEmpty(): Boolean {
        return this != Employee(0, "", "", "", "", "", "")
    }
}
package com.naffeid.gassin.ui.pages.manager.purchasetransaction.confirmation

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
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityConfirmationPurchaseTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity

class ConfirmationPurchaseTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmationPurchaseTransactionBinding
    private val viewModel by viewModels<ConfirmationPurchaseTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationPurchaseTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val quantity = intent.getStringExtra("QUANTITY-PURCHASE").toString()
        val totalPayment = intent.getStringExtra("TOTAL-PURCHASE").toString()
        setupTobBar()
        setupView(quantity, totalPayment)
        validate(quantity, totalPayment)
    }

    private fun setupView(quantity:String, totalPayment:String) {
        setupCustomer()
        setupEmployee()
        setupPayment(quantity, totalPayment)
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
    }

    private fun setupPayment(quantity:String, totalPayment:String) {
        viewModel.getCustomer().observe(this) { customer ->
            binding.tvPriceOneGas.text = customer.price.toString()
        }
        binding.tvQtyGasTotal.text = quantity
        binding.tvTotalPayment.text = totalPayment
    }

    private fun validate(quantity:String, totalPayment:String) {
        binding.btnCreatePurchaseTransaction.setOnClickListener {
            viewModel.getCustomer().observe(this){ customer ->
                if (customer != null){
                    viewModel.getEmployee().observe(this){ employee ->
                        if (employee != null){
                            val idCustomer = customer.id.toString()
                            val idUser = employee.id.toString()
                            createNewPurchaseTransaction(idCustomer, idUser, quantity, totalPayment)
                        }
                    }
                }
            }
        }
    }

    private fun createNewPurchaseTransaction(idCustomer: String, idUser: String,  qty: String, totalPayment: String) {
        viewModel.createNewPurchaseTransaction(idCustomer, idUser, qty, totalPayment).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(it.error)
                        Log.e("error search store:", it.error.toString())
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(getString(R.string.transaksi_antar_gas_berhasil_dibuat))
                        navigateToHome()
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        val intentToHome = Intent(this@ConfirmationPurchaseTransactionActivity, ManagerMainActivity::class.java)
        startActivity(intentToHome)
        finish()
    }

    private fun setupTobBar() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
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
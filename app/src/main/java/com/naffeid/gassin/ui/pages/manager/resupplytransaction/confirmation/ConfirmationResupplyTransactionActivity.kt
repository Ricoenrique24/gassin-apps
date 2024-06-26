package com.naffeid.gassin.ui.pages.manager.resupplytransaction.confirmation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityConfirmationResupplyTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity

class ConfirmationResupplyTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmationResupplyTransactionBinding
    private val viewModel by viewModels<ConfirmationResupplyTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationResupplyTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val quantity = intent.getStringExtra("QUANTITY-RESUPPLY").toString()
        val totalPayment = intent.getStringExtra("TOTAL-RESUPPLY").toString()
        setupTobBar()
        setupView(quantity, totalPayment)
        validate(quantity, totalPayment)
    }

    private fun setupView(quantity:String, totalPayment:String) {
        setupStore()
        setupEmployee()
        setupPayment(quantity, totalPayment)
    }

    private fun setupStore() {
        viewModel.getStore().observe(this) { store ->
            if (store.isNotEmpty()) {
                with(binding) {
                    tvStoreName.text = store.name
                    tvStorePhone.text = store.phone
                }
            } else {
                with(binding) {
                    tvStoreName.text = getString(R.string.pelanggan_belum_dipilih)
                    tvStorePhone.text = getString(R.string.pilih_pelanggan_terlebih_dahulu)
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
        viewModel.getStore().observe(this) { store ->
            binding.tvPriceOneGas.text = store.price.toString()
        }
        binding.tvQtyGasTotal.text = quantity
        binding.tvTotalPayment.text = totalPayment
    }

    private fun validate(quantity:String, totalPayment:String) {
        binding.btnCreateResupplyTransaction.setOnClickListener {
            viewModel.getStore().observe(this){ store ->
                if (store != null){
                    viewModel.getEmployee().observe(this){ employee ->
                        if (employee != null){
                            val idStore = store.id.toString()
                            val idUser = employee.id.toString()
                            createNewResupplyTransaction(idStore, idUser, quantity, totalPayment)
                        }
                    }
                }
            }
        }
    }

    private fun createNewResupplyTransaction(idStore: String, idUser: String,  qty: String, totalPayment: String) {
        viewModel.createNewResupplyTransaction(idStore, idUser, qty, totalPayment).observe(this) {
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
        val intentToHome = Intent(this@ConfirmationResupplyTransactionActivity, ManagerMainActivity::class.java)
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

    private fun Store.isNotEmpty(): Boolean {
        return this != Store(0, "", "", "", "", "")
    }

    private fun Employee.isNotEmpty(): Boolean {
        return this != Employee(0, "", "", "", "", "", "")
    }
}
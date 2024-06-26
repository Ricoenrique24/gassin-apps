package com.naffeid.gassin.ui.pages.manager.resupplytransaction.create

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityCreateReSupplyTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.choose.store.ChooseStoreActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.confirmation.ConfirmationResupplyTransactionActivity

class CreateReSupplyTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateReSupplyTransactionBinding
    private val viewModel by viewModels<CreateResupplyTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReSupplyTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData){
            setupView()
            validate()
        }
        setupTobBar()
        setupView()
        validate()
    }

    private fun setupView() {
        setupStore()
        setupEmployee()
        setupQty()
        setupPayment()
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
        binding.btnChangeStore.setOnClickListener {
            navigateToChooseStore()
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
        viewModel.getStore().observe(this) { store ->
            val price = store.price.toDoubleOrNull() ?: 0.0
            binding.tvPriceOneGas.text = Rupiah.convertToRupiah(price)

            viewModel.updateGasPriceFromStorePrice(store.price)

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
        binding.btnCreateResupplyTransaction.setOnClickListener {
            viewModel.getStore().observe(this){ store ->
                if (store != null){
                    viewModel.getEmployee().observe(this){ employee ->
                        if (employee != null){
                            viewModel.quantity.observe(this) { quantity ->
                                viewModel.totalPayment.observe(this) { totalPayment ->
                                    if (quantity != 0) {
                                        val qty = quantity.toString()
                                        val paymentTotal =  totalPayment.toString()
                                        confirmationResupplyTransaction(qty, paymentTotal)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun confirmationResupplyTransaction(qty: String, totalPayment: String) {
        val intentToConfirm = Intent(this@CreateReSupplyTransactionActivity, ConfirmationResupplyTransactionActivity::class.java)
        intentToConfirm.putExtra("QUANTITY-RESUPPLY", qty)
        intentToConfirm.putExtra("TOTAL-RESUPPLY", totalPayment)
        startActivity(intentToConfirm)
    }

    private fun navigateToHome() {
        val intentToHome = Intent(this@CreateReSupplyTransactionActivity, ManagerMainActivity::class.java)
        startActivity(intentToHome)
        finish()
    }

    private fun navigateToChooseStore() {
        val intentToChooseStore= Intent(this@CreateReSupplyTransactionActivity, ChooseStoreActivity::class.java)
        startActivity(intentToChooseStore)
        finish()
    }

    private fun navigateToChooseEmployee() {
        val intentToChooseEmployee = Intent(this@CreateReSupplyTransactionActivity, ChooseEmployeeActivity::class.java)
        startActivity(intentToChooseEmployee)
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
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
        val quantity = intent.getStringExtra("QUANTITY")
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData) {
            setupData(quantity)
        } else {
            setupData(quantity)
        }
        setupTopBar()
    }

    private fun setupData(qty:String?) {
        setupView(qty)
        validate()
    }

    private fun setupView(qty:String?) {
        setupStore()
        setupEmployee()
        setupQty(qty)
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
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseStore(qty.toString())
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
        binding.btnChangeEmployee.setOnClickListener {
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseEmployee(qty.toString())
            }
        }
    }

    private fun setupQty(quantity:String?) {
        if (quantity != null) {
            val newQty = quantity.toIntOrNull() ?: 1
            binding.edQtyGas.setText(quantity)
            viewModel.setQuantity(newQty)
        } else {
            viewModel.quantity.observe(this) { qty ->
                binding.edQtyGas.setText(qty.toString())
            }
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
        intentToConfirm.putExtra("QUANTITY", qty)
        intentToConfirm.putExtra("TOTAL-RESUPPLY", totalPayment)
        startActivity(intentToConfirm)
    }

    private fun navigateToHome() {
        val intentToHome = Intent(this@CreateReSupplyTransactionActivity, ManagerMainActivity::class.java)
        startActivity(intentToHome)
        finish()
    }

    private fun navigateToChooseStore(qty: String) {
        val intentToChooseStore = Intent(this@CreateReSupplyTransactionActivity, ChooseStoreActivity::class.java)
        intentToChooseStore.putExtra("FROM-CREATE-RESUPPLY",true)
        intentToChooseStore.putExtra("QUANTITY", qty)
        startActivity(intentToChooseStore)
        finish()
    }

    private fun navigateToChooseEmployee(qty: String) {
        val intentToChooseEmployee = Intent(this@CreateReSupplyTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-CREATE-RESUPPLY",true)
        intentToChooseEmployee.putExtra("QUANTITY", qty)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            navigateToHome()
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
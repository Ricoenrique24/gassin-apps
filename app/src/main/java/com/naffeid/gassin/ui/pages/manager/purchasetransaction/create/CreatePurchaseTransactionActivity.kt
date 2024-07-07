package com.naffeid.gassin.ui.pages.manager.purchasetransaction.create

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityCreatePurchaseTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerActivity
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.confirmation.ConfirmationPurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity

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
        showAvailableStockQuantity()
        setupView(qty)
        validate()
    }

    private fun setupView(qty:String?) {
        setupCustomer()
        setupEmployee()
        setupQty(qty)
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
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseCustomer(qty.toString())
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
            val stock = viewModel.getStock()
            if (stock < newQty+1) {
                showStockWarningDialog(stock)
            } else {
                viewModel.setQuantity(newQty)
                viewModel.increaseQuantity()
            }
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
        intentToConfirm.putExtra("QUANTITY", qty)
        intentToConfirm.putExtra("TOTAL-PURCHASE", totalPayment)
        startActivity(intentToConfirm)
    }

    private fun navigateToChooseCustomer(qty: String) {
        val intentToChooseCustomer= Intent(this@CreatePurchaseTransactionActivity, ChooseCustomerActivity::class.java)
        intentToChooseCustomer.putExtra("FROM-CREATE-PURCHASE",true)
        intentToChooseCustomer.putExtra("QUANTITY", qty)
        startActivity(intentToChooseCustomer)
        finish()
    }


    private fun navigateToChooseEmployee(qty: String) {
        val intentToChooseEmployee = Intent(this@CreatePurchaseTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-CREATE-PURCHASE",true)
        intentToChooseEmployee.putExtra("QUANTITY", qty)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun navigateToCreateResupplyTransaction() {
        val intentToChooseEmployee = Intent(this@CreatePurchaseTransactionActivity, CreateReSupplyTransactionActivity::class.java)
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

    private fun showAvailableStockQuantity() {
        viewModel.getAvailableStockQuantity().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val stock = result.data.stock
                    if (stock != null) {
                        if (stock == 0) {
                            showResupplyWarningDialog()
                        } else {
                            viewModel.setStock(stock)
                        }
                    }

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun showStockWarningDialog(availableStock: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.stok_tidak_cukup))
            .setMessage(
                getString(
                    R.string.stok_saat_ini_apakah_anda_ingin_menggunakan_stok_yang_tersedia,
                    availableStock.toString()
                ))
            .setPositiveButton(getString(R.string.gunakan_stok)) { _, _ ->
                viewModel.setQuantity(availableStock)
            }
            .setNegativeButton(getString(R.string.resupply)) { _, _ ->
                navigateToCreateResupplyTransaction()
            }
            .show()
    }

    private fun showResupplyWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.stok_habis))
            .setMessage(getString(R.string.stok_saat_ini_kosong_anda_perlu_melakukan_resupply))
            .setPositiveButton(getString(R.string.resupply)) { _, _ ->
                navigateToCreateResupplyTransaction()
            }
            .show()
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
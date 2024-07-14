package com.naffeid.gassin.ui.pages.manager.purchasetransaction.edit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity

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
        val quantity = intent.getStringExtra("QUANTITY")
        val idPurchase = intent.getStringExtra("PURCHASE")
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData) {
            if (idPurchase != null) setupData(quantity, idPurchase, true)
        }
        if (!idPurchase.isNullOrBlank()) {
            setupData(quantity, idPurchase.toString(), updateData)
            setupTopBar(idPurchase.toString())
            validate(idPurchase.toString())
        }
    }

    private fun setupData(qty:String?, id: String, updateData: Boolean) {
        viewModel.showPurchaseTransaction(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAvailableStockQuantity()
                    val purchaseData = result.data.purchase
                    if (purchaseData != null) setupView(qty, purchaseData, updateData)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(qty:String?, purchase: Purchase, updateData: Boolean) {
        setupCustomer(purchase, updateData)
        setupEmployee(purchase, updateData)
        setupQty(qty, purchase)
        setupPayment(purchase)
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
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseCustomer(qty.toString(), purchase.id.toString())
            }
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
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseEmployee(qty.toString(), purchase.id.toString())
            }
        }
    }

    private fun setupQty(quantity: String?, purchase: Purchase) {
        // Set initial quantity
        if (quantity != null) {
            val newQty = quantity.toIntOrNull() ?: 1
            viewModel.setQuantity(newQty)
        } else {
            viewModel.setQuantity(purchase.qty!!.toInt())
        }

        // Observe quantity changes and update the UI
        viewModel.quantity.observe(this) { qty ->
            binding.edQtyGas.setText(qty.toString())
        }

        // Set onClick listeners for plus and minus buttons
        binding.btnMinus.setOnClickListener {
            val currentQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
            if (currentQty > 1) {
                viewModel.decreaseQuantity()
            }
        }

        binding.btnPlus.setOnClickListener {
            val currentQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
            val initialQty = purchase.qty!!.toInt()
            val stock = viewModel.getStock()

            if (currentQty < initialQty) {
                // Jika quantity lebih kecil dari awal, tambahkan ke stock
                viewModel.increaseQuantity()
            } else {
                // Jika quantity lebih besar dari awal, lakukan pengecekan stock
                val totalQty = currentQty + 1 - initialQty
                if (stock >= totalQty) {
                    viewModel.increaseQuantity()
                } else {
                    showStockWarningDialog(stock, currentQty)
                }
            }
        }

        binding.edQtyGas.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val newQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
                val initialQty = purchase.qty!!.toInt()
                val stock = viewModel.getStock()
                if (newQty < 1) {
                    binding.edQtyGas.setText("1")
                    viewModel.setQuantity(1)
                } else {
                    if (newQty > initialQty) {
                        val totalQty = newQty - initialQty
                        if (stock < totalQty) {
                            showStockWarningDialog(stock, newQty)
                        } else {
                            viewModel.setQuantity(newQty)
                        }
                    } else {
                        viewModel.setQuantity(newQty)
                    }
                }
            }
        }
    }


    private fun setupPayment(purchase: Purchase) {
        viewModel.getCustomer().observe(this) { customer ->
            val price: Double = if (customer != null && customer.price != null && customer.price.toDoubleOrNull() ?: 0.0 > 0.0) {
                customer.price.toDouble()
            } else {
                purchase.customer?.price?.toDoubleOrNull() ?: 0.0
            }

            binding.tvPriceOneGas.text = Rupiah.convertToRupiah(price)
            viewModel.updateGasPriceFromCustomerPrice(price.toString())
        }

        viewModel.totalPayment.observe(this) { totalPayment ->
            val total = totalPayment.toDouble()
            binding.tvTotalPayment.text = Rupiah.convertToRupiah(total)
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
                        navigateToShow(id)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(result.error)
                        Log.e("error customer:", result.error.toString())
                    }
                }
            }
    }

    private fun navigateToChooseCustomer(qty: String, id:String) {
        val intentToChooseCustomer = Intent(this@EditPurchaseTransactionActivity, ChooseCustomerActivity::class.java)
        intentToChooseCustomer.putExtra("FROM-EDIT-PURCHASE",true)
        intentToChooseCustomer.putExtra("PURCHASE", id)
        intentToChooseCustomer.putExtra("QUANTITY", qty)
        startActivity(intentToChooseCustomer)
        finish()
    }

    private fun navigateToChooseEmployee(qty: String, id:String) {
        val intentToChooseEmployee = Intent(this@EditPurchaseTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-EDIT-PURCHASE",true)
        intentToChooseEmployee.putExtra("PURCHASE", id)
        intentToChooseEmployee.putExtra("QUANTITY", qty)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun navigateToCreateResupplyTransaction() {
        val intentToChooseEmployee = Intent(this@EditPurchaseTransactionActivity, CreateReSupplyTransactionActivity::class.java)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun navigateToShow(id:String) {
        val intentToShow = Intent(this@EditPurchaseTransactionActivity, ShowPurchaseTransactionActivity::class.java)
        intentToShow.putExtra("PURCHASE", id)
        intentToShow.putExtra("PURCHASE-UPDATED", true)
        startActivity(intentToShow)
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

    private fun showAvailableStockQuantity() {
        viewModel.getAvailableStockQuantity().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val stock = result.data.stock
                    if (stock != null) viewModel.setStock(stock)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun showStockWarningDialog(availableStock: Int, qty: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.stok_tidak_cukup))
            .setMessage(
                getString(
                    R.string.stok_saat_ini_apakah_anda_ingin_menggunakan_stok_yang_tersedia,
                    availableStock.toString()
                ))
            .setPositiveButton(getString(R.string.gunakan_stok)) { _, _ ->
                viewModel.setQuantity(qty)
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
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    private fun Customer.isNotEmpty(): Boolean {
        return this != Customer(0, "", "", "", "", "")
    }

    private fun Employee.isNotEmpty(): Boolean {
        return this != Employee(0, "", "", "", "", "", "")
    }
}
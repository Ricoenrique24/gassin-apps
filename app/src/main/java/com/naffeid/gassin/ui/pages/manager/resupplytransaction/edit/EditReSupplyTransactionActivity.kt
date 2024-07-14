package com.naffeid.gassin.ui.pages.manager.resupplytransaction.edit

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
import com.naffeid.gassin.data.remote.response.Resupply
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityEditReSupplyTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.choose.store.ChooseStoreActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.show.ShowReSupplyTransactionActivity

class EditReSupplyTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditReSupplyTransactionBinding
    private val viewModel by viewModels<EditReSupplyTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditReSupplyTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val quantity = intent.getStringExtra("QUANTITY")
        val idResupply = intent.getStringExtra("RESUPPLY")
        val updateData = intent.getBooleanExtra("CHOOSE-UPDATED", false)
        if (updateData) {
            if (idResupply != null) setupData(quantity, idResupply, true)
        }
        if (!idResupply.isNullOrBlank()) {
            setupData(quantity, idResupply.toString(), updateData)
            setupTopBar(idResupply.toString())
            validate(idResupply.toString())
        }
    }

    private fun setupData(qty:String?, id: String, updateData: Boolean) {
        viewModel.showResupplyTransaction(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val resupplyData = result.data.resupply
                    if (resupplyData != null) setupView(qty, resupplyData, updateData)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(qty:String?, resupply: Resupply, updateData: Boolean) {
        setupStore(resupply, updateData)
        setupEmployee(resupply, updateData)
        setupQty(qty, resupply)
        setupPayment(resupply)
    }

    private fun setupStore(resupply: Resupply, updateData: Boolean) {
        if (updateData) {
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
        } else {
            with(binding) {
                tvStoreName.text = resupply.store!!.name
                tvStorePhone.text = resupply.store.phone
            }
        }
        binding.btnChangeStore.setOnClickListener {
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseStore(qty.toString(), resupply.id.toString())
            }
        }

    }

    private fun setupEmployee(resupply: Resupply, updateData: Boolean) {
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
                tvEmployeeName.text = resupply.user!!.name
                tvEmployeePhone.text = resupply.user.phone
            }
        }
        binding.btnChangeEmployee.setOnClickListener {
            viewModel.quantity.observe(this) { qty ->
                navigateToChooseEmployee(qty.toString(), resupply.id.toString())
            }
        }
    }

    private fun setupQty(quantity:String?, resupply: Resupply) {
        if (quantity != null) {
            val newQty = quantity.toIntOrNull() ?: 1
            viewModel.setQuantity(newQty)
        } else {
            viewModel.setQuantity(resupply.qty!!.toInt())
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
            viewModel.increaseQuantity()
        }

        binding.edQtyGas.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val newQty = binding.edQtyGas.text.toString().toIntOrNull() ?: 1
                if (newQty < 1) {
                    binding.edQtyGas.setText("1")
                    viewModel.setQuantity(1)
                } else {
                    viewModel.setQuantity(newQty)
                }
            }
        }
    }

    private fun setupPayment(resupply: Resupply) {
        viewModel.getStore().observe(this) { store ->
            val price: Double = if (store != null && store.price != null && store.price.toDoubleOrNull() ?: 0.0 > 0.0) {
                store.price.toDouble()
            } else {
                resupply.store?.price?.toDoubleOrNull() ?: 0.0
            }

            binding.tvPriceOneGas.text = Rupiah.convertToRupiah(price)
            viewModel.updateGasPriceFromStorePrice(price.toString())
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
        binding.btnCreateResupplyTransaction.setOnClickListener {
            viewModel.getStore().observe(this) { store ->
                if (store != null) {
                    viewModel.getEmployee().observe(this) { employee ->
                        if (employee != null) {
                            viewModel.quantity.observe(this) { quantity ->
                                viewModel.totalPayment.observe(this) { totalPayment ->
                                    if (quantity != 0) {
                                        val qty = quantity.toString()
                                        val paymentTotal = totalPayment.toString()
                                        confirmationResupplyTransaction(
                                            id,
                                            store.id.toString(),
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

    private fun confirmationResupplyTransaction(
        id: String,
        idStore: String,
        idUser: String,
        qty: String,
        totalPayment: String
    ) {
        viewModel.updateResupplyTransaction(id, idStore, idUser, qty, totalPayment)
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

    private fun navigateToChooseStore(qty: String, id:String) {
        val intentToChooseStore = Intent(this@EditReSupplyTransactionActivity, ChooseStoreActivity::class.java)
        intentToChooseStore.putExtra("FROM-EDIT-RESUPPLY",true)
        intentToChooseStore.putExtra("RESUPPLY", id)
        intentToChooseStore.putExtra("QUANTITY", qty)
        startActivity(intentToChooseStore)
        finish()
    }

    private fun navigateToChooseEmployee(qty: String, id:String) {
        val intentToChooseEmployee = Intent(this@EditReSupplyTransactionActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("FROM-EDIT-RESUPPLY",true)
        intentToChooseEmployee.putExtra("RESUPPLY", id)
        intentToChooseEmployee.putExtra("QUANTITY", qty)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun navigateToShow(id:String) {
        val intentToShow = Intent(this@EditReSupplyTransactionActivity, ShowReSupplyTransactionActivity::class.java)
        intentToShow.putExtra("RESUPPLY", id)
        intentToShow.putExtra("RESUPPLY-UPDATED", true)
        startActivity(intentToShow)
        finish()
    }

    private fun setupTopBar(id:String) {
        binding.btnBack.setOnClickListener {
            val intentToShow = Intent(this@EditReSupplyTransactionActivity, ShowReSupplyTransactionActivity::class.java)
            intentToShow.putExtra("RESUPPLY", id)
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

    private fun Store.isNotEmpty(): Boolean {
        return this != Store(0, "", "", "", "", "")
    }

    private fun Employee.isNotEmpty(): Boolean {
        return this != Employee(0, "", "", "", "", "", "")
    }
}
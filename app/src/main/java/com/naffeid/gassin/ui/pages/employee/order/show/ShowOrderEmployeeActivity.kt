package com.naffeid.gassin.ui.pages.employee.order.show

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.Transaction
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ActivityShowOrderEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory

class ShowOrderEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowOrderEmployeeBinding
    private val viewModel by viewModels<ShowOrderEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowOrderEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val idTransaction = intent.getStringExtra("TRANSACTION")
        val typeTransaction = intent.getStringExtra("TYPE-TRANSACTION")
        val updateTransaction = intent.getBooleanExtra("TRANSACTION-UPDATED", false)
        if (updateTransaction) {
            if (idTransaction != null && typeTransaction != null) setupData(idTransaction, typeTransaction)
        }
        if (idTransaction != null && typeTransaction != null) setupData(idTransaction, typeTransaction)
        setupTobBar(updateTransaction)
    }

    private fun setupData(id: String, type:String) {
        viewModel.showTransaction(id, type).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val transactionData = result.data.transaction
                    if(transactionData != null) setupView(transactionData)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(transaction: Transaction) {
        with(binding) {
            val statusTransaction = transaction.statusTransaction
            tvStatusTransaction.text = transaction.statusTransaction?.status!!.capitalizeWords()
            tvStatusTransactionDesc.text = TransactionStatus.convertStatusToDescription(
                statusTransaction!!.id.toString())
            if (transaction.type == "purchase"){
                tvName.text = transaction.customer!!.name
                tvPhone.text = transaction.customer.phone
                tvCategoryName.text = getString(R.string.pelanggan)
                ivCategoryTransation.setImageResource(R.drawable.ic_profile_24dp)
            } else if (transaction.type == "resupply"){
                tvName.text = transaction.store!!.name
                tvPhone.text = transaction.store.phone
                tvCategoryName.text = getString(R.string.agen)
                ivCategoryTransation.setImageResource(R.drawable.ic_shop_24dp)
            }
            tvQtyGasTotal.text = transaction.qty.toString()
            tvTotalPayment.text = Rupiah.convertToRupiah(transaction.totalPayment?.toDoubleOrNull()!!)
            tvPriceOneGas.text = Rupiah.convertToRupiah(transaction.totalPayment.toDoubleOrNull()!! / transaction.qty!!)
        }
        setupStatusButton(transaction)
    }

    private fun setupStatusButton(transaction: Transaction) {
        val statusTransaction = transaction.statusTransaction!!.id
        val typeTransaction = transaction.type

        if (statusTransaction == 1) {
            with(binding){
                btnCancelTransaction.visibility = View.GONE
                btnUpdateTransaction.text = getString(R.string.berangkat)
                btnUpdateTransaction.setOnClickListener {
                    showAlert(getString(R.string.berangkat))
                }
            }
        } else if (statusTransaction == 2) {
            with(binding){
                btnCancelTransaction.visibility = View.VISIBLE
                btnCancelTransaction.setOnClickListener {
                    showAlert(getString(R.string.terkendala))
                }
                if (typeTransaction == "purchase"){
                    btnUpdateTransaction.text = getString(R.string.sudah_diantar)
                    btnUpdateTransaction.setOnClickListener {
                        showAlert(getString(R.string.sudah_diantar))
                    }
                } else if (typeTransaction == "resupply"){
                    btnUpdateTransaction.text = getString(R.string.sudah_dibeli)
                    btnUpdateTransaction.setOnClickListener {
                        showAlert(getString(R.string.sudah_dibeli))
                    }
                }

            }
        } else if (statusTransaction == 3) {
            with(binding){
                btnCancelTransaction.visibility = View.GONE
                btnUpdateTransaction.text = getString(R.string.ajukan_biaya_operasional)
                btnUpdateTransaction.setOnClickListener {
                    showAlert(getString(R.string.ajukan_biaya_operasional))
                }

            }
        } else {
            with(binding){
                btnCancelTransaction.visibility = View.GONE
                btnUpdateTransaction.visibility = View.GONE
            }
        }
    }

    private fun setupTobBar(updateTransaction: Boolean) {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }
}
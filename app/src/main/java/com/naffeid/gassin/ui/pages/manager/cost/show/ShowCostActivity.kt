package com.naffeid.gassin.ui.pages.manager.cost.show

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.OperationTransaction
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityShowCostBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory

class ShowCostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowCostBinding
    private val viewModel by viewModels<ShowCostViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val idTransaction = intent.getStringExtra("OPERATION-TRANSACTION")
        val typeTransaction = intent.getStringExtra("TYPE-TRANSACTION")

        if (idTransaction != null && typeTransaction != null) setupData(idTransaction, typeTransaction)
        setupTopBar()
    }
    private fun setupData(id: String, type: String) {
        viewModel.showOperationTransactionManager(id, type).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val operationTransactionData = result.data.operationTransaction
                    if (operationTransactionData != null) setupView(operationTransactionData)
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(operationTransaction: OperationTransaction) {
        val verified = operationTransaction.verified
        val typeTransaction = operationTransaction.categoryTransaction?.name.toString()
        val idTransaction = operationTransaction.id.toString()

        with(binding) {
            noteCost.text = operationTransaction.note.toString()
            tvTotalPayment.text = Rupiah.convertToRupiah(operationTransaction.totalPayment?.toDoubleOrNull()!!)
            if (typeTransaction == "purchase") {
                tvEmployeeName.text = operationTransaction.purchase!!.user?.name.toString()
                tvEmployeePhone.text = operationTransaction.purchase.user?.phone.toString()
            } else if (typeTransaction == "resupply") {
                tvEmployeeName.text = operationTransaction.resupply!!.user?.name.toString()
                tvEmployeePhone.text = operationTransaction.resupply.user?.phone.toString()
            }
        }

        when (verified) {
            0 -> with(binding) {
                btnReject.visibility = View.VISIBLE
                btnConfirm.visibility = View.VISIBLE
                btnReject.setOnClickListener {
                    updateOperationTransaction(idTransaction, typeTransaction, false)
                }
                btnConfirm.setOnClickListener {
                    updateOperationTransaction(idTransaction, typeTransaction, true)
                }
            }
            1 -> with(binding) {
                btnReject.visibility = View.INVISIBLE
                btnConfirm.visibility = View.INVISIBLE
            }
            else -> with(binding) {
                btnReject.visibility = View.INVISIBLE
                btnConfirm.visibility = View.INVISIBLE
            }
        }
    }
    private fun updateOperationTransaction(idTransaction:String, typeTransaction:String, verified:Boolean) {
        viewModel.updateOperationTransaction(idTransaction, typeTransaction, verified).observe(this) {
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
        onBackPressed()
        finish()
    }
    private fun setupTopBar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }
}
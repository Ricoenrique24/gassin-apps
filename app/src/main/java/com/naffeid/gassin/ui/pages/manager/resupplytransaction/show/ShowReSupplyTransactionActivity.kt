package com.naffeid.gassin.ui.pages.manager.resupplytransaction.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListResupplyItem
import com.naffeid.gassin.data.remote.response.Resupply
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ActivityShowReSupplyTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.edit.EditReSupplyTransactionActivity

class ShowReSupplyTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowReSupplyTransactionBinding
    private val viewModel by viewModels<ShowResupplyTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowReSupplyTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val idResupply = intent.getStringExtra("RESUPPLY")
        val updateResupply = intent.getBooleanExtra("RESUPPLY-UPDATED",false)
        if (updateResupply) {
            if (idResupply != null) setupData(idResupply)
        }
        if (idResupply != null) setupData(idResupply)
        setupTobBar(updateResupply)
    }

    private fun setupData(id: String) {
        viewModel.showResupplyTransaction(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val resupplyData = result.data.resupply
                    if(resupplyData != null) setupView(resupplyData)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(resupply: Resupply) {
        with(binding) {
            tvStatusTransaction.text = resupply.statusTransaction?.status?.capitalizeWords()
            tvStatusTransactionDesc.text = TransactionStatus.convertStatusToDescription(resupply.statusTransaction?.id.toString())
            tvStoreName.text = resupply.store!!.name
            tvStorePhone.text = resupply.store.phone
            tvEmployeeName.text = resupply.user!!.name
            tvEmployeePhone.text = resupply.user.phone
            tvQtyGasTotal.text = resupply.qty.toString()
            tvTotalPayment.text = Rupiah.convertToRupiah(resupply.totalPayment?.toDoubleOrNull()!!)
            tvPriceOneGas.text = Rupiah.convertToRupiah(resupply.totalPayment.toDoubleOrNull()!! / resupply.qty!!)
            binding.btnEditResupply.setOnClickListener {
                val resupplyData = ListResupplyItem(
                    id = resupply.id,
                    note = resupply.note.toString(),
                    idStore = resupply.idStore,
                    qty = resupply.qty,
                    statusTransaction = resupply.statusTransaction,
                    idUser = resupply.idUser,
                    user = resupply.user,
                    totalPayment = resupply.totalPayment,
                    status = resupply.status,
                    store = resupply.store

                )
                editResupplyTransaction(resupplyData)
            }
            binding.btnCancelResupply.setOnClickListener {
                val id = resupply.id.toString()
                deleteResupplyTransaction(id)
            }
        }
    }

    private fun editResupplyTransaction(data: ListResupplyItem) {
        val intentToDetail = Intent(this@ShowReSupplyTransactionActivity, EditReSupplyTransactionActivity::class.java)
        intentToDetail.putExtra("RESUPPLY", data.id.toString())
        startActivity(intentToDetail)
    }
    private fun deleteResupplyTransaction(id: String) {
        viewModel.deleteResupplyTransaction(id).observe(this){ result->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(result.data.message.toString())

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }

    private fun setupTobBar(updateResupply: Boolean) {
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
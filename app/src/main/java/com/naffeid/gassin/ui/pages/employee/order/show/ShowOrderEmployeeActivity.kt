package com.naffeid.gassin.ui.pages.employee.order.show

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.OperationTransaction
import com.naffeid.gassin.data.remote.response.Transaction
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ActivityShowOrderEmployeeBinding
import com.naffeid.gassin.ui.components.DialogCustomerDetail
import com.naffeid.gassin.ui.components.DialogStoreDetail
import com.naffeid.gassin.ui.components.EditText
import com.naffeid.gassin.ui.components.ProblemBottomSheet
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
        setupTopBar()
    }

    private fun setupData(id: String, type: String) {
        viewModel.showTransaction(id, type).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val transactionData = result.data.transaction
                    if (transactionData != null) setupView(transactionData)
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
            if (transaction.type == "purchase") {
                tvName.text = transaction.customer!!.name
                tvPhone.text = transaction.customer.phone
                tvCategoryName.text = getString(R.string.pelanggan)
                ivCategoryTransation.setImageResource(R.drawable.ic_profile_24dp)
                cdInfo.setOnClickListener {
                    DialogCustomerDetail.showCustomerDetailDialog(this@ShowOrderEmployeeActivity, transaction.customer)
                }
            } else if (transaction.type == "resupply") {
                tvName.text = transaction.store!!.name
                tvPhone.text = transaction.store.phone
                tvCategoryName.text = getString(R.string.agen)
                ivCategoryTransation.setImageResource(R.drawable.ic_shop_24dp)
                cdInfo.setOnClickListener {
                    DialogStoreDetail.showStoreDetailDialog(this@ShowOrderEmployeeActivity, transaction.store)
                }
            }
            tvQtyGasTotal.text = transaction.qty.toString()
            tvTotalPayment.text = Rupiah.convertToRupiah(transaction.totalPayment?.toDoubleOrNull()!!)
            tvPriceOneGas.text = Rupiah.convertToRupiah(transaction.totalPayment.toDoubleOrNull()!! / transaction.qty!!)
        }
        setupStatusButton(transaction)
    }

    private fun setupStatusButton(transaction: Transaction) {
        val statusTransaction = transaction.statusTransaction!!.id
        val typeTransaction = transaction.type.toString()
        val idTransaction = transaction.id.toString()

        when (statusTransaction) {
            1 -> with(binding) {
                btnCancelTransaction.visibility = View.GONE
                btnUpdateTransaction.visibility = View.VISIBLE
                noteLayout.visibility = View.GONE
                btnUpdateTransaction.text = getString(R.string.berangkat)
                btnUpdateTransaction.setOnClickListener { inProgressTransaction(idTransaction, typeTransaction) }
            }
            2 -> with(binding) {
                btnCancelTransaction.visibility = View.VISIBLE
                btnUpdateTransaction.visibility = View.VISIBLE
                noteLayout.visibility = View.GONE
                btnCancelTransaction.setOnClickListener {
                    showBottomSheet(idTransaction, typeTransaction)
                }
                btnUpdateTransaction.text = if (typeTransaction == "purchase") getString(R.string.sudah_diantar) else getString(R.string.sudah_dibeli)
                btnUpdateTransaction.setOnClickListener { completedTransaction(idTransaction, typeTransaction) }
            }
            3 -> with(binding) {
                noteLayout.visibility = View.GONE
                viewModel.showOperationTransaction(idTransaction, typeTransaction).observe(this@ShowOrderEmployeeActivity) { result ->
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            val data = result.data.operationTransaction
                            if (data != null) {
                                btnCancelTransaction.visibility = View.GONE
                                btnUpdateTransaction.visibility = View.VISIBLE
                                btnUpdateTransaction.text =
                                    getString(R.string.lihat_status_pengajuan_biaya_operasional)
                                btnUpdateTransaction.setOnClickListener {
                                    showCostDetailDialog(data)
                                }
                            } else {
                                btnUpdateTransaction.visibility = View.VISIBLE
                                btnCancelTransaction.visibility = View.GONE
                                btnUpdateTransaction.text = getString(R.string.ajukan_biaya_operasional)
                                btnUpdateTransaction.setOnClickListener {
                                    showCostDialog(idTransaction, typeTransaction)
                                }
                            }
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showAlert(result.error)
                            Log.e("error customer:", result.error.toString())
                        }
                    }
                }

            }
            4 -> with(binding) {
                if (transaction.note != null) {
                    noteLayout.visibility = View.VISIBLE
                    tvNote.text = transaction.note.toString()
                } else {
                    noteLayout.visibility = View.GONE
                }
            }
            else -> with(binding) {
                btnCancelTransaction.visibility = View.GONE
                btnUpdateTransaction.visibility = View.GONE
            }
        }
    }
    private fun showBottomSheet(idTransaction: String, type: String) {
        val preDefinedReasons = listOf(
            "Alamat tidak jelas",
            "Pelanggan tidak tersedia",
            "Kendala teknis",
            "Kendala keselamatan"
        )

        ProblemBottomSheet.showBottomSheet(this, preDefinedReasons) { reason ->
            cancelledTransaction(idTransaction, type, reason)
        }
    }

    private fun showCostDialog(idTransaction: String, type: String) {
        val costDialog = Dialog(this@ShowOrderEmployeeActivity, R.style.CustomDialogTheme)
        costDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        costDialog.setContentView(R.layout.dialog_cost)
        costDialog.setCancelable(false)

        val btnYes = costDialog.findViewById<View>(R.id.btn_yes)
        val btnNo = costDialog.findViewById<View>(R.id.btn_no)

        btnYes.setOnClickListener {
            costDialog.dismiss()
            showCostFormDialog(idTransaction, type)
        }

        btnNo.setOnClickListener {
            costDialog.dismiss()
        }

        costDialog.show()
    }
    private fun showCostFormDialog(idTransaction: String, type: String) {
        val costFormDialog = Dialog(this@ShowOrderEmployeeActivity, R.style.CustomDialogTheme)
        costFormDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        costFormDialog.setContentView(R.layout.dialog_cost_form)

        val edCostDescription = costFormDialog.findViewById<EditText>(R.id.ed_cost_description)
        val edCostPrice = costFormDialog.findViewById<EditText>(R.id.ed_cost_price)
        val btnSubmit = costFormDialog.findViewById<MaterialButton>(R.id.btn_submit)
        val btnCancel = costFormDialog.findViewById<MaterialButton>(R.id.btn_cancel)

        btnSubmit.setOnClickListener {
            val costDescription = edCostDescription.text.toString()
            val costPrice = edCostPrice.text.toString()

            if (costDescription.isNotEmpty() && costPrice.isNotEmpty()) {
                createNewOperationTransaction(idTransaction, costDescription, costPrice, type)
                costFormDialog.dismiss()
            } else {
                showAlert(getString(R.string.lengkapi_data_yang_belum_terisi))
            }
        }

        btnCancel.setOnClickListener {
            costFormDialog.dismiss()
        }

        costFormDialog.setCancelable(false)
        costFormDialog.show()
    }

    private fun showCostDetailDialog(data: OperationTransaction) {
        val costDetailDialog = Dialog(this@ShowOrderEmployeeActivity, R.style.CustomDialogTheme)
        costDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        costDetailDialog.setContentView(R.layout.dialog_cost_detail)

        val tvCostDescription = costDetailDialog.findViewById<TextView>(R.id.tv_cost_description)
        val tvCostPrice = costDetailDialog.findViewById<TextView>(R.id.tv_cost_price)
        val tvCostVerified = costDetailDialog.findViewById<TextView>(R.id.tv_cost_verified)
        val costDescription = data.note.toString()
        val costPrice = data.totalPayment.toString()
        val costVerified = data.verified
        tvCostDescription.text = costDescription
        tvCostPrice.text = costPrice
        when (costVerified) {
            0 -> {
                tvCostVerified.text = getString(R.string.pengajuan_ditolak)
            }
            1 -> {
                tvCostVerified.text = getString(R.string.pengajuan_diterima)
            }
            else -> {
                tvCostVerified.text = getString(R.string.proses_pengajuan)
            }
        }

        val btnClose = costDetailDialog.findViewById<MaterialButton>(R.id.btn_close)

        btnClose.setOnClickListener {
            costDetailDialog.dismiss()
        }

        costDetailDialog.setCancelable(true)
        costDetailDialog.show()
    }

    private fun createNewOperationTransaction(idTransaction: String, note: String, totalPayment: String, type: String) {
        viewModel.createNewOperationTransaction(idTransaction,note, totalPayment, type).observe(this){ result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showAlert(result.data.message.toString())
                    setupData(idTransaction, type)
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun inProgressTransaction(id: String, type: String) {
        viewModel.inProgressTransaction(id, type).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showAlert(result.data.message.toString())
                    setupData(id, type)
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun completedTransaction(id: String, type: String) {
        viewModel.completedTransaction(id, type).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showAlert(result.data.message.toString())
                    setupData(id, type)
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun cancelledTransaction(id: String, type: String, note: String) {
        viewModel.cancelledTransaction(id, type, note).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    showAlert(result.data.message.toString())
                    setupData(id, type)
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
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

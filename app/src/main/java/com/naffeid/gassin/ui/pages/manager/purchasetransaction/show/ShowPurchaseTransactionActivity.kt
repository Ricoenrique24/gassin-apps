package com.naffeid.gassin.ui.pages.manager.purchasetransaction.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListPurchaseItem
import com.naffeid.gassin.data.remote.response.Purchase
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.ActivityShowPurchaseTransactionBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.edit.EditPurchaseTransactionActivity

class ShowPurchaseTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowPurchaseTransactionBinding
    private val viewModel by viewModels<ShowPurchaseTransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPurchaseTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val idPurchase = intent.getStringExtra("PURCHASE")
        val updatePurchase = intent.getBooleanExtra("PURCHASE-UPDATED",false)
        if (updatePurchase) {
            if (idPurchase != null) setupData(idPurchase)
        }
        if (idPurchase != null) setupData(idPurchase)
        setupTobBar()
    }

    private fun setupData(id: String) {
        viewModel.showPurchaseTransaction(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val purchaseData = result.data.purchase
                    if(purchaseData != null) setupView(purchaseData)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun setupView(purchase: Purchase) {
        with(binding) {
            tvStatusTransaction.text = purchase.statusTransaction?.status
            tvStatusTransactionDesc.text = purchase.statusTransaction?.status
            tvCustomerName.text = purchase.customer!!.name
            tvCustomerPhone.text = purchase.customer.phone
            tvEmployeeName.text = purchase.user!!.name
            tvEmployeePhone.text = purchase.user.phone
            tvQtyGasTotal.text = purchase.qty.toString()
            tvTotalPayment.text = Rupiah.convertToRupiah(purchase.totalPayment?.toDoubleOrNull()!!)
            tvPriceOneGas.text = Rupiah.convertToRupiah(purchase.totalPayment.toDoubleOrNull()!! / purchase.qty!!)

            val statusTransaction = purchase.status
            when(statusTransaction) {
                "1" -> {
                    btnEditPurchase.visibility = View.VISIBLE
                    btnCancelPurchase.visibility = View.VISIBLE
                    btnCancelPurchase.setOnClickListener {
                        val id = purchase.id.toString()
                        cancelledPurchaseTransaction(id)
                    }
                    btnEditPurchase.setOnClickListener {
                        val purchaseData = ListPurchaseItem(
                            id = purchase.id,
                            note = purchase.note.toString(),
                            idCustomer = purchase.idCustomer,
                            qty = purchase.qty,
                            statusTransaction = purchase.statusTransaction,
                            idUser = purchase.idUser,
                            user = purchase.user,
                            totalPayment = purchase.totalPayment,
                            status = purchase.status,
                            customer = purchase.customer

                        )
                        editPurchaseTransaction(purchaseData)
                    }
                }
                "2" -> {
                    btnEditPurchase.visibility = View.VISIBLE
                    btnCancelPurchase.visibility = View.VISIBLE
                    btnCancelPurchase.setOnClickListener {
                        val id = purchase.id.toString()
                        cancelledPurchaseTransaction(id)
                    }
                    btnEditPurchase.setOnClickListener {
                        val purchaseData = ListPurchaseItem(
                            id = purchase.id,
                            note = purchase.note.toString(),
                            idCustomer = purchase.idCustomer,
                            qty = purchase.qty,
                            statusTransaction = purchase.statusTransaction,
                            idUser = purchase.idUser,
                            user = purchase.user,
                            totalPayment = purchase.totalPayment,
                            status = purchase.status,
                            customer = purchase.customer

                        )
                        editPurchaseTransaction(purchaseData)
                    }
                }
                "3" -> {
                    btnCancelPurchase.visibility = View.GONE
                    btnEditPurchase.visibility = View.GONE
                }
                "4" -> {
                    btnCancelPurchase.visibility = View.GONE
                    btnEditPurchase.visibility = View.GONE
                }
                else -> {
                    btnCancelPurchase.visibility = View.GONE
                    btnEditPurchase.visibility = View.GONE
                }

            }

        }
    }

    private fun editPurchaseTransaction(data: ListPurchaseItem) {
        val intentToDetail = Intent(this@ShowPurchaseTransactionActivity, EditPurchaseTransactionActivity::class.java)
        intentToDetail.putExtra("PURCHASE", data.id.toString())
        startActivity(intentToDetail)
    }
    private fun cancelledPurchaseTransaction(id: String) {
        viewModel.cancelledPurchaseTransaction(id).observe(this){ result->
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
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
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
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }
}
package com.naffeid.gassin.ui.pages.manager.customer.create

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityCreateCustomerBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity

class CreateCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCustomerBinding
    private val viewModel by viewModels<CreateCustomerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromChooseCustomer = intent.getBooleanExtra("FROM-CHOOSE-CUSTOMER",false)
        val fromIndexCustomer = intent.getBooleanExtra("FROM-INDEX-CUSTOMER",false)

        validate(fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer)
        setupTopBar(fromCreatePurchase, fromEditPurchase,fromChooseCustomer, fromIndexCustomer)
    }

    private fun validate(fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromChooseCustomer:Boolean, fromIndexCustomer:Boolean) {
        binding.btnCreateCustomer.setOnClickListener {
            val name = binding.edCustomerName.text.toString()
            val linkMap = binding.edCustomerLinkMap.text.toString()
            val address = binding.edCustomerAddress.text.toString()
            val phone = binding.edCustomerPhone.text.toString()
            val price = binding.edCustomerPrice.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
                createCustomer(name, phone, address, linkMap, price, fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer)
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun createCustomer(name: String, phone: String, address: String, linkMap: String, price: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromChooseCustomer:Boolean, fromIndexCustomer:Boolean) {
        viewModel.createNewCustomer(name, phone, address, linkMap, price).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(it.error)
                        Log.e("error search customer:", it.error.toString())
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(it.data.message.toString())
                        when {
                            fromChooseCustomer -> navigateToChooseCustomer(fromCreatePurchase, fromEditPurchase)
                            fromIndexCustomer -> navigateToIndexCustomer()
                            else -> {
                                showAlert("Halaman Tidak Dapat Ditemukan")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToChooseCustomer(fromCreatePurchase:Boolean, fromEditPurchase:Boolean) {
        val intentToChoose = Intent(this@CreateCustomerActivity, ChooseCustomerActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToChoose.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToChoose.putExtra("FROM-CREATE-CUSTOMER",true)
        startActivity(intentToChoose)
        finish()
    }
    private fun navigateToIndexCustomer() {
        val intentToIndex = Intent(this@CreateCustomerActivity, IndexCustomerActivity::class.java)
        intentToIndex.putExtra("FROM-CREATE-CUSTOMER", true)
        startActivity(intentToIndex)
        finish()
    }

    private fun setupTopBar(fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromChooseCustomer: Boolean, fromIndexCustomer: Boolean) {
        if (fromChooseCustomer && fromIndexCustomer) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromChooseCustomer -> {
                    Intent(this@CreateCustomerActivity, ChooseCustomerActivity::class.java).apply {
                        putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
                        putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
                    }
                }
                fromIndexCustomer -> {
                    Intent(this@CreateCustomerActivity, IndexCustomerActivity::class.java)
                }
                else -> {
                    showAlert("Halaman Tidak Dapat Ditemukan")
                    return@setOnClickListener
                }
            }
            startActivity(intentToHome)
            finish()
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
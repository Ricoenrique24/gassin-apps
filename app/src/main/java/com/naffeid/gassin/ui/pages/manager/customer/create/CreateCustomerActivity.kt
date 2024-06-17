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
        setupTobBar()
        validate()
    }

    private fun validate() {
        binding.btnCreateCustomer.setOnClickListener {
            val name = binding.edCustomerName.text.toString()
            val linkMap = binding.edCustomerLinkMap.text.toString()
            val address = binding.edCustomerAddress.text.toString()
            val phone = binding.edCustomerPhone.text.toString()
            val price = binding.edCustomerPrice.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
                createCustomer(name, phone, address, linkMap, price)
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun createCustomer(name: String, phone: String, address: String, linkMap: String, price: String) {
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
                        showAlert(getString(R.string.login_success))
                        navigateToIndexCustomer()
                    }
                }
            }
        }
    }

    private fun navigateToIndexCustomer() {
        val intentToIndex = Intent(this@CreateCustomerActivity, IndexCustomerActivity::class.java)
        intentToIndex.putExtra("CUSTOMERUPDATED", true)
        startActivity(intentToIndex)
        finish()
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
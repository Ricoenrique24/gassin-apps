package com.naffeid.gassin.ui.pages.manager.customer.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.Customer
import com.naffeid.gassin.data.remote.response.ListCustomerItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityShowCustomerBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.edit.EditCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity

class ShowCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowCustomerBinding
    private val viewModel by viewModels<ShowCustomerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val customer = intent.getParcelableExtra<ListCustomerItem>("CUSTOMER")
        val updateCustomer = intent.getBooleanExtra("CUSTOMERUPDATED",false)
        if (updateCustomer) {
            if (customer != null) setupData(customer)
        }
        if (customer != null) setupData(customer)
        setupTobBar(updateCustomer)
    }

    private fun setupData(customer: ListCustomerItem) {
        val id = customer.id.toString()
        viewModel.showCustomer(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val customerData = result.data.customer
                    if(customerData !=null) setupView(customerData)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(customer: Customer) {
        with(binding){
            edCustomerName.setText(customer.name)
            edCustomerLinkMap.setText(customer.linkMap)
            edCustomerAddress.setText(customer.address)
            edCustomerPhone.setText(customer.phone)
            edCustomerPrice.setText(customer.price)
            btnEditCustomer.setOnClickListener {
                val customerData = ListCustomerItem(
                    id = customer.id,
                    name = customer.name,
                    linkMap = customer.linkMap,
                    address = customer.address,
                    phone = customer.phone,
                    price = customer.price
                )
                editCustomer(customerData)
            }
            btnDeleteCustomer.setOnClickListener {
                deleteCustomer(customer.id.toString())
            }
        }
    }

    private fun editCustomer(data: ListCustomerItem) {
        val intentToDetail = Intent(this@ShowCustomerActivity, EditCustomerActivity::class.java)
        intentToDetail.putExtra("CUSTOMER", data)
        startActivity(intentToDetail)
    }

    private fun deleteCustomer(id: String) {
        viewModel.deleteCustomer(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(getString(R.string.agen_berhasil_dihapus))
                    navigateToIndexCustomer()
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error delete customer:", result.error.toString())
                }
            }
        }
    }

    private fun navigateToIndexCustomer() {
        val intentToIndex = Intent(this@ShowCustomerActivity, IndexCustomerActivity::class.java)
        intentToIndex.putExtra("CUSTOMERUPDATED", true)
        startActivity(intentToIndex)
        finish()
    }

    private fun setupTobBar(updateCustomer: Boolean) {
        binding.btnBack.setOnClickListener {
            if (updateCustomer) {
                navigateToIndexCustomer()
            } else {
                onBackPressed()
            }
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
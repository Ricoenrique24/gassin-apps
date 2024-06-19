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
import com.naffeid.gassin.ui.pages.manager.choose.customer.ChooseCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.edit.EditCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.data.model.Customer as CustomerModel

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
        val fromChooseCustomer = intent.getBooleanExtra("FROM-CHOOSE-CUSTOMER",false)
        if (updateCustomer) {
            if (customer != null) setupData(customer,updateCustomer,fromChooseCustomer)
        }
        if (customer != null) setupData(customer,updateCustomer,fromChooseCustomer)
        setupTobBar(updateCustomer,fromChooseCustomer)
    }

    private fun setupData(customer: ListCustomerItem, updateCustomer:Boolean, fromChooseCustomer:Boolean) {
        val id = customer.id.toString()
        viewModel.showCustomer(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val customerData = result.data.customer
                    if (updateCustomer) {
                        viewModel.getCustomer().observe(this) { customer ->
                            if (customer.isNotEmpty()) {
                                val idCustomer = customer.id.toString()
                                if (id == idCustomer) {
                                    viewModel.deleteCustomer()
                                    val dataCustomer = CustomerModel(
                                        id = customerData?.id ?: 0,
                                        name = customerData?.name ?: "",
                                        linkMap = customerData?.linkMap ?: "",
                                        address = customerData?.address ?: "",
                                        phone = customerData?.phone ?: "",
                                        price = customerData?.price ?: ""
                                    )
                                    viewModel.saveCustomer(dataCustomer)
                                }
                            }
                        }
                    }
                    if(customerData !=null) setupView(customerData,fromChooseCustomer)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(customer: Customer,fromChooseCustomer:Boolean) {
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
                editCustomer(customerData,fromChooseCustomer)
            }
            btnDeleteCustomer.setOnClickListener {
                deleteCustomer(customer.id.toString(),fromChooseCustomer)
            }
        }
    }

    private fun editCustomer(data: ListCustomerItem,fromChooseCustomer:Boolean) {
        if (fromChooseCustomer) {
            val intentToDetail = Intent(this@ShowCustomerActivity, EditCustomerActivity::class.java)
            intentToDetail.putExtra("CUSTOMER", data)
            intentToDetail.putExtra("FROM-CHOOSE-CUSTOMER",true)
            startActivity(intentToDetail)
        } else {
            val intentToDetail = Intent(this@ShowCustomerActivity, EditCustomerActivity::class.java)
            intentToDetail.putExtra("CUSTOMER", data)
            intentToDetail.putExtra("FROM-CHOOSE-CUSTOMER",false)
            startActivity(intentToDetail)
        }
    }

    private fun deleteCustomer(id: String,fromChooseCustomer:Boolean) {
        viewModel.deleteCustomer(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(getString(R.string.agen_berhasil_dihapus))
                    viewModel.getCustomer().observe(this) { customer ->
                        if (customer.isNotEmpty()) {
                            val idCustomer = customer.id.toString()
                            if (id == idCustomer) {
                                viewModel.deleteCustomer()
                            }
                        }
                    }
                    if (fromChooseCustomer){
                        navigateToChooseCustomer()
                    } else {
                        navigateToIndexCustomer()
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error delete customer:", result.error.toString())
                }
            }
        }
    }
    private fun CustomerModel.isNotEmpty(): Boolean {
        return this != CustomerModel(0, "", "", "", "", "")
    }

    private fun navigateToIndexCustomer() {
        val intentToIndex = Intent(this@ShowCustomerActivity, IndexCustomerActivity::class.java)
        intentToIndex.putExtra("CUSTOMERUPDATED", true)
        startActivity(intentToIndex)
        finish()
    }
    private fun navigateToChooseCustomer() {
        val intentToChooseCustomer = Intent(this@ShowCustomerActivity, ChooseCustomerActivity::class.java)
        intentToChooseCustomer.putExtra("CUSTOMERUPDATED", true)
        startActivity(intentToChooseCustomer)
        finish()
    }

    private fun setupTobBar(updateCustomer: Boolean, fromChooseCustomer:Boolean) {
        binding.btnBack.setOnClickListener {
            if (updateCustomer) {
                if (fromChooseCustomer){
                    navigateToChooseCustomer()
                } else {
                    navigateToIndexCustomer()
                }
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
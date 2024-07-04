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
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromChooseCustomer = intent.getBooleanExtra("FROM-CHOOSE-CUSTOMER",false)
        val fromIndexCustomer = intent.getBooleanExtra("FROM-INDEX-CUSTOMER",false)
        val fromEditCustomer = intent.getBooleanExtra("FROM-EDIT-CUSTOMER",false)

        if (fromEditCustomer) {
            if (customer != null) setupData(customer, fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer, true)
        }
        if (customer != null) setupData(customer, fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer, fromEditCustomer)
        setupTopBar(fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer)
    }

    private fun setupData(customer: ListCustomerItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromChooseCustomer:Boolean, fromIndexCustomer: Boolean, fromEditCustomer:Boolean) {
        val id = customer.id.toString()
        viewModel.showCustomer(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val customerData = result.data.customer
                    if (fromEditCustomer) {
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
                    if(customerData !=null) setupView(customerData, fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(customer: Customer, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromChooseCustomer:Boolean, fromIndexCustomer: Boolean) {
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
                editCustomer(customerData,fromCreatePurchase, fromEditPurchase, fromChooseCustomer, fromIndexCustomer)
            }
            btnDeleteCustomer.setOnClickListener {
                deleteCustomer(customer.id.toString(), fromCreatePurchase, fromEditPurchase, fromChooseCustomer)
            }
        }
    }

    private fun editCustomer(data: ListCustomerItem, fromCreatePurchase:Boolean,fromEditPurchase:Boolean, fromChooseCustomer:Boolean, fromIndexCustomer: Boolean) {
        val intentToEdit = Intent(this@ShowCustomerActivity, EditCustomerActivity::class.java)
        intentToEdit.putExtra("CUSTOMER", data)
        intentToEdit.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToEdit.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToEdit.putExtra("FROM-CHOOSE-CUSTOMER",fromChooseCustomer)
        intentToEdit.putExtra("FROM-INDEX-CUSTOMER",fromIndexCustomer)
        startActivity(intentToEdit)
    }

    private fun deleteCustomer(id: String,fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromChooseCustomer:Boolean) {
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
                        navigateToChooseCustomer(fromCreatePurchase, fromEditPurchase, true)
                    } else {
                        navigateToIndexCustomer(true)
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

    private fun navigateToIndexCustomer(fromEditCustomer:Boolean) {
        val intentToIndex = Intent(this@ShowCustomerActivity, IndexCustomerActivity::class.java)
        intentToIndex.putExtra("FROM-EDIT-CUSTOMER", fromEditCustomer)
        startActivity(intentToIndex)
        finish()
    }
    private fun navigateToChooseCustomer(fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromEditCustomer:Boolean) {
        val intentToChoose = Intent(this@ShowCustomerActivity, ChooseCustomerActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToChoose.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToChoose.putExtra("FROM-EDIT-CUSTOMER",fromEditCustomer)
        startActivity(intentToChoose)
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
                    Intent(this@ShowCustomerActivity, ChooseCustomerActivity::class.java).apply {
                        putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
                        putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
                    }
                }
                fromIndexCustomer -> {
                    Intent(this@ShowCustomerActivity, IndexCustomerActivity::class.java)
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
package com.naffeid.gassin.ui.pages.manager.customer.edit

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListCustomerItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityEditCustomerBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.edit.EditCustomerViewModel
import com.naffeid.gassin.ui.pages.manager.customer.show.ShowCustomerActivity

class EditCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCustomerBinding
    private val viewModel by viewModels<EditCustomerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupTobBar()
        val customer = intent.getParcelableExtra<ListCustomerItem>("CUSTOMER")
        if (customer != null) setupView(customer)
    }

    private fun setupView(customer: ListCustomerItem) {
        with(binding) {
            edCustomerName.setText(customer.name)
            edCustomerLinkMap.setText(customer.linkMap)
            edCustomerAddress.setText(customer.address)
            edCustomerPhone.setText(customer.phone)
            edCustomerPrice.setText(customer.price)
            btnUpdateCustomer.setOnClickListener {
                validate(customer.id.toString())
            }
        }
    }

    private fun validate(id: String) {
        val name = binding.edCustomerName.text.toString()
        val linkMap = binding.edCustomerLinkMap.text.toString()
        val address = binding.edCustomerAddress.text.toString()
        val phone = binding.edCustomerPhone.text.toString()
        val price = binding.edCustomerPrice.text.toString()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
            updateCustomer(id, name, phone, address, linkMap, price)
        } else {
            showAlert(getString(R.string.please_fill_in_all_input))
        }
    }

    private fun updateCustomer(id:String,name: String, phone: String, address: String, linkMap: String, price: String) {
        viewModel.updateCustomer(id, name, phone, address, linkMap, price).observe(this) {
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
                        showAlert(getString(R.string.agen_berhasil_diupdate))
                        val customer = it.data.customer
                        val customerData = ListCustomerItem(
                            id = customer?.id,
                            name = customer?.name,
                            linkMap = customer?.linkMap,
                            address = customer?.address,
                            phone = customer?.phone,
                            price = customer?.price
                        )
                        navigateToShowCustomer(customerData)
                    }
                }
            }
        }
    }

    private fun navigateToShowCustomer(data: ListCustomerItem) {
        val intentToShow = Intent(this@EditCustomerActivity, ShowCustomerActivity::class.java)
        intentToShow.putExtra("CUSTOMERUPDATED", true)
        intentToShow.putExtra("CUSTOMER", data)
        startActivity(intentToShow)
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
package com.naffeid.gassin.ui.pages.manager.choose.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Customer
import com.naffeid.gassin.data.remote.response.ListCustomerItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityChooseCustomerBinding
import com.naffeid.gassin.ui.adapter.ChooseCustomerAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.create.CreateCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.show.ShowCustomerActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity

class ChooseCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseCustomerBinding
    private val viewModel by viewModels<ChooseCustomerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var chooseCustomerAdapter: ChooseCustomerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromCreateCustomer = intent.getBooleanExtra("FROM-CREATE-CUSTOMER",false)
        if (fromCreateCustomer) {
            setupData()
        }
        setupRecyclerView(fromCreatePurchase)
        setupView(fromCreatePurchase)
        setupData()
        setupTopBar()
    }

    private fun setupData() {
        showAllCustomer()
    }

    private fun setupView(fromCreatePurchase:Boolean) {
        with(binding){
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                val query = searchView.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchCustomer(query)
                } else {
                    showAllCustomer()
                }
                false
            }

            //Create Customer
            btnAddStory.setOnClickListener {
                navigateToCreateCustomer(fromCreatePurchase)
            }
        }
    }

    private fun setupRecyclerView(fromCreatePurchase:Boolean) {
        chooseCustomerAdapter = ChooseCustomerAdapter()
        chooseCustomerAdapter.setOnItemClickCallback(object : ChooseCustomerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListCustomerItem) {
                navigateToShowCustomer(data, fromCreatePurchase)
            }

            override fun onChooseCustomerClicked(data: ListCustomerItem) {
                selectedCustomer(data, fromCreatePurchase)

            }
        })
        binding.rvCustomer.apply {
            layoutManager = LinearLayoutManager(this@ChooseCustomerActivity)
            adapter = chooseCustomerAdapter
        }
    }

    private fun showAllCustomer() {
        viewModel.showAllCustomer().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listCustomer = result.data.listCustomer
                    chooseCustomerAdapter.submitList(listCustomer)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error customer:", result.error.toString())
                }
            }
        }
    }

    private fun searchCustomer(query: String) {
        viewModel.searchCustomer(query).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listCustomer = result.data.listCustomer
                    chooseCustomerAdapter.submitList(listCustomer)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search customer:", result.error.toString())
                }
            }
        }
    }

    private fun selectedCustomer(data: ListCustomerItem, fromCreatePurchase: Boolean){
        viewModel.deleteCustomer()
        viewModel.saveCustomer(
            Customer(
                id = data.id ?: 0,
                name = data.name ?: "",
                phone = data.phone ?: "",
                address = data.address ?: "",
                linkMap = data.linkMap ?: "",
                price = data.price ?: ""
            )
        )
        showAlert(getString(R.string.berhasil_memilih_customer))
        navigateToCreatePurchase()
    }

    private fun navigateToCreateCustomer(fromCreatePurchase:Boolean) {
        val intentToCreate = Intent(this@ChooseCustomerActivity, CreateCustomerActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToCreate.putExtra("FROM-CHOOSE-CUSTOMER",true)
        startActivity(intentToCreate)
    }

    private fun navigateToShowCustomer(data: ListCustomerItem, fromCreatePurchase:Boolean) {
        val intentToDetail = Intent(this@ChooseCustomerActivity, ShowCustomerActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToDetail.putExtra("FROM-CHOOSE-CUSTOMER",true)
        intentToDetail.putExtra("CUSTOMER", data)
        startActivity(intentToDetail)
    }

    private fun navigateToCreatePurchase() {
        val intentToCreate = Intent(this@ChooseCustomerActivity, CreatePurchaseTransactionActivity::class.java)
        startActivity(intentToCreate)
        finish()
    }


    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            val intentToHome = Intent(this@ChooseCustomerActivity, CreatePurchaseTransactionActivity::class.java)
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
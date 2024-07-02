package com.naffeid.gassin.ui.pages.manager.customer.index

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListCustomerItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityIndexCustomerBinding
import com.naffeid.gassin.ui.adapter.CustomerAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.create.CreateCustomerActivity
import com.naffeid.gassin.ui.pages.manager.customer.show.ShowCustomerActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity

class IndexCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIndexCustomerBinding
    private val viewModel by viewModels<IndexCustomerViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var customerAdapter: CustomerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val updateCustomer = intent.getBooleanExtra("FROM-CREATE-CUSTOMER",false)
        if (updateCustomer) {
            showAllCustomer()
        }
        setupTopBar()
        setupRecyclerView()
        setupView()
        showAllCustomer()
    }

    private fun setupView() {
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
                navigateToCreateCustomer()
            }
        }
    }

    private fun setupRecyclerView() {
        customerAdapter = CustomerAdapter()
        customerAdapter.setOnItemClickCallback(object : CustomerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListCustomerItem) {
                navigateToShowCustomer(data)
            }
        })
        binding.rvCustomer.apply {
            layoutManager = LinearLayoutManager(this@IndexCustomerActivity)
            adapter = customerAdapter
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
                    customerAdapter.submitList(listCustomer)
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
                    customerAdapter.submitList(listCustomer)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search customer:", result.error.toString())
                }
            }
        }
    }

    private fun navigateToShowCustomer(data: ListCustomerItem, ) {
        val intentToDetail = Intent(this@IndexCustomerActivity, ShowCustomerActivity::class.java)
        intentToDetail.putExtra("FROM-INDEX-CUSTOMER",true)
        intentToDetail.putExtra("CUSTOMER", data)
        startActivity(intentToDetail)
    }

    private fun navigateToCreateCustomer() {
        val intentToCreate = Intent(this@IndexCustomerActivity, CreateCustomerActivity::class.java)
        intentToCreate.putExtra("FROM-INDEX-CUSTOMER",true)
        startActivity(intentToCreate)
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            val intentToHome = Intent(this@IndexCustomerActivity, ManagerMainActivity::class.java)
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
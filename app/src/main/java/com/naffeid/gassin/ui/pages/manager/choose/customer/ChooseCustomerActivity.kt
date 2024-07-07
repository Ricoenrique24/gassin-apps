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
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.edit.EditPurchaseTransactionActivity

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
        val idPurchase = intent.getStringExtra("PURCHASE")
        val quantity = intent.getStringExtra("QUANTITY")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromCreateCustomer = intent.getBooleanExtra("FROM-CREATE-CUSTOMER",false)
        if (fromCreateCustomer) {
            setupData()
        }
        setupRecyclerView(quantity.toString(), idPurchase.toString(), fromCreatePurchase, fromEditPurchase)
        setupView(quantity.toString(),fromCreatePurchase, fromEditPurchase)
        setupData()
        setupTopBar(quantity.toString(), fromCreatePurchase, fromEditPurchase, idPurchase.toString())
    }

    private fun setupData() {
        showAllCustomer()
    }

    private fun setupView(qty:String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean) {
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
                navigateToCreateCustomer(qty, fromCreatePurchase, fromEditPurchase)
            }
        }
    }

    private fun setupRecyclerView(qty:String, idPurchase:String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean) {
        chooseCustomerAdapter = ChooseCustomerAdapter()
        chooseCustomerAdapter.setOnItemClickCallback(object : ChooseCustomerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListCustomerItem) {
                navigateToShowCustomer(qty, data, fromCreatePurchase, fromEditPurchase)
            }

            override fun onChooseCustomerClicked(data: ListCustomerItem) {
                selectedCustomer(qty, data, idPurchase, fromCreatePurchase, fromEditPurchase)

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

    private fun selectedCustomer(qty:String, data: ListCustomerItem, idPurchase:String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean){
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
        when {
            fromCreatePurchase -> {
                navigateToCreatePurchase(qty)
            }
            fromEditPurchase -> {
                navigateToEditPurchase(idPurchase, qty)
            }
        }
    }

    private fun navigateToCreateCustomer(qty:String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean) {
        val intentToCreate = Intent(this@ChooseCustomerActivity, CreateCustomerActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToCreate.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToCreate.putExtra("FROM-CHOOSE-CUSTOMER",true)
        intentToCreate.putExtra("QUANTITY", qty)
        startActivity(intentToCreate)
    }

    private fun navigateToShowCustomer(qty:String, data: ListCustomerItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean) {
        val intentToDetail = Intent(this@ChooseCustomerActivity, ShowCustomerActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToDetail.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToDetail.putExtra("FROM-CHOOSE-CUSTOMER",true)
        intentToDetail.putExtra("CUSTOMER", data)
        intentToDetail.putExtra("QUANTITY", qty)
        startActivity(intentToDetail)
    }

    private fun navigateToCreatePurchase(qty:String) {
        val intentToCreate = Intent(this@ChooseCustomerActivity, CreatePurchaseTransactionActivity::class.java)
        intentToCreate.putExtra("QUANTITY", qty)
        startActivity(intentToCreate)
        finish()
    }
    private fun navigateToEditPurchase(idPurchase: String, qty:String) {
        val intentToEdit = Intent(this@ChooseCustomerActivity, EditPurchaseTransactionActivity::class.java)
        intentToEdit.putExtra("PURCHASE", idPurchase)
        intentToEdit.putExtra("CHOOSE-UPDATED", true)
        intentToEdit.putExtra("QUANTITY", qty)
        startActivity(intentToEdit)
        finish()
    }

    private fun setupTopBar(qty:String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, id:String) {
        if (fromCreatePurchase && fromEditPurchase) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromCreatePurchase -> {
                    Intent(this@ChooseCustomerActivity, CreatePurchaseTransactionActivity::class.java).apply {
                        putExtra("QUANTITY", qty)
                    }
                }
                fromEditPurchase -> {
                    Intent(this@ChooseCustomerActivity, EditPurchaseTransactionActivity::class.java).apply {
                        putExtra("PURCHASE", id)
                        putExtra("QUANTITY", qty)
                    }
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
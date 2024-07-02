package com.naffeid.gassin.ui.pages.manager.choose.employee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Employee
import com.naffeid.gassin.data.remote.response.ListEmployeeItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityChooseEmployeeBinding
import com.naffeid.gassin.ui.adapter.ChooseEmployeeAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.employee.create.CreateEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.employee.show.ShowEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity

class ChooseEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseEmployeeBinding
    private val viewModel by viewModels<ChooseEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var chooseEmployeeAdapter: ChooseEmployeeAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromCreateEmployee = intent.getBooleanExtra("FROM-CREATE-EMPLOYEE",false)
        if (fromCreateEmployee) {
            setupData()
        }
        setupRecyclerView(fromCreatePurchase, fromCreateResupply)
        setupView(fromCreatePurchase, fromCreateResupply)
        setupData()
        setupTopBar(fromCreatePurchase, fromCreateResupply)
    }
    private fun setupData() {
        showAllEmployee()
    }

    private fun setupView(fromCreatePurchase:Boolean, fromCreateResupply:Boolean) {
        with(binding){
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                val query = searchView.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchEmployee(query)
                } else {
                    showAllEmployee()
                }
                false
            }

            //Create Employee
            btnAddStory.setOnClickListener {
                navigateToCreateEmployee(fromCreatePurchase, fromCreateResupply)
            }
        }
    }

    private fun setupRecyclerView(fromCreatePurchase:Boolean, fromCreateResupply:Boolean) {
        chooseEmployeeAdapter = ChooseEmployeeAdapter()
        chooseEmployeeAdapter.setOnItemClickCallback(object : ChooseEmployeeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListEmployeeItem) {
                navigateToShowEmployee(data, fromCreatePurchase, fromCreateResupply)
            }

            override fun onChooseEmployeeClicked(data: ListEmployeeItem) {
                selectedEmployee(data, fromCreatePurchase, fromCreateResupply)
            }
        })
        binding.rvEmployee.apply {
            layoutManager = LinearLayoutManager(this@ChooseEmployeeActivity)
            adapter = chooseEmployeeAdapter
        }
    }

    private fun showAllEmployee() {
        viewModel.showAllEmployee().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listEmployee = result.data.listEmployee
                    chooseEmployeeAdapter.submitList(listEmployee)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun searchEmployee(query: String) {
        viewModel.searchEmployee(query).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listEmployee = result.data.listEmployee
                    chooseEmployeeAdapter.submitList(listEmployee)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search employee:", result.error.toString())
                }
            }
        }
    }
    private fun selectedEmployee(data: ListEmployeeItem, fromCreatePurchase: Boolean, fromCreateResupply: Boolean){
        viewModel.deleteEmployee()
        viewModel.saveEmployee(
            Employee(
                id = data.id ?: 0,
                name = data.name ?: "",
                username = data.username ?: "",
                email = data.email ?: "",
                phone = data.phone ?: "",
                role = data.role ?: "",
                token = data.apikey ?: ""
            )
        )
        showAlert(getString(R.string.berhasil_memilih_employee))
        navigateToCreateTransaction(fromCreatePurchase, fromCreateResupply)
    }
    private fun navigateToCreateEmployee(fromCreatePurchase:Boolean, fromCreateResupply:Boolean) {
        val intentToCreate = Intent(this@ChooseEmployeeActivity, CreateEmployeeActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToCreate.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToCreate.putExtra("FROM-CHOOSE-EMPLOYEE",true)
        startActivity(intentToCreate)
    }

    private fun navigateToShowEmployee(data:ListEmployeeItem, fromCreatePurchase:Boolean, fromCreateResupply:Boolean) {
        val intentToDetail = Intent(this@ChooseEmployeeActivity, ShowEmployeeActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToDetail.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToDetail.putExtra("FROM-CHOOSE-EMPLOYEE",true)
        intentToDetail.putExtra("EMPLOYEE", data)
        startActivity(intentToDetail)
    }

    private fun navigateToCreateTransaction(fromCreatePurchase: Boolean, fromCreateResupply: Boolean) {
        val intentToCreate = when {
            fromCreatePurchase -> Intent(this@ChooseEmployeeActivity, CreatePurchaseTransactionActivity::class.java)
            fromCreateResupply -> Intent(this@ChooseEmployeeActivity, CreateReSupplyTransactionActivity::class.java)
            else -> null
        }

        intentToCreate?.let {
            startActivity(it)
            finish()
        } ?: run {
            showAlert("Halaman Tidak Dapat Ditemukan")
        }
    }


    private fun setupTopBar(fromCreatePurchase: Boolean, fromCreateResupply: Boolean) {
        if (fromCreatePurchase && fromCreateResupply) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromCreatePurchase -> Intent(this@ChooseEmployeeActivity, CreatePurchaseTransactionActivity::class.java)
                fromCreateResupply -> Intent(this@ChooseEmployeeActivity, CreateReSupplyTransactionActivity::class.java)
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
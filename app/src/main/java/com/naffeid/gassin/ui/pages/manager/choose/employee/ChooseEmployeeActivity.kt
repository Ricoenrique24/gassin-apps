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
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.edit.EditPurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.edit.EditReSupplyTransactionActivity

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
        val idPurchase = intent.getStringExtra("PURCHASE")
        val idResupply = intent.getStringExtra("RESUPPLY")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromEditResupply = intent.getBooleanExtra("FROM-EDIT-RESUPPLY",false)
        val fromCreateEmployee = intent.getBooleanExtra("FROM-CREATE-EMPLOYEE",false)
        if (fromCreateEmployee) {
            setupData()
        }
        setupRecyclerView(idPurchase.toString(), idResupply.toString(), fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
        setupView(fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
        setupData()
        setupTopBar(fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, idPurchase.toString(), idResupply.toString())
    }
    private fun setupData() {
        showAllEmployee()
    }

    private fun setupView(fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
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
                navigateToCreateEmployee(fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
            }
        }
    }

    private fun setupRecyclerView(idPurchase:String, idResupply:String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        chooseEmployeeAdapter = ChooseEmployeeAdapter()
        chooseEmployeeAdapter.setOnItemClickCallback(object : ChooseEmployeeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListEmployeeItem) {
                navigateToShowEmployee(data, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
            }

            override fun onChooseEmployeeClicked(data: ListEmployeeItem) {
                selectedEmployee(data, idPurchase, idResupply, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
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
    private fun selectedEmployee(data: ListEmployeeItem, idPurchase: String, idResupply: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean){
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
        when {
            fromCreatePurchase -> {
                navigateToCreatePurchase()
            }
            fromEditPurchase -> {
                navigateToEditPurchase(idPurchase)

            }
            fromCreateResupply -> {
                navigateToCreateResupply()
            }
            fromEditResupply -> {
                navigateToEditResupply(idResupply)
            }
        }
    }

    private fun navigateToEditResupply(idResupply: String) {
        val intentToEdit = Intent(this@ChooseEmployeeActivity, EditReSupplyTransactionActivity::class.java)
        intentToEdit.putExtra("RESUPPLY", idResupply)
        intentToEdit.putExtra("CHOOSE-UPDATED", true)
        startActivity(intentToEdit)
        finish()
    }

    private fun navigateToCreateResupply() {
        val intentToCreateResupply = Intent(this@ChooseEmployeeActivity, CreateReSupplyTransactionActivity::class.java)
        startActivity(intentToCreateResupply)
        finish()
    }

    private fun navigateToEditPurchase(idPurchase: String) {
        val intentToEdit = Intent(this@ChooseEmployeeActivity, EditPurchaseTransactionActivity::class.java)
        intentToEdit.putExtra("PURCHASE", idPurchase)
        intentToEdit.putExtra("CHOOSE-UPDATED", true)
        startActivity(intentToEdit)
        finish()
    }

    private fun navigateToCreatePurchase() {
        val intentToCreateResupply = Intent(this@ChooseEmployeeActivity, CreatePurchaseTransactionActivity::class.java)
        startActivity(intentToCreateResupply)
        finish()
    }

    private fun navigateToCreateEmployee(fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        val intentToCreate = Intent(this@ChooseEmployeeActivity, CreateEmployeeActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToCreate.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToCreate.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToCreate.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
        intentToCreate.putExtra("FROM-CHOOSE-EMPLOYEE",true)
        startActivity(intentToCreate)
    }

    private fun navigateToShowEmployee(data:ListEmployeeItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        val intentToDetail = Intent(this@ChooseEmployeeActivity, ShowEmployeeActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToDetail.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToDetail.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToDetail.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
        intentToDetail.putExtra("FROM-CHOOSE-EMPLOYEE",true)
        intentToDetail.putExtra("EMPLOYEE", data)
        startActivity(intentToDetail)
    }

    private fun setupTopBar(fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, idPurchase: String, idResupply: String) {
        if (fromCreatePurchase && fromCreateResupply && fromEditPurchase && fromEditResupply) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromCreatePurchase -> Intent(this@ChooseEmployeeActivity, CreatePurchaseTransactionActivity::class.java)
                fromEditPurchase -> Intent(
                        this@ChooseEmployeeActivity,
                        EditPurchaseTransactionActivity::class.java
                    ).apply {
                        putExtra("PURCHASE", idPurchase)
                    }
                fromCreateResupply -> Intent(this@ChooseEmployeeActivity, CreateReSupplyTransactionActivity::class.java)
                fromEditResupply -> Intent(
                        this@ChooseEmployeeActivity,
                        CreateReSupplyTransactionActivity::class.java
                    ).apply {
                        putExtra("RESUPPLY", idResupply)
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
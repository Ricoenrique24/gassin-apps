package com.naffeid.gassin.ui.pages.manager.employee.index

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListEmployeeItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityIndexEmployeeBinding
import com.naffeid.gassin.ui.adapter.EmployeeAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.employee.create.CreateEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.employee.show.ShowEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity

class IndexEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIndexEmployeeBinding
    private val viewModel by viewModels<IndexEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var employeeAdapter: EmployeeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val updateEmployee = intent.getBooleanExtra("FROM-CREATE-EMPLOYEE",false)
        if (updateEmployee) {
            showAllEmployee()
        }
        setupTopBar()
        setupRecyclerView()
        setupView()
        showAllEmployee()
    }

    private fun setupView() {
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
                navigateToCreateEmployee()
            }
        }
    }

    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter()
        employeeAdapter.setOnItemClickCallback(object : EmployeeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListEmployeeItem) {
                navigateToShowEmployee(data)
            }
        })
        binding.rvEmployee.apply {
            layoutManager = LinearLayoutManager(this@IndexEmployeeActivity)
            adapter = employeeAdapter
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
                    employeeAdapter.submitList(listEmployee)
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
                    employeeAdapter.submitList(listEmployee)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search employee:", result.error.toString())
                }
            }
        }
    }
    
    private fun navigateToShowEmployee(data: ListEmployeeItem, ) {
        val intentToDetail = Intent(this@IndexEmployeeActivity, ShowEmployeeActivity::class.java)
        intentToDetail.putExtra("FROM-INDEX-EMPLOYEE",true)
        intentToDetail.putExtra("EMPLOYEE", data)
        startActivity(intentToDetail)
    }

    private fun navigateToCreateEmployee() {
        val intentToCreate = Intent(this@IndexEmployeeActivity, CreateEmployeeActivity::class.java)
        intentToCreate.putExtra("FROM-INDEX-EMPLOYEE",true)
        startActivity(intentToCreate)
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            val intentToHome = Intent(this@IndexEmployeeActivity, ManagerMainActivity::class.java)
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
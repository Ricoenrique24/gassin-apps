package com.naffeid.gassin.ui.pages.manager.employee.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.Employee
import com.naffeid.gassin.data.remote.response.ListEmployeeItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityShowEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.employee.edit.EditEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.data.model.Employee as EmployeeModel

class ShowEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowEmployeeBinding
    private val viewModel by viewModels<ShowEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val employee = intent.getParcelableExtra<ListEmployeeItem>("EMPLOYEE")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromChooseEmployee = intent.getBooleanExtra("FROM-CHOOSE-EMPLOYEE",false)
        val fromIndexEmployee = intent.getBooleanExtra("FROM-INDEX-EMPLOYEE",false)
        val fromEditEmployee = intent.getBooleanExtra("FROM-EDIT-EMPLOYEE",false)

        if (fromEditEmployee) {
            if (employee != null) setupData(employee, fromCreatePurchase, fromCreateResupply, fromChooseEmployee, fromIndexEmployee, true)
        }
        if (employee != null) setupData(employee, fromCreatePurchase, fromCreateResupply, fromChooseEmployee, fromIndexEmployee, fromEditEmployee)
        setupTopBar(fromCreatePurchase, fromCreateResupply, fromChooseEmployee, fromIndexEmployee)
    }

    private fun setupData(employee: ListEmployeeItem, fromCreatePurchase:Boolean, fromCreateResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean, fromEditEmployee:Boolean) {
        val id = employee.id.toString()
        viewModel.showEmployee(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val employeeData = result.data.employee
                    if (fromEditEmployee) {
                        viewModel.getEmployee().observe(this) { employee ->
                            if (employee.isNotEmpty()) {
                                val idEmployee = employee.id.toString()
                                if (id == idEmployee) {
                                    viewModel.deleteEmployee()
                                    val dataEmployee = EmployeeModel(
                                        id = employeeData?.id ?: 0,
                                        name = employeeData?.name ?: "",
                                        username = employeeData?.username ?: "",
                                        email = employeeData?.email ?: "",
                                        phone = employeeData?.phone ?: "",
                                        role = employeeData?.role ?: "",
                                        token = employeeData?.apikey ?: ""
                                    )
                                    viewModel.saveEmployee(dataEmployee)
                                }
                            }
                        }
                    }
                    if(employeeData !=null) setupView(employeeData,fromCreatePurchase, fromCreateResupply, fromChooseEmployee, fromIndexEmployee)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(employee: Employee, fromCreatePurchase:Boolean, fromCreateResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean) {
        with(binding){
            edEmployeeName.setText(employee.name)
            edEmployeeUsername.setText(employee.username)
            edEmployeeEmail.setText(employee.email)
            edEmployeePhone.setText(employee.phone)
            btnEditEmployee.setOnClickListener {
                val employeeData = ListEmployeeItem(
                    id = employee.id,
                    name = employee.name,
                    username = employee.username,
                    email = employee.email,
                    phone = employee.phone
                )
                editEmployee(employeeData,fromCreatePurchase, fromCreateResupply, fromChooseEmployee, fromIndexEmployee)
            }
            btnDeleteEmployee.setOnClickListener {
                deleteEmployee(employee.id.toString(), fromCreatePurchase, fromCreateResupply, fromChooseEmployee)
            }
        }
    }

    private fun editEmployee(data: ListEmployeeItem, fromCreatePurchase:Boolean, fromCreateResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean) {
        val intentToEdit = Intent(this@ShowEmployeeActivity, EditEmployeeActivity::class.java)
        intentToEdit.putExtra("EMPLOYEE", data)
        intentToEdit.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToEdit.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToEdit.putExtra("FROM-CHOOSE-EMPLOYEE",fromChooseEmployee)
        intentToEdit.putExtra("FROM-INDEX-EMPLOYEE",fromIndexEmployee)
        startActivity(intentToEdit)
    }

    private fun deleteEmployee(id: String, fromCreatePurchase:Boolean, fromCreateResupply:Boolean, fromChooseEmployee:Boolean) {
        viewModel.deleteEmployee(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(getString(R.string.agen_berhasil_dihapus))
                    viewModel.getEmployee().observe(this) { employee ->
                        if (employee.isNotEmpty()) {
                            val idEmployee = employee.id.toString()
                            if (id == idEmployee) {
                                viewModel.deleteEmployee()
                            }
                        }
                    }
                    if (fromChooseEmployee){
                        navigateToChooseEmployee(fromCreatePurchase, fromCreateResupply, true)
                    } else {
                        navigateToIndexEmployee(true)
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error delete employee:", result.error.toString())
                }
            }
        }
    }

    private fun EmployeeModel.isNotEmpty(): Boolean {
        return this != EmployeeModel(0, "", "", "", "", "","")
    }

    private fun navigateToIndexEmployee(fromEditEmployee:Boolean) {
        val intentToIndex = Intent(this@ShowEmployeeActivity, IndexEmployeeActivity::class.java)
        intentToIndex.putExtra("FROM-EDIT-EMPLOYEE", fromEditEmployee)
        startActivity(intentToIndex)
        finish()
    }
    private fun navigateToChooseEmployee(fromCreatePurchase:Boolean, fromCreateResupply:Boolean, fromEditEmployee:Boolean) {
        val intentToChoose = Intent(this@ShowEmployeeActivity, ChooseEmployeeActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToChoose.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToChoose.putExtra("FROM-EDIT-EMPLOYEE",fromEditEmployee)
        startActivity(intentToChoose)
        finish()
    }

    private fun setupTopBar(fromCreatePurchase: Boolean, fromCreateResupply: Boolean, fromChooseEmployee: Boolean, fromIndexEmployee: Boolean) {
        if (fromChooseEmployee && fromIndexEmployee) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromChooseEmployee -> {
                    Intent(this@ShowEmployeeActivity, ChooseEmployeeActivity::class.java).apply {
                        putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
                        putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
                    }
                }
                fromIndexEmployee -> {
                    Intent(this@ShowEmployeeActivity, IndexEmployeeActivity::class.java)
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
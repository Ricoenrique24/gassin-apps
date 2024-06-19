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
        val updateEmployee = intent.getBooleanExtra("EMPLOYEEUPDATED",false)
        val fromChooseEmployee = intent.getBooleanExtra("FROM-CHOOSE-EMPLOYEE",false)
        if (updateEmployee) {
            if (employee != null) setupData(employee, updateEmployee,fromChooseEmployee)
        }
        if (employee != null) setupData(employee, updateEmployee,fromChooseEmployee)
        setupTobBar(updateEmployee,fromChooseEmployee)
    }

    private fun setupData(employee: ListEmployeeItem, updateEmployee:Boolean, fromChooseEmployee:Boolean) {
        val id = employee.id.toString()
        viewModel.showEmployee(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val employeeData = result.data.employee
                    if (updateEmployee) {
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
                    if(employeeData !=null) setupView(employeeData,fromChooseEmployee)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(employee: Employee, fromChooseEmployee:Boolean) {
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
                editEmployee(employeeData,fromChooseEmployee)
            }
            btnDeleteEmployee.setOnClickListener {
                deleteEmployee(employee.id.toString(),fromChooseEmployee)
            }
        }
    }

    private fun editEmployee(data: ListEmployeeItem, fromChooseEmployee:Boolean) {
        if (fromChooseEmployee) {
            val intentToDetail = Intent(this@ShowEmployeeActivity, EditEmployeeActivity::class.java)
            intentToDetail.putExtra("EMPLOYEE", data)
            intentToDetail.putExtra("FROM-CHOOSE-EMPLOYEE",true)
            startActivity(intentToDetail)
        } else {
            val intentToDetail = Intent(this@ShowEmployeeActivity, EditEmployeeActivity::class.java)
            intentToDetail.putExtra("EMPLOYEE", data)
            intentToDetail.putExtra("FROM-CHOOSE-EMPLOYEE",false)
            startActivity(intentToDetail)
        }
    }

    private fun deleteEmployee(id: String, fromChooseEmployee:Boolean) {
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
                        navigateToChooseEmployee()
                    } else {
                        navigateToIndexEmployee()
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

    private fun navigateToIndexEmployee() {
        val intentToIndex = Intent(this@ShowEmployeeActivity, IndexEmployeeActivity::class.java)
        intentToIndex.putExtra("EMPLOYEEUPDATED", true)
        startActivity(intentToIndex)
        finish()
    }
    private fun navigateToChooseEmployee() {
        val intentToChooseEmployee = Intent(this@ShowEmployeeActivity, ChooseEmployeeActivity::class.java)
        intentToChooseEmployee.putExtra("EMPLOYEEUPDATED", true)
        startActivity(intentToChooseEmployee)
        finish()
    }

    private fun setupTobBar(updateEmployee: Boolean, fromChooseEmployee:Boolean) {
        binding.btnBack.setOnClickListener {
            if (updateEmployee) {
                if (fromChooseEmployee){
                    navigateToChooseEmployee()
                } else {
                    navigateToIndexEmployee()
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
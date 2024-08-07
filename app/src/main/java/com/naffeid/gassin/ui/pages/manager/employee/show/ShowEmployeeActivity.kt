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
        val quantity = intent.getStringExtra("QUANTITY")
        val employee = intent.getParcelableExtra<ListEmployeeItem>("EMPLOYEE")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromEditResupply = intent.getBooleanExtra("FROM-EDIT-RESUPPLY",false)
        val fromChooseEmployee = intent.getBooleanExtra("FROM-CHOOSE-EMPLOYEE",false)
        val fromIndexEmployee = intent.getBooleanExtra("FROM-INDEX-EMPLOYEE",false)
        val fromEditEmployee = intent.getBooleanExtra("FROM-EDIT-EMPLOYEE",false)

        if (fromEditEmployee) {
            if (employee != null) setupData(quantity.toString(), employee, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee, true)
        }
        if (employee != null) setupData(quantity.toString(), employee, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee, fromEditEmployee)
        setupTopBar(quantity.toString(), fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
    }

    private fun setupData(qty: String, employee: ListEmployeeItem, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean, fromEditEmployee:Boolean) {
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
                    if(employeeData !=null) setupView(qty, employeeData, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(qty: String, employee: Employee, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean) {
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
                editEmployee(qty, employeeData, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
            }
            btnDeleteEmployee.setOnClickListener {
                deleteEmployee(qty, employee.id.toString(), fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee)
            }
        }
    }

    private fun editEmployee(qty: String, data: ListEmployeeItem, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromChooseEmployee:Boolean, fromIndexEmployee: Boolean) {
        val intentToEdit = Intent(this@ShowEmployeeActivity, EditEmployeeActivity::class.java)
        intentToEdit.putExtra("EMPLOYEE", data)
        intentToEdit.putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
        intentToEdit.putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
        intentToEdit.putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
        intentToEdit.putExtra("FROM-EDIT-RESUPPLY", fromEditResupply)
        intentToEdit.putExtra("FROM-CHOOSE-EMPLOYEE",fromChooseEmployee)
        intentToEdit.putExtra("FROM-INDEX-EMPLOYEE",fromIndexEmployee)
        intentToEdit.putExtra("QUANTITY", qty)
        startActivity(intentToEdit)
    }

    private fun deleteEmployee(qty: String, id: String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromChooseEmployee:Boolean) {
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
                        navigateToChooseEmployee(qty, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, true)
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
    private fun navigateToChooseEmployee(qty:String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromEditEmployee:Boolean) {
        val intentToChoose = Intent(this@ShowEmployeeActivity, ChooseEmployeeActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
        intentToChoose.putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
        intentToChoose.putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
        intentToChoose.putExtra("FROM-EDIT-RESUPPLY", fromEditResupply)
        intentToChoose.putExtra("FROM-EDIT-EMPLOYEE",fromEditEmployee)
        intentToChoose.putExtra("QUANTITY", qty)
        startActivity(intentToChoose)
        finish()
    }

    private fun setupTopBar(qty:String, fromCreatePurchase: Boolean, fromEditPurchase: Boolean, fromCreateResupply: Boolean, fromEditResupply: Boolean, fromChooseEmployee: Boolean, fromIndexEmployee: Boolean) {
        if (fromChooseEmployee && fromIndexEmployee) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromChooseEmployee -> {
                    Intent(this@ShowEmployeeActivity, ChooseEmployeeActivity::class.java).apply {
                        putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
                        putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
                        putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
                        putExtra("FROM-EDIT-RESUPPLY", fromEditResupply)
                        putExtra("QUANTITY", qty)
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
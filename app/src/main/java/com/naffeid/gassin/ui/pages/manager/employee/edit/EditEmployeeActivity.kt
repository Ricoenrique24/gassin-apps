package com.naffeid.gassin.ui.pages.manager.employee.edit

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListEmployeeItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityEditEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.employee.show.ShowEmployeeActivity

class EditEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEmployeeBinding
    private val viewModel by viewModels<EditEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val employee = intent.getParcelableExtra<ListEmployeeItem>("EMPLOYEE")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromEditResupply = intent.getBooleanExtra("FROM-EDIT-RESUPPLY",false)
        val fromChooseEmployee = intent.getBooleanExtra("FROM-CHOOSE-EMPLOYEE",false)
        val fromIndexEmployee = intent.getBooleanExtra("FROM-INDEX-EMPLOYEE",false)
        if (employee != null) {
            setupView(employee, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
            setupTopBar(employee, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
        }
    }

    private fun setupView(employee: ListEmployeeItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        with(binding) {
            edEmployeeName.setText(employee.name)
            edEmployeeUsername.setText(employee.username)
            edEmployeeEmail.setText(employee.email)
            edEmployeePhone.setText(employee.phone)
            cbChangePassword.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.edPasswordLayout.visibility = View.VISIBLE
                    binding.edPasswordConfirmLayout.visibility = View.VISIBLE
                } else {
                    binding.edPasswordLayout.visibility = View.GONE
                    binding.edPasswordConfirmLayout.visibility = View.GONE
                }
            }
            btnUpdateEmployee.setOnClickListener {
                validate(employee.id.toString(),fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
            }
        }
    }

    private fun validate(id: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        val name = binding.edEmployeeName.text.toString()
        val username = binding.edEmployeeUsername.text.toString()
        val email = binding.edEmployeeEmail.text.toString()
        val phone = binding.edEmployeePhone.text.toString()

        // Periksa apakah password fields harus divalidasi
        if (binding.cbChangePassword.isChecked) {
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edPasswordConfirm.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(phone)) {
                if (password.length >= 8 && confirmPassword.length >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (confirmPassword == password) {
                        updateEmployee(id, name, username, email, password, phone, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
                    } else {
                        showAlert(getString(R.string.konfirmasi_kata_sandi_tidak_sama_dengan_password))
                    }
                } else {
                    showAlert(getString(R.string.password_less_characters))
                }
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        } else {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone)) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    updateEmployee(id, name, username, email, null, phone, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)  // Password null jika tidak diubah
                } else {
                    showAlert(getString(R.string.format_email_tidak_valid))
                }
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun updateEmployee(id:String, name: String, username: String, email: String, password: String?, phone: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        viewModel.updateEmployee(id, name, username, email, password, phone).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(it.error)
                        Log.e("error search employee:", it.error.toString())
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(getString(R.string.agen_berhasil_diupdate))
                        val employee = it.data.employee
                        val employeeData = ListEmployeeItem(
                            id = employee?.id,
                            name = employee?.name,
                            username = employee?.username,
                            email = employee?.email,
                            phone = employee?.phone
                        )
                        navigateToShowEmployee(employeeData, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
                    }
                }
            }
        }
    }

    private fun navigateToShowEmployee(data: ListEmployeeItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        val intentToShow = Intent(this@EditEmployeeActivity, ShowEmployeeActivity::class.java)
        intentToShow.putExtra("EMPLOYEE", data)
        intentToShow.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToShow.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToShow.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToShow.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
        intentToShow.putExtra("FROM-CHOOSE-EMPLOYEE",fromChooseEmployee)
        intentToShow.putExtra("FROM-INDEX-EMPLOYEE",fromIndexEmployee)
        intentToShow.putExtra("FROM-EDIT-EMPLOYEE",true)
        startActivity(intentToShow)
        finish()
    }

    private fun setupTopBar(data: ListEmployeeItem, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        binding.btnBack.setOnClickListener {
            val intentToShow = Intent(this@EditEmployeeActivity, ShowEmployeeActivity::class.java)
            intentToShow.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
            intentToShow.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
            intentToShow.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
            intentToShow.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
            intentToShow.putExtra("FROM-CHOOSE-EMPLOYEE",fromChooseEmployee)
            intentToShow.putExtra("FROM-INDEX-EMPLOYEE",fromIndexEmployee)
            intentToShow.putExtra("FROM-EDIT-EMPLOYEE",true)
            intentToShow.putExtra("EMPLOYEE", data)
            startActivity(intentToShow)
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
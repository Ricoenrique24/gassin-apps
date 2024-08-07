package com.naffeid.gassin.ui.pages.manager.employee.create

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
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityCreateEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.employee.ChooseEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity

class CreateEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEmployeeBinding
    private val viewModel by viewModels<CreateEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val quantity = intent.getStringExtra("QUANTITY")
        val fromCreatePurchase = intent.getBooleanExtra("FROM-CREATE-PURCHASE",false)
        val fromEditPurchase = intent.getBooleanExtra("FROM-EDIT-PURCHASE",false)
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromEditResupply = intent.getBooleanExtra("FROM-EDIT-RESUPPLY",false)
        val fromChooseEmployee = intent.getBooleanExtra("FROM-CHOOSE-EMPLOYEE",false)
        val fromIndexEmployee = intent.getBooleanExtra("FROM-INDEX-EMPLOYEE",false)

        validate(quantity.toString(), fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
        setupTopBar(quantity.toString(), fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
    }

    private fun validate(qty: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        binding.btnCreateEmployee.setOnClickListener {
            val name = binding.edEmployeeName.text.toString()
            val username = binding.edEmployeeUsername.text.toString()
            val email = binding.edEmployeeEmail.text.toString()
            val phone = binding.edEmployeePhone.text.toString()
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edPasswordConfirm.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(phone)) {
                if (password.length >= 8 && confirmPassword.length >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (confirmPassword == password){
                        createNewEmployee(qty, name, username, email, password, phone, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply, fromChooseEmployee, fromIndexEmployee)
                    } else {
                        showAlert(getString(R.string.konfirmasi_kata_sandi_tidak_sama_dengan_password))
                    }
                } else {
                    showAlert(getString(R.string.password_less_characters))
                }
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun createNewEmployee(qty: String, name: String, username: String, email: String, password: String, phone: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee:Boolean, fromIndexEmployee:Boolean) {
        viewModel.createNewEmployee(name, username, email, password, phone).observe(this) {
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
                        showAlert(getString(R.string.login_success))
                        when {
                            fromChooseEmployee -> navigateToChooseEmployee(qty, fromCreatePurchase, fromEditPurchase, fromCreateResupply, fromEditResupply)
                            fromIndexEmployee -> navigateToIndexEmployee()
                            else -> {
                                showAlert("Halaman Tidak Dapat Ditemukan")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToChooseEmployee(qty: String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        val intentToChoose = Intent(this@CreateEmployeeActivity, ChooseEmployeeActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-PURCHASE",fromCreatePurchase)
        intentToChoose.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToChoose.putExtra("FROM-EDIT-PURCHASE",fromEditPurchase)
        intentToChoose.putExtra("FROM-EDIT-RESUPPLY", fromEditResupply)
        intentToChoose.putExtra("FROM-CREATE-EMPLOYEE",true)
        intentToChoose.putExtra("QUANTITY", qty)
        startActivity(intentToChoose)
        finish()
    }
    private fun navigateToIndexEmployee() {
        val intentToIndex = Intent(this@CreateEmployeeActivity, IndexEmployeeActivity::class.java)
        intentToIndex.putExtra("FROM-CREATE-EMPLOYEE", true)
        startActivity(intentToIndex)
        finish()
    }

    private fun setupTopBar(qty:String, fromCreatePurchase:Boolean, fromEditPurchase:Boolean, fromCreateResupply:Boolean, fromEditResupply:Boolean, fromChooseEmployee: Boolean, fromIndexEmployee: Boolean) {
        if (fromChooseEmployee && fromIndexEmployee) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromChooseEmployee -> {
                    Intent(this@CreateEmployeeActivity, ChooseEmployeeActivity::class.java).apply {
                        putExtra("FROM-CREATE-PURCHASE", fromCreatePurchase)
                        putExtra("FROM-EDIT-PURCHASE", fromEditPurchase)
                        putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
                        putExtra("FROM-EDIT-RESUPPLY", fromEditResupply)
                        putExtra("QUANTITY", qty)
                    }
                }
                fromIndexEmployee -> {
                    Intent(this@CreateEmployeeActivity, IndexEmployeeActivity::class.java)
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
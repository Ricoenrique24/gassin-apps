package com.naffeid.gassin.ui.pages.employee.user.edit

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
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityEditUserBinding
import com.naffeid.gassin.databinding.ActivityEditUserEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.user.show.ShowUserEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.user.show.ShowUserActivity

class EditUserEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditUserEmployeeBinding
    private val viewModel by viewModels<EditUserEmployeeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupView()
        setupTobBar()
    }
    private fun setupView() {
        viewModel.getSession().observe(this) { user->
            if (user != null) {
                with(binding) {
                    edUserName.setText(user.name)
                    edUserUsername.setText(user.username)
                    edUserEmail.setText(user.email)
                    edUserPhone.setText(user.phone)
                    cbChangePassword.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            binding.edPasswordLayout.visibility = View.VISIBLE
                            binding.edPasswordConfirmLayout.visibility = View.VISIBLE
                        } else {
                            binding.edPasswordLayout.visibility = View.GONE
                            binding.edPasswordConfirmLayout.visibility = View.GONE
                        }
                    }
                    btnUpdateUser.setOnClickListener {
                        validate(user.id.toString())
                    }
                }
            }
        }
    }

    private fun validate(id: String) {
        val name = binding.edUserName.text.toString()
        val username = binding.edUserUsername.text.toString()
        val email = binding.edUserEmail.text.toString()
        val phone = binding.edUserPhone.text.toString()

        // Periksa apakah password fields harus divalidasi
        if (binding.cbChangePassword.isChecked) {
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edPasswordConfirm.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(phone)) {
                if (password.length >= 8 && confirmPassword.length >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (confirmPassword == password) {
                        updateUser(id, name, username, email, password, phone)
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
                    updateUser(id, name, username, email, null, phone)  // Password null jika tidak diubah
                } else {
                    showAlert(getString(R.string.format_email_tidak_valid))
                }
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun updateUser(id:String, name: String, username: String, email: String, password: String?, phone: String) {
        viewModel.updateUser(id, name, username, email, password, phone).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(it.error)
                        Log.e("error search user:", it.error.toString())
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(getString(R.string.user_berhasil_di_update))
                        val data = it.data.loginResult
                        if (data != null) {
                            val user = User(
                                id = data.id!!,
                                name = data.name.toString(),
                                username = data.username.toString(),
                                email = data.email.toString(),
                                phone = data.phone.toString(),
                                role = data.role.toString(),
                                apikey = data.apikey.toString(),
                                tokenfcm = data.tokenFcm.toString()
                            )
                            saveSession(user)
                            navigateToShowUser()
                        }
                    }
                }
            }
        }
    }

    private fun saveSession(user: User) {
        viewModel.saveSession(user)
    }
    private fun navigateToShowUser() {
        val intentToShow = Intent(this@EditUserEmployeeActivity, ShowUserEmployeeActivity::class.java)
        intentToShow.putExtra("USER-UPDATED", true)
        startActivity(intentToShow)
        finish()
    }
    private fun setupTobBar() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
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
package com.naffeid.gassin.ui.pages.signin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivitySignInBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.main.EmployeeMainActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel by viewModels<SignInViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        validate()
    }

    private fun validate() {
        binding.signinBtn.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if (password.length >= 8 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    login(email, password)
                } else {
                    showAlert(getString(R.string.password_less_characters))
                }
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(getString(R.string.login_error))
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(getString(R.string.login_success))
                        val data = it.data.data
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
                        }

                        startActivity(Intent(this, ManagerMainActivity::class.java))

                        finish()
                    }
                }
            }
        }
    }

    
    private fun saveSession(user: User) {
        viewModel.saveSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMainScreen(role: String) {
        viewModel.checkUserRole(role) { isRoleMatch ->
            if (isRoleMatch) {
                val intent = when (role) {
                    "employee" -> Intent(this@SignInActivity, EmployeeMainActivity::class.java)
                    "manager" -> Intent(this@SignInActivity, EmployeeMainActivity::class.java)
                    else -> Intent(this@SignInActivity, SignInActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}
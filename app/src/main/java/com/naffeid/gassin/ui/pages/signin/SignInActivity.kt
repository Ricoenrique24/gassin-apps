package com.naffeid.gassin.ui.pages.signin

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
//                        saveSession()
//                        startActivity(Intent(this, MainActivity::class.java))

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
}
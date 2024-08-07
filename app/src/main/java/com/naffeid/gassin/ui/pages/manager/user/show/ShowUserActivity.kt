package com.naffeid.gassin.ui.pages.manager.user.show

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityShowUserBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.user.edit.EditUserActivity

class ShowUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowUserBinding
    private val viewModel by viewModels<ShowUserViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val updateUser = intent.getBooleanExtra("USER-UPDATED",false)
        if (updateUser) {
            setupData()
        }
        setupData()
        setupTobBar()
    }

    private fun setupData() {
        viewModel.getSession().observe(this) {user->
            if (user != null) {
                viewModel.showUser(user.id.toString()).observe(this) {
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
                                val data = it.data.loginResult
                                if (data != null) {
                                    val userData = User(
                                        id = data.id!!,
                                        name = data.name.toString(),
                                        username = data.username.toString(),
                                        email = data.email.toString(),
                                        phone = data.phone.toString(),
                                        role = data.role.toString(),
                                        apikey = data.apikey.toString(),
                                        tokenfcm = data.tokenFcm.toString()
                                    )
                                    if (userData == user) {
                                        setupView(user)
                                    } else {
                                        viewModel.logout()
                                        saveSession(userData)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView(user: User) {
        with(binding) {
            tvUsername.text = user.username
            tvRole.text = user.role
            edUserName.setText(user.name)
            edUserEmail.setText(user.email)
            edUserPhone.setText(user.phone)
            btnEditProfil.setOnClickListener {
                val intent = Intent(this@ShowUserActivity, EditUserActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveSession(user: User) {
        viewModel.saveSession(user)
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
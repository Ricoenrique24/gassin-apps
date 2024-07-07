package com.naffeid.gassin.ui.pages.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivitySplashBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.main.EmployeeMainActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, SPLASH_DELAY)
    }

    private fun checkSession() {
        viewModel.getSession().observe(this, Observer { user ->
            if (user != null) {
                viewModel.showUser(user.id.toString()).observe(this, Observer {
                    when (it) {
                        is Result.Loading -> {
                            Log.d("Get User Data Process:", "Loading ...")
                        }

                        is Result.Error -> {
                            Log.d("Get User Data Process:", "Error: ${it.error}")
                            navigateToSignInScreen()
                        }

                        is Result.Success -> {
                            val data = it.data.loginResult
                            if (data != null) {
                                val userData = User(
                                    id = data.id!!,
                                    name = data.name ?: "",
                                    username = data.username ?: "",
                                    email = data.email ?: "",
                                    phone = data.phone ?: "",
                                    role = data.role ?: "",
                                    apikey = data.apikey ?: "",
                                    tokenfcm = data.tokenFcm ?: ""
                                )
                                if (userData == user) {
                                    checkRole(userData.role)
                                } else {
                                    viewModel.logout()
                                    navigateToSignInScreen()
                                }
                            } else {
                                viewModel.logout()
                                navigateToSignInScreen()
                            }
                        }
                    }
                })
            } else {
                navigateToSignInScreen()
            }
        })
    }

    private fun checkRole(role: String) {
        viewModel.checkUserRole(role) { isRoleMatch ->
            if (isRoleMatch) {
                val intent = when (role) {
                    "employee" -> Intent(this@SplashActivity, EmployeeMainActivity::class.java)
                    "manager" -> Intent(this@SplashActivity, ManagerMainActivity::class.java)
                    else -> Intent(this@SplashActivity, SignInActivity::class.java)
                }
                startActivity(intent)
                finish()
            } else {
                navigateToSignInScreen()
            }
        }
    }

    private fun navigateToSignInScreen() {
        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
    }
}
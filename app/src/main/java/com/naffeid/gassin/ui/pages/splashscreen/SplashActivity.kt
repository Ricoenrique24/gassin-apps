package com.naffeid.gassin.ui.pages.splashscreen

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
//                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            viewModel.getSession().observe(this) { user ->
                if (user.apikey.isNotEmpty()) {
                    navigateToMainScreen(user.role)
                    Log.d("ini role user login : ", user.role)
                } else {
                    navigateToSignInScreen()
                }
            }
//            navigateToMainScreenTest()
        }, SPLASH_DELAY)
    }

    private fun navigateToMainScreen(role: String) {
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

    private fun navigateToMainScreenTest() {
        val intent = Intent(this@SplashActivity, ManagerMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignInScreen() {
        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
    }
}
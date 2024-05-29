package com.naffeid.gassin.ui.pages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.CustomerRepository
import com.naffeid.gassin.data.repository.EmployeeRepository
import com.naffeid.gassin.data.repository.StoreRepository
import com.naffeid.gassin.data.repository.UserRepository
import com.naffeid.gassin.di.Injection
import com.naffeid.gassin.ui.pages.signin.SignInViewModel
import com.naffeid.gassin.ui.pages.splashscreen.SplashViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val storeRepository: StoreRepository,
    private val customerRepository: CustomerRepository,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(userRepository,authRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideEmployeeRepository(context),
                    Injection.provideStoreRepository(context),
                    Injection.provideCustomerRepository(context),
                )
            }.also { instance = it }
    }
}

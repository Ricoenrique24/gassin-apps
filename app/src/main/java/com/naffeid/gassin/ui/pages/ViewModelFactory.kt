package com.naffeid.gassin.ui.pages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.UserRepository
import com.naffeid.gassin.di.Injection
import com.naffeid.gassin.ui.pages.signin.SignInViewModel
import com.naffeid.gassin.ui.pages.splashscreen.SplashViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
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
                )
            }.also { instance = it }
    }
}

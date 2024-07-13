package com.naffeid.gassin.ui.pages.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class SignInViewModel(private val userRepository: UserRepository,private val authRepository: AuthRepository) : ViewModel() {
    fun login(email: String, password: String, fcmToken:String) = authRepository.login(email, password, fcmToken)
    fun saveSession(user: User) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
    fun checkUserRole(role: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isRoleMatch = userRepository.checkUserRole(role)
            callback(isRoleMatch)
        }
    }
}
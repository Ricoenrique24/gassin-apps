package com.naffeid.gassin.ui.pages.employee.user.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class EditUserEmployeeViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun saveSession(user: User) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
    fun updateUser(id:String, name: String, username: String, email: String, password: String?, phone: String) = authRepository.updateUser(id, name, username, email, password, phone)
}
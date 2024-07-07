package com.naffeid.gassin.ui.pages.employee.main.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class MoreEmployeeViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun checkUserRole(role: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isRoleMatch = userRepository.checkUserRole(role)
            callback(isRoleMatch)
        }
    }
    fun showUser(id:String) = authRepository.showUser(id)
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
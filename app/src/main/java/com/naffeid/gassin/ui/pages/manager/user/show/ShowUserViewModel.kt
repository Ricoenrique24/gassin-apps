package com.naffeid.gassin.ui.pages.manager.user.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.AuthRepository
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class ShowUserViewModel(
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
    fun showUser(id:String) = authRepository.showUser(id)
}
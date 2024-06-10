package com.naffeid.gassin.ui.pages.manager.main.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.UserRepository
import kotlinx.coroutines.launch

class MoreViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
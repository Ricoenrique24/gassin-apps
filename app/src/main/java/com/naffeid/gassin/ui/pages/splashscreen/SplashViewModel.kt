package com.naffeid.gassin.ui.pages.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.repository.UserRepository

class SplashViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return userRepository.getSession().asLiveData()
    }
}
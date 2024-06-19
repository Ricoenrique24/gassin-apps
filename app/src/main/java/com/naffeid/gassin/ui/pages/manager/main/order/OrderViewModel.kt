package com.naffeid.gassin.ui.pages.manager.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is purchasetransaction Fragment"
    }
    val text: LiveData<String> = _text
}
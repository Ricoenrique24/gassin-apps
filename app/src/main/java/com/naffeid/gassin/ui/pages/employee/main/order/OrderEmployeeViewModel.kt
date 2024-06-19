package com.naffeid.gassin.ui.pages.employee.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderEmployeeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is purchasetransaction Fragment"
    }
    val text: LiveData<String> = _text
}
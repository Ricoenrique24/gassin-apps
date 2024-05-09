package com.naffeid.gassin.ui.pages.manager.main.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StockViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is stock Fragment"
    }
    val text: LiveData<String> = _text
}
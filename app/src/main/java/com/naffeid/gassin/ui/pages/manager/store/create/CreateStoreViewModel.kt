package com.naffeid.gassin.ui.pages.manager.store.create

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.StoreRepository

class CreateStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun createNewStore(name: String, phone: String, address: String, linkMap: String, price: String) = storeRepository.createNewStore(name, phone, address, linkMap, price)
}
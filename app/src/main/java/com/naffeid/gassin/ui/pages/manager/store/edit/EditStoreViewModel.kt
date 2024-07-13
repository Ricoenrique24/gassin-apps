package com.naffeid.gassin.ui.pages.manager.store.edit

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.StoreRepository

class EditStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun showStore(id: String) = storeRepository.showStore(id)
    fun updateStore(id: String, name: String, phone: String, address: String, linkMap: String, price: String) = storeRepository.updateStore(id, name, phone, address, linkMap, price)
}
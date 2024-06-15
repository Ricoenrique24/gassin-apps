package com.naffeid.gassin.ui.pages.manager.store.show

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.StoreRepository

class ShowStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun showStore(id: String) = storeRepository.showStore(id)
    fun deleteStore(id: String) = storeRepository.deleteStore(id)
}
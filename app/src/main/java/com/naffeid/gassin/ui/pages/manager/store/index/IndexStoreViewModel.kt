package com.naffeid.gassin.ui.pages.manager.store.index

import androidx.lifecycle.ViewModel
import com.naffeid.gassin.data.repository.StoreRepository

class IndexStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun showAllStore() = storeRepository.showAllStore()
    fun searchStore(query: String) = storeRepository.searchStore(query)
}
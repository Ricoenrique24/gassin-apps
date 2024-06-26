package com.naffeid.gassin.ui.pages.manager.choose.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.repository.StoreRepository
import kotlinx.coroutines.launch

class ChooseStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun showAllStore() = storeRepository.showAllStore()
    fun searchStore(query: String) = storeRepository.searchStore(query)

    fun getStore(): LiveData<Store> {
        return storeRepository.getStore().asLiveData()
    }

    fun saveStore(store: Store) {
        viewModelScope.launch {
            storeRepository.saveStore(store)
        }
    }
    fun deleteStore(){
        viewModelScope.launch {
            storeRepository.deleteStore()
        }
    }
}
package com.naffeid.gassin.ui.pages.manager.store.show

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.repository.StoreRepository
import kotlinx.coroutines.launch

class ShowStoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    fun showStore(id: String) = storeRepository.showStore(id)
    fun deleteStore(id: String) = storeRepository.deleteStore(id)
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
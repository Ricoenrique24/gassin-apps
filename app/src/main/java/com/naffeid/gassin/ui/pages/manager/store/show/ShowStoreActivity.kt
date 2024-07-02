package com.naffeid.gassin.ui.pages.manager.store.show

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListStoreItem
import com.naffeid.gassin.data.remote.response.Store
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityShowStoreBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.store.ChooseStoreActivity
import com.naffeid.gassin.ui.pages.manager.store.edit.EditStoreActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity

class ShowStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowStoreBinding
    private val viewModel by viewModels<ShowStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val store = intent.getParcelableExtra<ListStoreItem>("STORE")
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromChooseStore = intent.getBooleanExtra("FROM-CHOOSE-STORE",false)
        val fromIndexStore = intent.getBooleanExtra("FROM-INDEX-STORE",false)
        val fromEditStore = intent.getBooleanExtra("FROM-EDIT-STORE",false)

        if (fromEditStore) {
            if (store != null) setupData(store, fromCreateResupply, fromChooseStore, fromIndexStore, true)
        }
        if (store != null) setupData(store, fromCreateResupply, fromChooseStore, fromIndexStore, fromEditStore)
        setupTopBar(fromCreateResupply, fromChooseStore, fromIndexStore)
    }

    private fun setupData(store: ListStoreItem, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore: Boolean, fromEditStore:Boolean) {
        val id = store.id.toString()
        viewModel.showStore(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val storeData = result.data.store
                    if (fromEditStore) {
                        viewModel.getStore().observe(this) { store ->
                            if (store.isNotEmpty()) {
                                val idStore = store.id.toString()
                                if (id == idStore) {
                                    viewModel.deleteStore()
                                    val dataStore = com.naffeid.gassin.data.model.Store(
                                        id = storeData?.id ?: 0,
                                        name = storeData?.name ?: "",
                                        linkMap = storeData?.linkMap ?: "",
                                        address = storeData?.address ?: "",
                                        phone = storeData?.phone ?: "",
                                        price = storeData?.price ?: ""
                                    )
                                    viewModel.saveStore(dataStore)
                                }
                            }
                        }
                    }
                    if(storeData !=null) setupView(storeData,fromCreateResupply, fromChooseStore, fromIndexStore)

                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(store: Store, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore: Boolean) {
        with(binding){
            edStoreName.setText(store.name)
            edStoreLinkMap.setText(store.linkMap)
            edStoreAddress.setText(store.address)
            edStorePhone.setText(store.phone)
            edStorePrice.setText(store.price)
            btnEditStore.setOnClickListener {
                val storeData = ListStoreItem(
                    id = store.id,
                    name = store.name,
                    linkMap = store.linkMap,
                    address = store.address,
                    phone = store.phone,
                    price = store.price
                )
                editStore(storeData,fromCreateResupply, fromChooseStore, fromIndexStore)
            }
            btnDeleteStore.setOnClickListener {
                deleteStore(store.id.toString(), fromCreateResupply, fromChooseStore)
            }
        }
    }

    private fun editStore(data: ListStoreItem, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore: Boolean) {
        val intentToEdit = Intent(this@ShowStoreActivity, EditStoreActivity::class.java)
        intentToEdit.putExtra("STORE", data)
        intentToEdit.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToEdit.putExtra("FROM-CHOOSE-STORE",fromChooseStore)
        intentToEdit.putExtra("FROM-INDEX-STORE",fromIndexStore)
        startActivity(intentToEdit)
    }

    private fun deleteStore(id: String,fromCreateResupply:Boolean, fromChooseStore:Boolean) {
        viewModel.deleteStore(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(getString(R.string.agen_berhasil_dihapus))
                    viewModel.getStore().observe(this) { store ->
                        if (store.isNotEmpty()) {
                            val idStore = store.id.toString()
                            if (id == idStore) {
                                viewModel.deleteStore()
                            }
                        }
                    }
                    if (fromChooseStore){
                        navigateToChooseStore(fromCreateResupply, true)
                    } else {
                        navigateToIndexStore(true)
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error delete store:", result.error.toString())
                }
            }
        }
    }
    private fun com.naffeid.gassin.data.model.Store.isNotEmpty(): Boolean {
        return this != com.naffeid.gassin.data.model.Store(0, "", "", "", "", "")
    }

    private fun navigateToIndexStore(fromEditStore:Boolean) {
        val intentToIndex = Intent(this@ShowStoreActivity, IndexStoreActivity::class.java)
        intentToIndex.putExtra("FROM-EDIT-STORE", fromEditStore)
        startActivity(intentToIndex)
        finish()
    }
    private fun navigateToChooseStore(fromCreateResupply:Boolean, fromEditStore:Boolean) {
        val intentToChoose = Intent(this@ShowStoreActivity, ChooseStoreActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToChoose.putExtra("FROM-EDIT-STORE",fromEditStore)
        startActivity(intentToChoose)
        finish()
    }

    private fun setupTopBar(fromCreateResupply: Boolean, fromChooseStore: Boolean, fromIndexStore: Boolean) {
        if (fromChooseStore && fromIndexStore) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromChooseStore -> {
                    Intent(this@ShowStoreActivity, ChooseStoreActivity::class.java).apply {
                        putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
                    }
                }
                fromIndexStore -> {
                    Intent(this@ShowStoreActivity, IndexStoreActivity::class.java)
                }
                else -> {
                    showAlert("Halaman Tidak Dapat Ditemukan")
                    return@setOnClickListener
                }
            }
            startActivity(intentToHome)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }
}
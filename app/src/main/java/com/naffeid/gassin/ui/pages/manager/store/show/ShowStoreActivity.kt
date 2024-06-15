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
import com.naffeid.gassin.ui.pages.manager.store.edit.EditStoreActivity

class ShowStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowStoreBinding
    private val viewModel by viewModels<ShowStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupTobBar()
        val store = intent.getParcelableExtra<ListStoreItem>("STORE")
        if (store != null) setupData(store)
    }

    private fun setupData(store: ListStoreItem) {
        val id = store.id.toString()
        viewModel.showStore(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val storeData = result.data.store
                    if(storeData !=null) setupView(storeData)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }
    private fun setupView(store: Store) {
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
                editStore(storeData)
            }
            btnDeleteStore.setOnClickListener {
                deleteStore(store.id.toString())
            }
        }
    }

    private fun editStore(data: ListStoreItem) {
        val intentToDetail = Intent(this@ShowStoreActivity, EditStoreActivity::class.java)
        intentToDetail.putExtra("STORE", data)
        startActivity(intentToDetail)
    }

    private fun deleteStore(id: String) {
        viewModel.deleteStore(id).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showAlert(getString(R.string.agen_berhasil_dihapus))
                    onBackPressed()
                    finish()
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error delete store:", result.error.toString())
                }
            }
        }
    }

    private fun setupTobBar() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
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
package com.naffeid.gassin.ui.pages.manager.store.edit

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListStoreItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityEditStoreBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.store.show.ShowStoreActivity

class EditStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditStoreBinding
    private val viewModel by viewModels<EditStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val store = intent.getParcelableExtra<ListStoreItem>("STORE")
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromChooseStore = intent.getBooleanExtra("FROM-CHOOSE-STORE",false)
        val fromIndexStore = intent.getBooleanExtra("FROM-INDEX-STORE",false)
        if (store != null) {
            setupView(store, fromCreateResupply, fromChooseStore, fromIndexStore)
            setupTopBar(store,fromCreateResupply, fromChooseStore, fromIndexStore)
        }
    }

    private fun setupView(store: ListStoreItem, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        with(binding) {
            edStoreName.setText(store.name)
            edStoreLinkMap.setText(store.linkMap)
            edStoreAddress.setText(store.address)
            edStorePhone.setText(store.phone)
            edStorePrice.setText(store.price)
            btnUpdateStore.setOnClickListener {
                validate(store.id.toString(), fromCreateResupply, fromChooseStore, fromIndexStore)
            }
        }
    }

    private fun validate(id: String, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        val name = binding.edStoreName.text.toString()
        val linkMap = binding.edStoreLinkMap.text.toString()
        val address = binding.edStoreAddress.text.toString()
        val phone = binding.edStorePhone.text.toString()
        val price = binding.edStorePrice.text.toString()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
            updateStore(id, name, phone, address, linkMap, price, fromCreateResupply, fromChooseStore, fromIndexStore)
        } else {
            showAlert(getString(R.string.please_fill_in_all_input))
        }
    }

    private fun updateStore(id:String,name: String, phone: String, address: String, linkMap: String, price: String, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        viewModel.updateStore(id, name, phone, address, linkMap, price).observe(this) {
            if (it != null) {
                when (it) {
                    Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showAlert(it.error)
                        Log.e("error search store:", it.error.toString())
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showAlert(getString(R.string.agen_berhasil_diupdate))
                        val store = it.data.store
                        val storeData = ListStoreItem(
                            id = store?.id,
                            name = store?.name,
                            linkMap = store?.linkMap,
                            address = store?.address,
                            phone = store?.phone,
                            price = store?.price
                        )
                        navigateToShowStore(storeData,fromCreateResupply, fromChooseStore, fromIndexStore)
                    }
                }
            }
        }
    }

    private fun navigateToShowStore(data: ListStoreItem, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        val intentToShow = Intent(this@EditStoreActivity, ShowStoreActivity::class.java)
        intentToShow.putExtra("STORE", data)
        intentToShow.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToShow.putExtra("FROM-CHOOSE-STORE",fromChooseStore)
        intentToShow.putExtra("FROM-INDEX-STORE",fromIndexStore)
        intentToShow.putExtra("FROM-EDIT-STORE",true)
        startActivity(intentToShow)
        finish()
    }

    private fun setupTopBar(data: ListStoreItem, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        binding.btnBack.setOnClickListener {
            val intentToShow = Intent(this@EditStoreActivity, ShowStoreActivity::class.java)
            intentToShow.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
            intentToShow.putExtra("FROM-CHOOSE-STORE",fromChooseStore)
            intentToShow.putExtra("FROM-INDEX-STORE",fromIndexStore)
            intentToShow.putExtra("FROM-EDIT-STORE",true)
            intentToShow.putExtra("STORE", data)
            startActivity(intentToShow)
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
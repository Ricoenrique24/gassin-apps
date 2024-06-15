package com.naffeid.gassin.ui.pages.manager.store.create

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityCreateStoreBinding
import com.naffeid.gassin.ui.adapter.StoreAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory

class CreateStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoreBinding
    private val viewModel by viewModels<CreateStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var storeAdapter: StoreAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupTobBar()
        validate()
    }

    private fun validate() {
        binding.btnCreateStore.setOnClickListener {
            val name = binding.edStoreName.text.toString()
            val linkMap = binding.edStoreLinkMap.text.toString()
            val address = binding.edStoreAddress.text.toString()
            val phone = binding.edStorePhone.text.toString()
            val price = binding.edStorePrice.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
                createStore(name, phone, address, linkMap, price)
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun createStore(name: String, phone: String, address: String, linkMap: String, price: String) {
        viewModel.createNewStore(name, phone, address, linkMap, price).observe(this) {
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
                        showAlert(getString(R.string.login_success))
                        onBackPressed()
                        finish()
                    }
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
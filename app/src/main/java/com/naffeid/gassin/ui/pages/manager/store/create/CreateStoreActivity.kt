package com.naffeid.gassin.ui.pages.manager.store.create

import android.content.Intent
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
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.choose.store.ChooseStoreActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity

class CreateStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoreBinding
    private val viewModel by viewModels<CreateStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromChooseStore = intent.getBooleanExtra("FROM-CHOOSE-STORE",false)
        val fromIndexStore = intent.getBooleanExtra("FROM-INDEX-STORE",false)

        validate(fromCreateResupply, fromChooseStore, fromIndexStore)
        setupTopBar(fromCreateResupply, fromChooseStore, fromIndexStore)
    }

    private fun validate(fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
        binding.btnCreateStore.setOnClickListener {
            val name = binding.edStoreName.text.toString()
            val linkMap = binding.edStoreLinkMap.text.toString()
            val address = binding.edStoreAddress.text.toString()
            val phone = binding.edStorePhone.text.toString()
            val price = binding.edStorePrice.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(linkMap) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(price)) {
                createStore(name, phone, address, linkMap, price, fromCreateResupply, fromChooseStore, fromIndexStore)
            } else {
                showAlert(getString(R.string.please_fill_in_all_input))
            }
        }
    }

    private fun createStore(name: String, phone: String, address: String, linkMap: String, price: String, fromCreateResupply:Boolean, fromChooseStore:Boolean, fromIndexStore:Boolean) {
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
                        when {
                            fromChooseStore -> navigateToChooseStore(fromCreateResupply)
                            fromIndexStore -> navigateToIndexStore()
                            else -> {
                                showAlert("Halaman Tidak Dapat Ditemukan")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToChooseStore(fromCreateResupply:Boolean) {
        val intentToChoose = Intent(this@CreateStoreActivity, ChooseStoreActivity::class.java)
        intentToChoose.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToChoose.putExtra("FROM-CREATE-STORE",true)
        startActivity(intentToChoose)
        finish()
    }
    private fun navigateToIndexStore() {
        val intentToIndex = Intent(this@CreateStoreActivity, IndexStoreActivity::class.java)
        intentToIndex.putExtra("FROM-CREATE-STORE", true)
        startActivity(intentToIndex)
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
                    Intent(this@CreateStoreActivity, ChooseStoreActivity::class.java).apply {
                        putExtra("FROM-CREATE-RESUPPLY", fromCreateResupply)
                    }
                }
                fromIndexStore -> {
                    Intent(this@CreateStoreActivity, IndexStoreActivity::class.java)
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
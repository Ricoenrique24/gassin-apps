package com.naffeid.gassin.ui.pages.manager.choose.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.model.Store
import com.naffeid.gassin.data.remote.response.ListStoreItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityChooseStoreBinding
import com.naffeid.gassin.ui.adapter.ChooseStoreAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.edit.EditReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.store.create.CreateStoreActivity
import com.naffeid.gassin.ui.pages.manager.store.show.ShowStoreActivity

class ChooseStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseStoreBinding
    private val viewModel by viewModels<ChooseStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var chooseStoreAdapter: ChooseStoreAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val idResupply = intent.getStringExtra("RESUPPLY")
        val quantity = intent.getStringExtra("QUANTITY")
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromEditResupply = intent.getBooleanExtra("FROM-EDIT-RESUPPLY",false)
        val fromCreateStore = intent.getBooleanExtra("FROM-CREATE-STORE",false)
        if (fromCreateStore) {
            setupData()
        }
        setupRecyclerView(quantity.toString(), idResupply.toString(), fromCreateResupply, fromEditResupply)
        setupView(quantity.toString(), fromCreateResupply, fromEditResupply)
        setupData()
        setupTopBar(quantity.toString(), fromCreateResupply, fromEditResupply, idResupply.toString())
    }
    private fun setupData() {
        showAllStore()
    }

    private fun setupView(qty:String, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        with(binding){
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                val query = searchView.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchStore(query)
                } else {
                    showAllStore()
                }
                false
            }

            //Create Store
            btnAddStory.setOnClickListener {
                navigateToCreateStore(qty, fromCreateResupply, fromEditResupply)
            }
        }
    }

    private fun setupRecyclerView(qty:String, idResupply:String, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        chooseStoreAdapter = ChooseStoreAdapter()
        chooseStoreAdapter.setOnItemClickCallback(object : ChooseStoreAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoreItem) {
                navigateToShowStore(qty, data, fromCreateResupply, fromEditResupply)
            }

            override fun onChooseStoreClicked(data: ListStoreItem) {
                selectedStore(qty, data, idResupply, fromCreateResupply, fromEditResupply)
            }
        })
        binding.rvStore.apply {
            layoutManager = LinearLayoutManager(this@ChooseStoreActivity)
            adapter = chooseStoreAdapter
        }
    }

    private fun showAllStore() {
        viewModel.showAllStore().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listStore = result.data.listStore
                    chooseStoreAdapter.submitList(listStore)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error store:", result.error.toString())
                }
            }
        }
    }

    private fun searchStore(query: String) {
        viewModel.searchStore(query).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listStore = result.data.listStore
                    chooseStoreAdapter.submitList(listStore)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search store:", result.error.toString())
                }
            }
        }
    }
    private fun selectedStore(qty:String, data: ListStoreItem, idResupply:String, fromCreateResupply:Boolean, fromEditResupply:Boolean){
        viewModel.deleteStore()
        viewModel.saveStore(
            Store(
                id = data.id ?: 0,
                name = data.name ?: "",
                phone = data.phone ?: "",
                address = data.address ?: "",
                linkMap = data.linkMap ?: "",
                price = data.price ?: ""
            )
        )
        showAlert(getString(R.string.berhasil_memilih_store))
        when {
            fromCreateResupply -> {
                navigateToCreateResupply(qty)
            }
            fromEditResupply -> {
                navigateToEditResupply(qty, idResupply)
            }
        }

    }

    private fun navigateToCreateResupply(qty:String) {
        val intentToCreateResupply = Intent(this@ChooseStoreActivity, CreateReSupplyTransactionActivity::class.java)
        intentToCreateResupply.putExtra("QUANTITY", qty)
        startActivity(intentToCreateResupply)
        finish()
    }

    private fun navigateToEditResupply(qty:String, id: String) {
        val intentToEdit = Intent(this@ChooseStoreActivity, EditReSupplyTransactionActivity::class.java)
        intentToEdit.putExtra("RESUPPLY", id)
        intentToEdit.putExtra("CHOOSE-UPDATED", true)
        intentToEdit.putExtra("QUANTITY", qty)
        startActivity(intentToEdit)
        finish()
    }

    private fun navigateToCreateStore(qty:String, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        val intentToCreate = Intent(this@ChooseStoreActivity, CreateStoreActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToCreate.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
        intentToCreate.putExtra("FROM-CHOOSE-STORE",true)
        intentToCreate.putExtra("QUANTITY", qty)
        startActivity(intentToCreate)
    }

    private fun navigateToShowStore(qty:String, data: ListStoreItem, fromCreateResupply:Boolean, fromEditResupply:Boolean) {
        val intentToDetail = Intent(this@ChooseStoreActivity, ShowStoreActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToDetail.putExtra("FROM-EDIT-RESUPPLY",fromEditResupply)
        intentToDetail.putExtra("FROM-CHOOSE-STORE",true)
        intentToDetail.putExtra("STORE", data)
        intentToDetail.putExtra("QUANTITY", qty)
        startActivity(intentToDetail)
    }

    private fun setupTopBar(qty:String, fromCreateResupply: Boolean, fromEditResupply: Boolean, id:String) {
        if (fromCreateResupply && fromEditResupply) {
            showAlert("Halaman Tidak Dapat Ditemukan")
            return
        }

        binding.btnBack.setOnClickListener {
            val intentToHome = when {
                fromCreateResupply -> {
                    Intent(this@ChooseStoreActivity, CreateReSupplyTransactionActivity::class.java).apply {
                        putExtra("QUANTITY", qty)
                    }
                }
                fromEditResupply -> {
                    Intent(this@ChooseStoreActivity, EditReSupplyTransactionActivity::class.java).apply {
                        putExtra("RESUPPLY", id)
                        putExtra("QUANTITY", qty)
                    }
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
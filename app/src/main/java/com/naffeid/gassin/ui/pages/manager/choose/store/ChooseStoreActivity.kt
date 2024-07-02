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
        val fromCreateResupply = intent.getBooleanExtra("FROM-CREATE-RESUPPLY",false)
        val fromCreateStore = intent.getBooleanExtra("FROM-CREATE-STORE",false)
        if (fromCreateStore) {
            setupData()
        }
        setupRecyclerView(fromCreateResupply)
        setupView(fromCreateResupply)
        setupData()
        setupTopBar()
    }
    private fun setupData() {
        showAllStore()
    }

    private fun setupView(fromCreateResupply:Boolean) {
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
                navigateToCreateStore(fromCreateResupply)
            }
        }
    }

    private fun setupRecyclerView(fromCreateResupply:Boolean) {
        chooseStoreAdapter = ChooseStoreAdapter()
        chooseStoreAdapter.setOnItemClickCallback(object : ChooseStoreAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoreItem) {
                navigateToShowStore(data, fromCreateResupply)
            }

            override fun onChooseStoreClicked(data: ListStoreItem) {
                selectedStore(data)
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
    private fun selectedStore(data: ListStoreItem){
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
        navigateToCreateResupply()
    }

    private fun navigateToCreateResupply() {
        val intentToCreateResupply = Intent(this@ChooseStoreActivity, CreateReSupplyTransactionActivity::class.java)
        startActivity(intentToCreateResupply)
        finish()
    }

    private fun navigateToCreateStore(fromCreateResupply:Boolean) {
        val intentToCreate = Intent(this@ChooseStoreActivity, CreateStoreActivity::class.java)
        intentToCreate.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToCreate.putExtra("FROM-CHOOSE-STORE",true)
        startActivity(intentToCreate)
    }

    private fun navigateToShowStore(data: ListStoreItem, fromCreateResupply:Boolean) {
        val intentToDetail = Intent(this@ChooseStoreActivity, ShowStoreActivity::class.java)
        intentToDetail.putExtra("FROM-CREATE-RESUPPLY",fromCreateResupply)
        intentToDetail.putExtra("FROM-CHOOSE-STORE",true)
        intentToDetail.putExtra("STORE", data)
        startActivity(intentToDetail)
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            val intentToHome = Intent(this@ChooseStoreActivity, CreateReSupplyTransactionActivity::class.java)
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
package com.naffeid.gassin.ui.pages.manager.store.index

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListStoreItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.ActivityIndexStoreBinding
import com.naffeid.gassin.ui.adapter.StoreAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.store.create.CreateStoreActivity
import com.naffeid.gassin.ui.pages.manager.store.show.ShowStoreActivity

class IndexStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIndexStoreBinding
    private val viewModel by viewModels<IndexStoreViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var storeAdapter: StoreAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val updateStore = intent.getBooleanExtra("STOREUPDATED",false)
        if (updateStore) {
            showAllStore()
        }
        setupTobBar()
        setupRecyclerView()
        setupView()
        showAllStore()
    }

    private fun setupView() {
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
                val intentToDetail = Intent(this@IndexStoreActivity, CreateStoreActivity::class.java)
                startActivity(intentToDetail)
            }
        }
    }

    private fun setupRecyclerView() {
        storeAdapter = StoreAdapter()
        storeAdapter.setOnItemClickCallback(object : StoreAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoreItem) {
                val intentToDetail = Intent(this@IndexStoreActivity, ShowStoreActivity::class.java)
                intentToDetail.putExtra("STORE", data)
                startActivity(intentToDetail)
            }
        })
        binding.rvStore.apply {
            layoutManager = LinearLayoutManager(this@IndexStoreActivity)
            adapter = storeAdapter
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
                    storeAdapter.submitList(listStore)
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
                    storeAdapter.submitList(listStore)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search store:", result.error.toString())
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
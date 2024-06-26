package com.naffeid.gassin.ui.pages.manager.main.stock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.remote.response.ListResupplyItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentStockManagerBinding
import com.naffeid.gassin.ui.adapter.ResupplyTransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.show.ShowReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class StockFragment : Fragment() {

    private var _binding: FragmentStockManagerBinding? = null

    private val binding get() = _binding!!
    private val viewModel by viewModels<StockViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var resupplyTransactionAdapter: ResupplyTransactionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }

            setupView()
            setupRecyclerView()
            showAllResupplyTransaction()
        }

        return root
    }
    private fun setupView() {
        with(binding){
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                val query = searchView.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchResupplyTransaction(query)
                } else {
                    showAllResupplyTransaction()
                }
                false
            }
        }
        binding.btnAddResupply.setOnClickListener {
            val intent = Intent(requireContext(), CreateReSupplyTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        resupplyTransactionAdapter = ResupplyTransactionAdapter()
        resupplyTransactionAdapter.setOnItemClickCallback(object : ResupplyTransactionAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListResupplyItem) {
                val intentToDetail = Intent(requireContext(), ShowReSupplyTransactionActivity::class.java)
                intentToDetail.putExtra("RESUPPLY", data.id.toString())
                startActivity(intentToDetail)
            }
        })
        binding.rvStockTransaction.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = resupplyTransactionAdapter
        }
    }

    private fun showAllResupplyTransaction() {
        viewModel.showAllResupplyTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listResupply = result.data.listResupply
                    resupplyTransactionAdapter.submitList(listResupply)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun searchResupplyTransaction(query: String) {
        viewModel.searchResupplyTransaction(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listResupply = result.data.listResupply
                    resupplyTransactionAdapter.submitList(listResupply)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search store:", result.error.toString())
                }
            }
        }
    }
    private fun navigationToSignIn(){
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
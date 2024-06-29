package com.naffeid.gassin.ui.pages.manager.main.cost

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
import com.naffeid.gassin.data.remote.response.ListOperationItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentCostManagerBinding
import com.naffeid.gassin.ui.adapter.OperationTransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.cost.show.ShowCostActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class CostFragment : Fragment() {
    private var _binding: FragmentCostManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CostViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var operationTransactionAdapter: OperationTransactionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCostManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }

            setupView()
            setupRecyclerView()
            showAllOperationTransaction()
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
                    searchOperationTransaction(query)
                } else {
                    showAllOperationTransaction()
                }
                false
            }
        }
    }

    private fun setupRecyclerView() {
        operationTransactionAdapter = OperationTransactionAdapter()
        operationTransactionAdapter.setOnItemClickCallback(object : OperationTransactionAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListOperationItem) {
                val intentToDetail = Intent(requireContext(), ShowCostActivity::class.java)
                intentToDetail.putExtra("OPERATION-TRANSACTION", data.id.toString())
                intentToDetail.putExtra("TYPE-TRANSACTION", data.categoryTransaction?.name.toString())
                startActivity(intentToDetail)
            }
        })
        binding.rvOperationTransaction.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = operationTransactionAdapter
        }
    }

    private fun showAllOperationTransaction() {
        viewModel.showAllOperationTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listOperation = result.data.listOperation
                    operationTransactionAdapter.submitList(listOperation)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun searchOperationTransaction(query: String) {
        viewModel.searchOperationTransaction(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listOperation = result.data.listOperation
                    operationTransactionAdapter.submitList(listOperation)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search customer:", result.error.toString())
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
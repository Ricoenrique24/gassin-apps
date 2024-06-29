package com.naffeid.gassin.ui.pages.employee.main.order

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
import com.naffeid.gassin.data.remote.response.ListTransactionItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentOrderEmployeeBinding
import com.naffeid.gassin.ui.adapter.TransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.order.show.ShowOrderEmployeeActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class OrderEmployeeFragment : Fragment() {

    private var _binding: FragmentOrderEmployeeBinding? = null

    private val binding get() = _binding!!
    private val viewModel by viewModels<OrderEmployeeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderEmployeeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }
            setupRecyclerView()
            showAllTransaction()
        }

        return root
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        transactionAdapter.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListTransactionItem) {
                val intentToDetail = Intent(requireContext(), ShowOrderEmployeeActivity::class.java)
                intentToDetail.putExtra("TRANSACTION", data.id.toString())
                intentToDetail.putExtra("TYPE-TRANSACTION", data.type.toString())
                startActivity(intentToDetail)
            }
        })
        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = transactionAdapter
        }
        val swipeRefreshLayout = binding.swipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener {
            showAllTransaction()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showAllTransaction() {
        viewModel.showAllTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listPurchase = result.data.listTransaction
                    transactionAdapter.submitList(listPurchase)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    private fun navigationToSignIn(){
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
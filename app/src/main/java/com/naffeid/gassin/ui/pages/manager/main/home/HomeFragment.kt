package com.naffeid.gassin.ui.pages.manager.main.home

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
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.remote.response.ListTransactionItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.FragmentHomeManagerBinding
import com.naffeid.gassin.ui.adapter.TransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.order.show.ShowOrderEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeManagerBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }
            setupView(user)
            setupRecyclerView()
            showAllActiveTransactionManager()
        }

        return root
    }

    private fun setupView(user:User) {
        with(binding){
            username.text = user.username
            dashboardCardSetup()
            buttonUserTransactionFeature()
            buttonReStockFeature()
            setupRecyclerView()
        }
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
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.isAutoMeasureEnabled = false
        binding.rvTransaction.apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            adapter = transactionAdapter
        }
    }
    private fun showAllActiveTransactionManager() {
        viewModel.showAllActiveTransactionManager().observe(viewLifecycleOwner) { result ->
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

    private fun buttonUserTransactionFeature() {
        binding.btnOrderFeature.setOnClickListener {
            val intent = Intent(requireContext(), CreatePurchaseTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun buttonReStockFeature() {
        binding.btnRestockFeature.setOnClickListener {
            val intent = Intent(requireContext(), CreateReSupplyTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun dashboardCardSetup() {
        //implementation dashboard
        showRevenueToday()
        showAvailableStockQuantity()
        buttonToStore()
        buttonToEmployee()
        buttonToCustomer()
        buttonToReport()
    }

    private fun showRevenueToday() {
        viewModel.getRevenueToday().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val revenue = result.data.dailyRevenue?.toDoubleOrNull() ?: 0.0
                    binding.tvRevenueToday.text = Rupiah.convertToRupiah(revenue)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun showAvailableStockQuantity() {
        viewModel.getAvailableStockQuantity().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val revenue = result.data.stock
                    binding.tvAvailableStock.text = revenue.toString()
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun buttonToReport() {
        binding.btnReportFeature.setOnClickListener {
            val intent = Intent(requireContext(), CreateReSupplyTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun buttonToCustomer() {
        binding.btnCustomerFeature.setOnClickListener {
            val intent = Intent(requireContext(), IndexCustomerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun buttonToEmployee() {
        binding.btnEmployeeFeature.setOnClickListener {
            val intent = Intent(requireContext(), IndexEmployeeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun buttonToStore() {
        binding.btnStoreFeature.setOnClickListener {
            val intent = Intent(requireContext(), IndexStoreActivity::class.java)
            startActivity(intent)
        }
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
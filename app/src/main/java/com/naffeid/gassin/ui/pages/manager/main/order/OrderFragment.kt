package com.naffeid.gassin.ui.pages.manager.main.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListPurchaseItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentOrderManagerBinding
import com.naffeid.gassin.ui.adapter.PurchaseTransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.show.ShowPurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderManagerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<OrderViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var purchaseTransactionAdapter: PurchaseTransactionAdapter
    // Variabel untuk menyimpan status dan filter waktu yang dipilih
    private var statusTransaction: String = "all"
    private var filterBy: String = "all"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }

            setupView()
            setupRecyclerView()
            showAllPurchaseTransaction()
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
                    searchPurchaseTransaction(query)
                } else {
                    showAllPurchaseTransaction()
                }
                false
            }

            // Setup ChipGroup for status filter
            chipGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.chip_allTransaction -> applyStatusFilter("all")
                    R.id.chip_pending_transaction -> applyStatusFilter("1")
                    R.id.chip_process_transaction -> applyStatusFilter("2")
                    R.id.chip_completed_transaction -> applyStatusFilter("3")
                    R.id.chip_cancel_transaction -> applyStatusFilter("4")
                }
            }
        }
        binding.btnAddPurchase.setOnClickListener {
            val intent = Intent(requireContext(), CreatePurchaseTransactionActivity::class.java)
            startActivity(intent)
        }

        // Setup filter button for time filter
        binding.filterButton.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun applyStatusFilter(status: String) {
        // Update statusTransaction variable
        statusTransaction = status
        showFilteredPurchaseTransaction()
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.time_filter_bottom_sheet, null)
        bottomSheet.setContentView(view)

        view.findViewById<MaterialRadioButton>(R.id.radioAll).setOnClickListener {
            filterBy = "all"
            showFilteredPurchaseTransaction()
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioDay).setOnClickListener {
            filterBy = "day"
            showFilteredPurchaseTransaction()
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioWeek).setOnClickListener {
            filterBy = "week"
            showFilteredPurchaseTransaction()
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioMonth).setOnClickListener {
            filterBy = "month"
            showFilteredPurchaseTransaction()
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun setupRecyclerView() {
        purchaseTransactionAdapter = PurchaseTransactionAdapter()
        purchaseTransactionAdapter.setOnItemClickCallback(object : PurchaseTransactionAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListPurchaseItem) {
                val intentToDetail = Intent(requireContext(), ShowPurchaseTransactionActivity::class.java)
                intentToDetail.putExtra("PURCHASE", data.id.toString())
                startActivity(intentToDetail)
            }
        })
        binding.rvOrderTransaction.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = purchaseTransactionAdapter
        }
    }

    private fun showAllPurchaseTransaction() {
        viewModel.showAllPurchaseTransaction().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listPurchase = result.data.listPurchase
                    purchaseTransactionAdapter.submitList(listPurchase)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun searchPurchaseTransaction(query: String) {
        viewModel.searchPurchaseTransaction(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listPurchase = result.data.listPurchase
                    purchaseTransactionAdapter.submitList(listPurchase)
                }

                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error search customer:", result.error.toString())
                }
            }
        }
    }
    private fun showFilteredPurchaseTransaction() {
        viewModel.showFilteredPurchaseTransaction(statusTransaction, filterBy).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    val listPurchase = result.data.listPurchase
                    purchaseTransactionAdapter.submitList(listPurchase)
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
package com.naffeid.gassin.ui.pages.manager.main.home

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.naffeid.gassin.data.utils.getFileUri
import com.naffeid.gassin.databinding.FragmentHomeManagerBinding
import com.naffeid.gassin.ui.adapter.TransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.main.EmployeeMainActivity
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.show.ShowPurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.show.ShowReSupplyTransactionActivity
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

        checkSession()

        return root
    }
    private fun setupData(user:User) {
        showAllActiveTransactionManager()
        showRevenueToday()
        showAvailableStockQuantity()
        setupView(user)
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
                val type = data.type
                if (type == "purchase") {
                    val intentToDetail = Intent(requireContext(), ShowPurchaseTransactionActivity::class.java)
                    intentToDetail.putExtra("PURCHASE", data.id.toString())
                    startActivity(intentToDetail)
                } else if (type == "resupply") {
                    val intentToDetail = Intent(requireContext(), ShowReSupplyTransactionActivity::class.java)
                    intentToDetail.putExtra("RESUPPLY", data.id.toString())
                    startActivity(intentToDetail)
                }

            }
        })
        binding.rvTransaction.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            this.layoutManager?.isAutoMeasureEnabled = false
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
    private fun downloadTransactionReport() {
        viewModel.downloadTransactionReport().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val responseBody = result.data.body()
                    if (responseBody != null) {
                        val byteArray = responseBody.bytes()
                        val uri = saveExcelFile(requireContext(), byteArray)
                        uri?.let {
                            openExcelFile(requireContext(), it)
                        }
                        showAlert("Berhasil mendownload laporan")
                    } else {
                        showAlert("Gagal mendownload laporan")
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }

    private fun saveExcelFile(context: Context, byteArray: ByteArray): Uri {
        val uri = getFileUri(context, "Documents/MyReports")
        uri.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(byteArray)
                showAlert("Laporan berhasil disimpan")
            } ?: showAlert("Gagal menyimpan laporan")
        }
        return uri
    }

    private fun openExcelFile(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.ms-excel")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open with"))
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
            downloadTransactionReport()
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

    private fun checkSession() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.showUser(user.id.toString()).observe(viewLifecycleOwner) {
                    when (it) {
                        is Result.Loading -> {
                            Log.d("Get User Data Process:", "Loading ...")
                        }

                        is Result.Error -> {
                            Log.d("Get User Data Process:", "Error: ${it.error}")
                            navigateToSignInScreen()
                        }

                        is Result.Success -> {
                            val data = it.data.loginResult
                            if (data != null) {
                                val userData = User(
                                    id = data.id!!,
                                    name = data.name ?: "",
                                    username = data.username ?: "",
                                    email = data.email ?: "",
                                    phone = data.phone ?: "",
                                    role = data.role ?: "",
                                    apikey = data.apikey ?: "",
                                    tokenfcm = data.tokenFcm ?: ""
                                )
                                if (userData == user) {
                                    checkRole(userData)
                                } else {
                                    viewModel.logout()
                                    navigateToSignInScreen()
                                }
                            } else {
                                viewModel.logout()
                                navigateToSignInScreen()
                            }
                        }
                    }
                }
            } else {
                navigateToSignInScreen()
            }
        }
    }
    private fun checkRole(userData: User) {
        viewModel.checkUserRole(userData.role) { isRoleMatch ->
            if (isRoleMatch) {
                 when (userData.role) {
                    "employee" -> {
                        val intent = Intent(requireContext(), EmployeeMainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    "manager" -> {
                        setupData(userData)
                        setupRecyclerView()
                        showAllActiveTransactionManager()
                    }
                    else -> {
                        val intent = Intent(requireContext(), SignInActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }

            } else {
                navigateToSignInScreen()
            }
        }
    }

    private fun navigateToSignInScreen() {
        startActivity(Intent(requireContext(), SignInActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
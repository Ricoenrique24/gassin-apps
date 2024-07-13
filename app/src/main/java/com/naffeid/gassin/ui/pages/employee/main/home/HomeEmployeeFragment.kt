package com.naffeid.gassin.ui.pages.employee.main.home

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
import com.naffeid.gassin.databinding.FragmentHomeEmployeeBinding
import com.naffeid.gassin.ui.adapter.TransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.order.show.ShowOrderEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class HomeEmployeeFragment : Fragment() {

    private var _binding: FragmentHomeEmployeeBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeEmployeeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var transactionAdapter: TransactionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeEmployeeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        checkSession()

        return root
    }

    private fun setupView(user: User) {
        with(binding){
            username.text = user.username
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
        val swipeRefreshLayout = binding.swipeRefreshLayout
        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = transactionAdapter
        }
        swipeRefreshLayout.setOnRefreshListener {
            showAllTransaction()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showAllTransaction() {
        viewModel.showAllActiveTransaction().observe(viewLifecycleOwner) { result ->
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
                        setupView(userData)
                        setupRecyclerView()
                        showAllTransaction()
                    }
                    "manager" -> {
                        val intent = Intent(requireContext(), ManagerMainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
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
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
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.remote.response.ListOperationItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentCostManagerBinding
import com.naffeid.gassin.ui.adapter.OperationTransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.main.EmployeeMainActivity
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

        checkSession()

        return root
    }

    private fun setupView() {
        with(binding){
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
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
                        setupView()
                        setupRecyclerView()
                        showAllOperationTransaction()
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
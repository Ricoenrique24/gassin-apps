package com.naffeid.gassin.ui.pages.manager.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.databinding.FragmentHomeManagerBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeManagerBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

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
        }

        return root
    }

    private fun setupView(user:User) {
        with(binding){
            username.text = user.username
            dashboardCardSetup()
            buttonUserTransactionFeature()
            buttonReStockFeature()
            recyclerviewTransactionSetup()
        }
    }

    private fun recyclerviewTransactionSetup() {
        // implementation insert data to recyclerview and show
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
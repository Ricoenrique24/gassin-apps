package com.naffeid.gassin.ui.pages.employee.main.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.databinding.FragmentMoreEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.user.show.ShowUserEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.main.ManagerMainActivity
import com.naffeid.gassin.ui.pages.manager.main.more.MoreViewModel
import com.naffeid.gassin.ui.pages.signin.SignInActivity

class MoreEmployeeFragment : Fragment() {

    private var _binding: FragmentMoreEmployeeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MoreViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreEmployeeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        checkSession()

        return root
    }
    private fun setupView(user: User) {
        with(binding){
            tvUsername.text = user.username
            btnAccount.setOnClickListener {
                navigationToProfile()
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                navigateToSignInScreen()
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

    private fun navigationToProfile(){
        val intent = Intent(requireContext(), ShowUserEmployeeActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
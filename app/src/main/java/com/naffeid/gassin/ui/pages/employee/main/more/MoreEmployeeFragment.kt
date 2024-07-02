package com.naffeid.gassin.ui.pages.employee.main.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.databinding.FragmentMoreEmployeeBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.employee.user.show.ShowUserEmployeeActivity
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

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.apikey == null) {
                navigationToSignIn()
            }
            setupView(user)
        }

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
                navigationToSignIn()
            }
        }
    }

    private fun navigationToSignIn(){
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
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
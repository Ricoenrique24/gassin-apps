package com.naffeid.gassin.ui.pages.manager.main.cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.naffeid.gassin.R

class CostFragment : Fragment() {

    companion object {
        fun newInstance() = CostFragment()
    }

    private lateinit var viewModel: CostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cost_manager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
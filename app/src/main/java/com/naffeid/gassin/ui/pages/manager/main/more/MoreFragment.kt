package com.naffeid.gassin.ui.pages.manager.main.more

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
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.getFileUri
import com.naffeid.gassin.databinding.FragmentMoreManagerBinding
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity
import com.naffeid.gassin.ui.pages.manager.user.show.ShowUserActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreManagerBinding? = null

    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private val binding get() = _binding!!

    private val viewModel by viewModels<MoreViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreManagerBinding.inflate(inflater, container, false)
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
            dashboardCardSetup()
            btnAccount.setOnClickListener {
                navigationToProfile()
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                navigationToSignIn()
            }
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

    private fun navigationToSignIn(){
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }
    private fun navigationToProfile(){
        val intent = Intent(requireContext(), ShowUserActivity::class.java)
        startActivity(intent)
    }
    private fun downloadTransactionReport() {
        viewModel.downloadTransactionReport().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showAlert("Loading ...")
                }
                is Result.Success -> {
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
                    showAlert(result.error)
                    Log.e("error employee:", result.error.toString())
                }
            }
        }
    }
    private fun saveExcelFile(context: Context, byteArray: ByteArray): Uri? {
        val uri = getFileUri(context, "Documents/MyReports")
        uri?.let {
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

    private fun showAlert(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
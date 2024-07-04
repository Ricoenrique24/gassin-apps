package com.naffeid.gassin.ui.pages.manager.main.home

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.naffeid.gassin.BuildConfig
import com.naffeid.gassin.data.model.User
import com.naffeid.gassin.data.remote.response.ListTransactionItem
import com.naffeid.gassin.data.utils.Result
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.databinding.FragmentHomeManagerBinding
import com.naffeid.gassin.ui.adapter.TransactionAdapter
import com.naffeid.gassin.ui.pages.ViewModelFactory
import com.naffeid.gassin.ui.pages.manager.customer.index.IndexCustomerActivity
import com.naffeid.gassin.ui.pages.manager.employee.index.IndexEmployeeActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.create.CreatePurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.purchasetransaction.show.ShowPurchaseTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.create.CreateReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.resupplytransaction.show.ShowReSupplyTransactionActivity
import com.naffeid.gassin.ui.pages.manager.store.index.IndexStoreActivity
import com.naffeid.gassin.ui.pages.signin.SignInActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeManagerBinding? = null

    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

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
            setupData(user)
            setupRecyclerView()
            showAllActiveTransactionManager()
        }

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
                        saveFile(requireContext(), responseBody)
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

    private fun getFileUri(context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.xlsx")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/MyReports/")
            }
            context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                ?: throw IOException("Failed to create new MediaStore record.")
        } else {
            val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(filesDir, "/MyReports/$timeStamp.xlsx")
            if (file.parentFile?.exists() == false) file.parentFile?.mkdirs()
            FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
        }
    }

    private fun saveFile(context: Context, responseBody: ResponseBody) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uri = getFileUri(context)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val inputStream: InputStream = responseBody.byteStream()
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    inputStream.close()
                    outputStream.close()
                } ?: throw IOException("Failed to open output stream.")

                CoroutineScope(Dispatchers.Main).launch {
                    showAlert("File berhasil disimpan di $uri")
                    openDownloadedFile(uri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    showAlert("Gagal menyimpan file: ${e.message}")
                }
            }
        }
    }

    private fun openDownloadedFile(uri: Uri) {
//        val uri = FileProvider.getUriForFile(
//            requireContext(),
//            requireContext().packageName + ".fileprovider",
//            file
//        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
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

    private fun navigationToSignIn(){
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
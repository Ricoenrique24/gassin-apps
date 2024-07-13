package com.naffeid.gassin.ui.components

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naffeid.gassin.databinding.ProblemBottomSheetBinding
import com.naffeid.gassin.ui.adapter.ProblemBottomSheetAdapter

object ProblemBottomSheet {

    fun showBottomSheet(
        context: Context,
        preDefinedReasons: List<String>,
        listener: (String) -> Unit
    ) {

        val binding = ProblemBottomSheetBinding.inflate(LayoutInflater.from(context))

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(binding.root)

        // RecyclerView untuk daftar pilihan kendala
        val rvReasons = binding.rvReasons
        rvReasons.layoutManager = LinearLayoutManager(context)
        val adapter = ProblemBottomSheetAdapter(preDefinedReasons)
        rvReasons.adapter = adapter

        // EditText untuk kendala khusus
        val etCustomReason = binding.etCustomReason

        // Tombol untuk membatalkan pengantaran
        val btnCancelDelivery = binding.btnCancelTransaction
        btnCancelDelivery.setOnClickListener {
            val reason = etCustomReason.text.toString().trim()
            dialog.dismiss()
            listener.invoke(reason)
        }

        // Handle klik pada pilihan kendala
        adapter.setOnItemClickListener(object : ProblemBottomSheetAdapter.OnItemClickListener {
            override fun onItemClick(reason: String) {
                etCustomReason.setText(reason)
            }
        })

        dialog.show()
    }
}

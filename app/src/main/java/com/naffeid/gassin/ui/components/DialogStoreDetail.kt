package com.naffeid.gassin.ui.components

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.Store
import com.naffeid.gassin.databinding.DialogStoreDetailBinding

object DialogStoreDetail {
    fun showStoreDetailDialog(
        context: Context,
        data: Store
    ) {
        val binding = DialogStoreDetailBinding.inflate(LayoutInflater.from(context))
        val StoreDetailDialog = Dialog(context, R.style.CustomDialogTheme)
        StoreDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        StoreDetailDialog.setContentView(binding.root)

        val StoreName = data.name.toString()
        val StorePhone = data.phone.toString()
        val StoreAddress = data.address.toString()
        val StoreLinkMap = data.linkMap.toString()

        binding.tvStoreName.text = StoreName
        binding.tvStorePhone.text = StorePhone
        binding.tvStoreAddress.text = StoreAddress

        binding.btnCallStore.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:$StorePhone"))
            if (callIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(callIntent)
            } else {
                Toast.makeText(context, "No application found to handle call", Toast.LENGTH_LONG).show()
            }
            StoreDetailDialog.dismiss()
        }

        if (!StoreLinkMap.isNullOrBlank()) {
            binding.btnMaps.setOnClickListener {
                val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(StoreLinkMap))
                if (mapsIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapsIntent)
                } else {
                    Toast.makeText(context, "No application found to handle maps link", Toast.LENGTH_LONG).show()
                }
                StoreDetailDialog.dismiss()
            }
        } else {
            binding.btnMaps.visibility = View.GONE
            binding.spacebtn.visibility = View.GONE
        }

        StoreDetailDialog.setCancelable(true)
        StoreDetailDialog.show()
    }
}


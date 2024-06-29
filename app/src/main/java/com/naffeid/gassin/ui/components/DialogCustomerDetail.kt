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
import com.naffeid.gassin.data.remote.response.Customer
import com.naffeid.gassin.databinding.DialogCustomerDetailBinding

object DialogCustomerDetail {
    fun showCustomerDetailDialog(
        context: Context,
        data: Customer
    ) {
        val binding = DialogCustomerDetailBinding.inflate(LayoutInflater.from(context))
        val customerDetailDialog = Dialog(context, R.style.CustomDialogTheme)
        customerDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customerDetailDialog.setContentView(binding.root)

        val customerName = data.name.toString()
        val customerPhone = data.phone.toString()
        val customerAddress = data.address.toString()
        val customerLinkMap = data.linkMap.toString()

        binding.tvCustomerName.text = customerName
        binding.tvCustomerPhone.text = customerPhone
        binding.tvCustomerAddress.text = customerAddress

        binding.btnCallCustomer.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:$customerPhone"))
            if (callIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(callIntent)
            } else {
                Toast.makeText(context, "No application found to handle call", Toast.LENGTH_LONG).show()
            }
            customerDetailDialog.dismiss()
        }

        if (!customerLinkMap.isNullOrBlank()) {
            binding.btnMaps.setOnClickListener {
                val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(customerLinkMap))
                if (mapsIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapsIntent)
                } else {
                    Toast.makeText(context, "No application found to handle maps link", Toast.LENGTH_LONG).show()
                }
                customerDetailDialog.dismiss()
            }
        } else {
            binding.btnMaps.visibility = View.GONE
            binding.spacebtn.visibility = View.GONE
        }

        customerDetailDialog.setCancelable(true)
        customerDetailDialog.show()
    }
}


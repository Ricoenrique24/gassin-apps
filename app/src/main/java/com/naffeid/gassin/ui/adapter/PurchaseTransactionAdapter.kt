package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.data.remote.response.ListPurchaseItem
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.Timestamp
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ItemPurchaseTransactionBinding

class PurchaseTransactionAdapter() : ListAdapter<ListPurchaseItem, PurchaseTransactionAdapter.PurchaseViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val binding = ItemPurchaseTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PurchaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        val purchase = getItem(position)
        holder.bind(purchase)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(purchase) }
    }

    class PurchaseViewHolder(private val binding: ItemPurchaseTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(purchase: ListPurchaseItem) {
            val totalPayment = purchase.totalPayment?.toDoubleOrNull() ?: 0.0
            binding.tvCustomerName.text = purchase.customer?.name?.capitalizeWords()
            val statusTransaction = purchase.statusTransaction?.id
            binding.tvStatusTransaction.text = TransactionStatus.convertStatusToDescription(statusTransaction.toString())
            val createdAt = purchase.createdAt.toString()
            binding.tvTimeTransaction.text = Timestamp.convertToDesiredFormat(createdAt)
            val qty = purchase.qty
            binding.tvQtyGas.text = "${qty} Tabung Gas 3 Kg"
            binding.tvTotalPayment.text = Rupiah.convertToRupiah(totalPayment)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListPurchaseItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListPurchaseItem> = object : DiffUtil.ItemCallback<ListPurchaseItem>() {
            override fun areItemsTheSame(oldItem: ListPurchaseItem, newItem: ListPurchaseItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListPurchaseItem, newItem: ListPurchaseItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
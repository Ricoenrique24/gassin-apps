package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListTransactionItem
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.Timestamp
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ItemTransactionBinding

class TransactionAdapter() : ListAdapter<ListTransactionItem, TransactionAdapter.TransactionViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(transaction) }
    }

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: ListTransactionItem) {
            val categoryTransaction = transaction.type.toString()
            val totalPayment = transaction.totalPayment?.toDoubleOrNull() ?: 0.0
            if (categoryTransaction == "purchase") {
                binding.tvName.text = transaction.customer?.name?.capitalizeWords()
            } else if (categoryTransaction == "resupply") {
                binding.tvName.text = transaction.store?.name?.capitalizeWords()
            }
            val statusTransaction = transaction.statusTransaction?.id
            binding.tvStatusTransaction.text = TransactionStatus.convertStatusToDescription(statusTransaction.toString())
            val createdAt = transaction.createdAt.toString()
            binding.tvTimeTransaction.text = Timestamp.convertToDesiredFormat(createdAt)
            val qty = transaction.qty
            binding.tvQtyGas.text = "${qty} Tabung Gas 3 Kg"
            binding.tvTotalPayment.text = Rupiah.convertToRupiah(totalPayment)
            if (categoryTransaction == "purchase") {
                binding.ivCategoryTransation.setImageResource(R.drawable.ic_delivery_24dp)
            } else if (categoryTransaction == "resupply") {
                binding.ivCategoryTransation.setImageResource(R.drawable.ic_shop_24dp)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListTransactionItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListTransactionItem> = object : DiffUtil.ItemCallback<ListTransactionItem>() {
            override fun areItemsTheSame(oldItem: ListTransactionItem, newItem: ListTransactionItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListTransactionItem, newItem: ListTransactionItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
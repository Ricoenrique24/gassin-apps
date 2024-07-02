package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.R
import com.naffeid.gassin.data.remote.response.ListOperationItem
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.Timestamp
import com.naffeid.gassin.databinding.ItemCostTransactionBinding

class OperationTransactionAdapter() : ListAdapter<ListOperationItem, OperationTransactionAdapter.CostViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostViewHolder {
        val binding = ItemCostTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        val cost = getItem(position)
        holder.bind(cost)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(cost) }
    }

    class CostViewHolder(private val binding: ItemCostTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: ListOperationItem) {
            val typeTransaction = transaction.categoryTransaction?.name.toString()
            when (typeTransaction) {
                "purchase" -> {
                    binding.tvCustomerName.text = transaction.purchase?.user?.name?.capitalizeWords()
                    binding.ivCategoryTransation.setImageResource(R.drawable.ic_delivery_24dp)
                }
                "resupply" -> {
                    binding.tvCustomerName.text = transaction.resupply?.user?.name?.capitalizeWords()
                    binding.ivCategoryTransation.setImageResource(R.drawable.ic_shop_24dp)
                }
                else -> {
                    binding.tvCustomerName.text = "Tidak ditemukan"
                }
            }
            val verified = transaction.verified
            when(verified) {
                0 -> {
                    binding.tvStatusTransaction.text = "Ditolak"
                }
                1 -> {
                    binding.tvStatusTransaction.text = "Disetujui"
                }
                else -> {
                    binding.tvStatusTransaction.text = "Butuh Konfirmasi"
                }
            }
            val createdAt = transaction.createdAt.toString()
            val totalPayment = transaction.totalPayment?.toDoubleOrNull() ?: 0.0
            binding.tvTimeTransaction.text = Timestamp.convertToDesiredFormat(createdAt)
            binding.tvQtyGas.text = transaction.note
            binding.tvTotalPayment.text = Rupiah.convertToRupiah(totalPayment)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListOperationItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListOperationItem> = object : DiffUtil.ItemCallback<ListOperationItem>() {
            override fun areItemsTheSame(oldItem: ListOperationItem, newItem: ListOperationItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListOperationItem, newItem: ListOperationItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
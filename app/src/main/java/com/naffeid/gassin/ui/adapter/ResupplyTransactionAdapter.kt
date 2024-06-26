package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.data.remote.response.ListResupplyItem
import com.naffeid.gassin.data.utils.CapitalizeWords.capitalizeWords
import com.naffeid.gassin.data.utils.Rupiah
import com.naffeid.gassin.data.utils.Timestamp
import com.naffeid.gassin.data.utils.TransactionStatus
import com.naffeid.gassin.databinding.ItemResupplyTransactionBinding

class ResupplyTransactionAdapter() : ListAdapter<ListResupplyItem, ResupplyTransactionAdapter.ResupplyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResupplyViewHolder {
        val binding = ItemResupplyTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResupplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResupplyViewHolder, position: Int) {
        val resupply = getItem(position)
        holder.bind(resupply)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(resupply) }
    }

    class ResupplyViewHolder(private val binding: ItemResupplyTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resupply: ListResupplyItem) {
            val totalPayment = resupply.totalPayment?.toDoubleOrNull() ?: 0.0
            binding.tvStoreName.text = resupply.store?.name?.capitalizeWords()
            val statusTransaction = resupply.statusTransaction?.id
            binding.tvStatusTransaction.text = TransactionStatus.convertStatusToDescription(statusTransaction.toString())
            val createdAt = resupply.createdAt.toString()
            binding.tvTimeTransaction.text = Timestamp.convertToDesiredFormat(createdAt)
            val qty = resupply.qty
            binding.tvQtyGas.text = "${qty} Tabung Gas 3 Kg"
            binding.tvTotalPayment.text = Rupiah.convertToRupiah(totalPayment)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListResupplyItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListResupplyItem> = object : DiffUtil.ItemCallback<ListResupplyItem>() {
            override fun areItemsTheSame(oldItem: ListResupplyItem, newItem: ListResupplyItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListResupplyItem, newItem: ListResupplyItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
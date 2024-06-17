package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.data.remote.response.ListCustomerItem
import com.naffeid.gassin.databinding.ItemCustomerBinding

class CustomerAdapter : ListAdapter<ListCustomerItem, CustomerAdapter.CustomerViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(customer) }
    }

    class CustomerViewHolder(private val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: ListCustomerItem) {
            binding.tvCustomerName.text = customer.name
            binding.tvPhone.text = customer.phone
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListCustomerItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListCustomerItem> = object : DiffUtil.ItemCallback<ListCustomerItem>() {
            override fun areItemsTheSame(oldItem: ListCustomerItem, newItem: ListCustomerItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListCustomerItem, newItem: ListCustomerItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.data.remote.response.ListStoreItem
import com.naffeid.gassin.databinding.ItemChooseStoreBinding

class ChooseStoreAdapter : ListAdapter<ListStoreItem, ChooseStoreAdapter.StoreViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemChooseStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = getItem(position)
        holder.bind(store)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(store) }
        holder.btnChooseStore.setOnClickListener { onItemClickCallback.onChooseStoreClicked(store) }
    }

    class StoreViewHolder(private val binding: ItemChooseStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        val btnChooseStore: CardView = binding.btnChooseStore
        fun bind(store: ListStoreItem) {
            binding.tvStoreName.text = store.name
            binding.tvPhone.text = store.phone
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoreItem)
        fun onChooseStoreClicked(data: ListStoreItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoreItem> = object : DiffUtil.ItemCallback<ListStoreItem>() {
            override fun areItemsTheSame(oldItem: ListStoreItem, newItem: ListStoreItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoreItem, newItem: ListStoreItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
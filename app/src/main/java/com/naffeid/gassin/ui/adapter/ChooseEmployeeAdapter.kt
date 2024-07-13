package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naffeid.gassin.data.remote.response.ListEmployeeItem
import com.naffeid.gassin.databinding.ItemChooseEmployeeBinding

class ChooseEmployeeAdapter : ListAdapter<ListEmployeeItem, ChooseEmployeeAdapter.EmployeeViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemChooseEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = getItem(position)
        holder.bind(employee)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(employee) }
        holder.btnChooseEmployee.setOnClickListener { onItemClickCallback.onChooseEmployeeClicked(employee) }
    }

    class EmployeeViewHolder(private val binding: ItemChooseEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {
        val btnChooseEmployee: CardView = binding.btnChooseEmployee
        fun bind(employee: ListEmployeeItem) {
            binding.tvEmployeeName.text = employee.name
            binding.tvPhone.text = employee.phone
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListEmployeeItem)
        fun onChooseEmployeeClicked(data: ListEmployeeItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListEmployeeItem> = object : DiffUtil.ItemCallback<ListEmployeeItem>() {
            override fun areItemsTheSame(oldItem: ListEmployeeItem, newItem: ListEmployeeItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEmployeeItem, newItem: ListEmployeeItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
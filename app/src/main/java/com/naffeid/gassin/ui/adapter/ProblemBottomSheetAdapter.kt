package com.naffeid.gassin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProblemBottomSheetAdapter(private val reasons: List<String>) :
    RecyclerView.Adapter<ProblemBottomSheetAdapter.ReasonViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(reason: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ReasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReasonViewHolder, position: Int) {
        val reason = reasons[position]
        holder.bind(reason)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(reason)
        }
    }

    override fun getItemCount(): Int {
        return reasons.size
    }

    inner class ReasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reasonTextView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(reason: String) {
            reasonTextView.text = reason
        }
    }
}
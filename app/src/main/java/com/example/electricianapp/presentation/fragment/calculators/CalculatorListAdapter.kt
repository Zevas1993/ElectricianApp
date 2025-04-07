package com.example.electricianapp.presentation.fragment.calculators

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemCalculatorBinding

class CalculatorListAdapter(
    private val onItemClick: (CalculatorListItem) -> Unit
) : ListAdapter<CalculatorListItem, CalculatorListAdapter.ViewHolder>(CalculatorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalculatorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemCalculatorBinding,
        private val onItemClick: (CalculatorListItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalculatorListItem) {
            binding.textViewCalculatorName.text = item.name
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class CalculatorDiffCallback : DiffUtil.ItemCallback<CalculatorListItem>() {
        override fun areItemsTheSame(oldItem: CalculatorListItem, newItem: CalculatorListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CalculatorListItem, newItem: CalculatorListItem): Boolean {
            return oldItem == newItem
        }
    }
}

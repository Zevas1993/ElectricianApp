package com.example.electricianapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemBoxComponentBinding
import com.example.electricianapp.domain.model.boxfill.BoxComponent

class BoxComponentAdapter(
    private val listener: BoxComponentItemListener
) : ListAdapter<BoxComponent, BoxComponentAdapter.BoxComponentViewHolder>(BoxComponentDiffCallback()) {

    interface BoxComponentItemListener {
        fun onEditComponent(position: Int, component: BoxComponent)
        fun onRemoveComponent(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxComponentViewHolder {
        val binding = ItemBoxComponentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoxComponentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BoxComponentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BoxComponentViewHolder(
        private val binding: ItemBoxComponentBinding,
        private val listener: BoxComponentItemListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditComponent(position, getItem(position))
                }
            }
            binding.removeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoveComponent(position)
                }
            }
        }

        fun bind(component: BoxComponent) {
            binding.componentTypeTextView.text = component.type.name
            val details = mutableListOf<String>()
            component.wireSize?.let { details.add("Size: $it AWG") }
            details.add("Qty: ${component.quantity}")
            binding.componentDetailsTextView.text = details.joinToString(", ")
        }
    }

    class BoxComponentDiffCallback : DiffUtil.ItemCallback<BoxComponent>() {
        override fun areItemsTheSame(oldItem: BoxComponent, newItem: BoxComponent): Boolean {
            // Assuming components don't have unique IDs, rely on content comparison for now
            // If components could be identical but represent different entries, a unique ID would be better.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BoxComponent, newItem: BoxComponent): Boolean {
            return oldItem == newItem
        }
    }
}

package com.example.electricianapp.presentation.adapter // Corrected package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemWireBinding // Import ViewBinding
import com.example.electricianapp.domain.model.conduitfill.Wire // Corrected import

class WireAdapter(
    private val listener: WireItemListener
) : ListAdapter<Wire, WireAdapter.WireViewHolder>(WireDiffCallback()) {

    interface WireItemListener {
        fun onEditWire(position: Int, wire: Wire)
        fun onRemoveWire(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WireViewHolder {
        // Inflate using ViewBinding
        val binding = ItemWireBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WireViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WireViewHolder, position: Int) {
        val wire = getItem(position)
        // Pass adapterPosition to listener methods if needed, otherwise it's not required in bind
        holder.bind(wire, listener)
    }

    // Update ViewHolder to use ViewBinding
    class WireViewHolder(private val binding: ItemWireBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wire: Wire, listener: WireItemListener) {
            binding.wireTypeTextView.text = wire.type.name // Assuming WireType is an enum with a name property
            binding.wireSizeTextView.text = wire.size
            binding.quantityTextView.text = "Qty: ${wire.quantity}"

            binding.editButton.setOnClickListener {
                // Use adapterPosition for accurate item position
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onEditWire(adapterPosition, wire)
                }
            }

            binding.removeButton.setOnClickListener {
                // Use adapterPosition for accurate item position
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onRemoveWire(adapterPosition)
                }
            }
        }
    }

    class WireDiffCallback : DiffUtil.ItemCallback<Wire>() {
        override fun areItemsTheSame(oldItem: Wire, newItem: Wire): Boolean {
            // Compare by the unique ID added to the Wire data class
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wire, newItem: Wire): Boolean {
            return oldItem == newItem // Data class equals checks all properties
        }
    }
}

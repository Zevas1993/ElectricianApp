package com.example.electricalcalculator.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricalcalculator.R
import com.example.electricalcalculator.domain.model.conduitfill.Wire

class WireAdapter(
    private val listener: WireItemListener
) : ListAdapter<Wire, WireAdapter.WireViewHolder>(WireDiffCallback()) {

    interface WireItemListener {
        fun onEditWire(position: Int, wire: Wire)
        fun onRemoveWire(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WireViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wire, parent, false)
        return WireViewHolder(view)
    }

    override fun onBindViewHolder(holder: WireViewHolder, position: Int) {
        val wire = getItem(position)
        holder.bind(wire, position, listener)
    }

    class WireViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wireTypeTextView: TextView = itemView.findViewById(R.id.wireTypeTextView)
        private val wireSizeTextView: TextView = itemView.findViewById(R.id.wireSizeTextView)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(wire: Wire, position: Int, listener: WireItemListener) {
            wireTypeTextView.text = wire.type.name
            wireSizeTextView.text = wire.size
            quantityTextView.text = "Qty: ${wire.quantity}"
            
            editButton.setOnClickListener {
                listener.onEditWire(position, wire)
            }
            
            removeButton.setOnClickListener {
                listener.onRemoveWire(position)
            }
        }
    }

    class WireDiffCallback : DiffUtil.ItemCallback<Wire>() {
        override fun areItemsTheSame(oldItem: Wire, newItem: Wire): Boolean {
            // In a real app, you might want to use a unique ID
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Wire, newItem: Wire): Boolean {
            return oldItem.type == newItem.type &&
                   oldItem.size == newItem.size &&
                   oldItem.quantity == newItem.quantity
        }
    }
}

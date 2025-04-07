package com.example.electricianapp.presentation.adapter // Corrected package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemApplianceBinding // Import ViewBinding
import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import

class ApplianceAdapter(
    private val listener: ApplianceItemListener
) : ListAdapter<Appliance, ApplianceAdapter.ApplianceViewHolder>(ApplianceDiffCallback()) {

    interface ApplianceItemListener {
        fun onEditAppliance(position: Int, appliance: Appliance)
        fun onRemoveAppliance(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        // Inflate using ViewBinding
        val binding = ItemApplianceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplianceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        val appliance = getItem(position)
        // Pass adapterPosition to listener methods if needed, otherwise it's not required in bind
        holder.bind(appliance, listener)
    }

    // Update ViewHolder to use ViewBinding
    class ApplianceViewHolder(private val binding: ItemApplianceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appliance: Appliance, listener: ApplianceItemListener) {
            binding.applianceNameTextView.text = appliance.name

            val details = buildString {
                append("${appliance.wattage}W")
                if (appliance.quantity > 1) {
                    append(" × ${appliance.quantity}")
                }
                // Only show demand factor if it's not 100% (1.0)
                if (appliance.demandFactor < 1.0 && appliance.demandFactor >= 0) {
                    append(" (${(appliance.demandFactor * 100).toInt()}% demand factor)")
                }
            }
            binding.applianceDetailsTextView.text = details // Assign details string

            binding.editButton.setOnClickListener {
                // Use adapterPosition for accurate item position
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onEditAppliance(adapterPosition, appliance)
                }
            }

            binding.removeButton.setOnClickListener {
                // Use adapterPosition for accurate item position
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onRemoveAppliance(adapterPosition)
                }
            }
        }
    }

    class ApplianceDiffCallback : DiffUtil.ItemCallback<Appliance>() {
        override fun areItemsTheSame(oldItem: Appliance, newItem: Appliance): Boolean {
            // Assuming name might not be unique, rely on object identity or add a unique ID to Appliance model
            return oldItem === newItem // Placeholder: Use unique ID if available
        }

        override fun areContentsTheSame(oldItem: Appliance, newItem: Appliance): Boolean {
            return oldItem == newItem // Data class equals checks all properties
        }
    }
}

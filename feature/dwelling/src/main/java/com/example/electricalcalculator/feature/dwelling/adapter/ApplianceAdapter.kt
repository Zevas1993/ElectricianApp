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
import com.example.electricalcalculator.domain.model.dwellingload.Appliance

class ApplianceAdapter(
    private val listener: ApplianceItemListener
) : ListAdapter<Appliance, ApplianceAdapter.ApplianceViewHolder>(ApplianceDiffCallback()) {

    interface ApplianceItemListener {
        fun onEditAppliance(position: Int, appliance: Appliance)
        fun onRemoveAppliance(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        val appliance = getItem(position)
        holder.bind(appliance, position, listener)
    }

    class ApplianceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val applianceNameTextView: TextView = itemView.findViewById(R.id.applianceNameTextView)
        private val applianceDetailsTextView: TextView = itemView.findViewById(R.id.applianceDetailsTextView)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(appliance: Appliance, position: Int, listener: ApplianceItemListener) {
            applianceNameTextView.text = appliance.name
            
            val details = buildString {
                append("${appliance.wattage}W")
                if (appliance.quantity > 1) {
                    append(" × ${appliance.quantity}")
                }
                if (appliance.demandFactor < 1.0) {
                    append(" (${appliance.demandFactor * 100}% demand factor)")
                }
            }
            
            applianceDetailsTextView.text = details
            
            editButton.setOnClickListener {
                listener.onEditAppliance(position, appliance)
            }
            
            removeButton.setOnClickListener {
                listener.onRemoveAppliance(position)
            }
        }
    }

    class ApplianceDiffCallback : DiffUtil.ItemCallback<Appliance>() {
        override fun areItemsTheSame(oldItem: Appliance, newItem: Appliance): Boolean {
            // In a real app, you might want to use a unique ID
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Appliance, newItem: Appliance): Boolean {
            return oldItem.name == newItem.name &&
                   oldItem.wattage == newItem.wattage &&
                   oldItem.quantity == newItem.quantity &&
                   oldItem.demandFactor == newItem.demandFactor
        }
    }
}

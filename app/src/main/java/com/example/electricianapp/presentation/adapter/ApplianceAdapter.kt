package com.example.electricianapp.presentation.adapter // Corrected package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R // Corrected import
import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import

class ApplianceAdapter(
    private val listener: ApplianceItemListener
) : ListAdapter<Appliance, ApplianceAdapter.ApplianceViewHolder>(ApplianceDiffCallback()) {

    interface ApplianceItemListener {
        fun onEditAppliance(position: Int, appliance: Appliance)
        fun onRemoveAppliance(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
         // TODO: Replace R.layout.item_appliance with the actual layout file name if different
         // TODO: Fix layout inflation and view finding
         val view = View(parent.context) // Placeholder View
        // val view = LayoutInflater.from(parent.context)
        //     .inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        val appliance = getItem(position)
        holder.bind(appliance, position, listener)
    }

    class ApplianceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: Replace R.id.* with actual IDs from your item_appliance.xml layout
        // TODO: Fix findViewById calls
        // private val applianceNameTextView: TextView = itemView.findViewById(R.id.applianceNameTextView)
        // private val applianceDetailsTextView: TextView = itemView.findViewById(R.id.applianceDetailsTextView)
        // private val editButton: Button = itemView.findViewById(R.id.editButton)
        // private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(appliance: Appliance, position: Int, listener: ApplianceItemListener) {
            // applianceNameTextView.text = appliance.name

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

            // applianceDetailsTextView.text = details

            // editButton.setOnClickListener {
            //     listener.onEditAppliance(position, appliance)
            // }

            // removeButton.setOnClickListener {
            //     listener.onRemoveAppliance(position)
            // }
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

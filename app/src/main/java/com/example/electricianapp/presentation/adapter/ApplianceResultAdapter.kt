package com.example.electricianapp.presentation.adapter // Corrected package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R // Corrected import
import com.example.electricianapp.presentation.fragment.dwellingload.DwellingLoadResultsFragment // Corrected import
import java.text.NumberFormat

class ApplianceResultAdapter : ListAdapter<DwellingLoadResultsFragment.ApplianceResult, ApplianceResultAdapter.ApplianceResultViewHolder>(ApplianceResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceResultViewHolder {
        // TODO: Replace R.layout.item_appliance_result with the actual layout file name if different
        // TODO: Fix layout inflation and view finding
        val view = View(parent.context) // Placeholder View
        // val view = LayoutInflater.from(parent.context)
        //     .inflate(R.layout.item_appliance_result, parent, false)
        return ApplianceResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceResultViewHolder, position: Int) {
        val applianceResult = getItem(position)
        holder.bind(applianceResult)
    }

    class ApplianceResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         // TODO: Replace R.id.* with actual IDs from your item_appliance_result.xml layout
         // TODO: Fix findViewById calls
        // private val applianceNameTextView: TextView = itemView.findViewById(R.id.applianceNameTextView)
        // private val connectedLoadTextView: TextView = itemView.findViewById(R.id.connectedLoadTextView)
        // private val demandFactorTextView: TextView = itemView.findViewById(R.id.demandFactorTextView)
        // private val demandLoadTextView: TextView = itemView.findViewById(R.id.demandLoadTextView)

        fun bind(applianceResult: DwellingLoadResultsFragment.ApplianceResult) {
            val numberFormat = NumberFormat.getNumberInstance().apply {
                maximumFractionDigits = 1
            }

            // applianceNameTextView.text = applianceResult.name
            // connectedLoadTextView.text = "${numberFormat.format(applianceResult.connectedLoad)} VA"
            // demandFactorTextView.text = "${(applianceResult.demandFactor * 100).toInt()}%"
            // demandLoadTextView.text = "${numberFormat.format(applianceResult.demandLoad)} VA"
        }
    }

    class ApplianceResultDiffCallback : DiffUtil.ItemCallback<DwellingLoadResultsFragment.ApplianceResult>() {
        override fun areItemsTheSame(oldItem: DwellingLoadResultsFragment.ApplianceResult, newItem: DwellingLoadResultsFragment.ApplianceResult): Boolean {
            // Assuming name is unique enough for item identity
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: DwellingLoadResultsFragment.ApplianceResult, newItem: DwellingLoadResultsFragment.ApplianceResult): Boolean {
            // Data class equals checks all properties
            return oldItem == newItem
        }
    }
}

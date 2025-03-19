package com.example.electricalcalculator.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricalcalculator.R
import com.example.electricalcalculator.presentation.fragment.dwellingload.DwellingLoadResultsFragment.ApplianceResult
import java.text.NumberFormat

class ApplianceResultAdapter : ListAdapter<ApplianceResult, ApplianceResultAdapter.ApplianceResultViewHolder>(ApplianceResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appliance_result, parent, false)
        return ApplianceResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceResultViewHolder, position: Int) {
        val applianceResult = getItem(position)
        holder.bind(applianceResult)
    }

    class ApplianceResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val applianceNameTextView: TextView = itemView.findViewById(R.id.applianceNameTextView)
        private val connectedLoadTextView: TextView = itemView.findViewById(R.id.connectedLoadTextView)
        private val demandFactorTextView: TextView = itemView.findViewById(R.id.demandFactorTextView)
        private val demandLoadTextView: TextView = itemView.findViewById(R.id.demandLoadTextView)

        fun bind(applianceResult: ApplianceResult) {
            val numberFormat = NumberFormat.getNumberInstance().apply {
                maximumFractionDigits = 1
            }
            
            applianceNameTextView.text = applianceResult.name
            connectedLoadTextView.text = "${numberFormat.format(applianceResult.connectedLoad)} VA"
            demandFactorTextView.text = "${(applianceResult.demandFactor * 100).toInt()}%"
            demandLoadTextView.text = "${numberFormat.format(applianceResult.demandLoad)} VA"
        }
    }

    class ApplianceResultDiffCallback : DiffUtil.ItemCallback<ApplianceResult>() {
        override fun areItemsTheSame(oldItem: ApplianceResult, newItem: ApplianceResult): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ApplianceResult, newItem: ApplianceResult): Boolean {
            return oldItem == newItem
        }
    }
}

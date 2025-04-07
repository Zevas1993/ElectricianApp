package com.example.electricianapp.presentation.adapter // Corrected package

import android.view.LayoutInflater // Keep only one import
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemApplianceResultBinding // Import ViewBinding
import com.example.electricianapp.presentation.fragment.dwellingload.DwellingLoadResultsFragment // Corrected import
import java.text.NumberFormat

class ApplianceResultAdapter : ListAdapter<DwellingLoadResultsFragment.ApplianceResult, ApplianceResultAdapter.ApplianceResultViewHolder>(ApplianceResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceResultViewHolder {
        // Inflate using ViewBinding
        val binding = ItemApplianceResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplianceResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplianceResultViewHolder, position: Int) {
        val applianceResult = getItem(position)
        holder.bind(applianceResult)
    }

    // Update ViewHolder to use ViewBinding
    class ApplianceResultViewHolder(private val binding: ItemApplianceResultBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(applianceResult: DwellingLoadResultsFragment.ApplianceResult) {
            val numberFormat = NumberFormat.getNumberInstance().apply {
                maximumFractionDigits = 1
            }
            val vaFormat = { value: Double -> "${numberFormat.format(value)} VA" }

            binding.applianceNameTextView.text = applianceResult.name
            binding.connectedLoadTextView.text = "Connected: ${vaFormat(applianceResult.connectedLoad)}"
            binding.demandFactorTextView.text = "Demand Factor: ${(applianceResult.demandFactor * 100).toInt()}%"
            binding.demandLoadTextView.text = vaFormat(applianceResult.demandLoad)
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

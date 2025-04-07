package com.example.electricianapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemHomeQuickAccessBinding

// Data class to represent a quick access item
data class QuickAccessItem(
    val id: String, // Unique ID for DiffUtil
    val label: String,
    @DrawableRes val iconResId: Int, // Resource ID for the icon drawable
    val navigationActionId: Int // Navigation action ID from nav_graph.xml
    // Add arguments Bundle if needed for navigation
)

class HomeQuickAccessAdapter(
    private val navController: NavController
) : ListAdapter<QuickAccessItem, HomeQuickAccessAdapter.ViewHolder>(QuickAccessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeQuickAccessBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, navController)
    }

    class ViewHolder(private val binding: ItemHomeQuickAccessBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuickAccessItem, navController: NavController) {
            binding.textViewLabel.text = item.label
            binding.imageViewIcon.setImageResource(item.iconResId)

            binding.root.setOnClickListener {
                try {
                    // Navigate using the provided action ID
                    // TODO: Handle arguments if needed (e.g., passing userId)
                    navController.navigate(item.navigationActionId)
                } catch (e: IllegalArgumentException) {
                    // Handle cases where the destination is not found (should not happen if IDs are correct)
                    // Log error or show a message
                    android.util.Log.e("HomeQuickAccessAdapter", "Navigation failed for action ID: ${item.navigationActionId}", e)
                }
            }
        }
    }

    class QuickAccessDiffCallback : DiffUtil.ItemCallback<QuickAccessItem>() {
        override fun areItemsTheSame(oldItem: QuickAccessItem, newItem: QuickAccessItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuickAccessItem, newItem: QuickAccessItem): Boolean {
            return oldItem == newItem
        }
    }
}

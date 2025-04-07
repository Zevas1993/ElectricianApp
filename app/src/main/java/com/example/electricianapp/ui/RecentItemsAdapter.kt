package com.example.electricianapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemDrawerRecentBinding // Use the correct binding class name

class RecentItemsAdapter(
    private val navController: NavController,
    private val closeDrawer: () -> Unit // Callback to close the drawer after navigation
) : ListAdapter<RecentItem, RecentItemsAdapter.ViewHolder>(RecentItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrawerRecentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, navController, closeDrawer)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemDrawerRecentBinding,
        private val navController: NavController,
        private val closeDrawer: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentItem) {
            binding.textViewRecentLabel.text = item.label
            binding.imageViewRecentIcon.setImageResource(item.iconResId)

            binding.root.setOnClickListener {
                try {
                    navController.navigate(item.navigationAction)
                    closeDrawer() // Close the drawer after navigating
                } catch (e: IllegalArgumentException) {
                    // Handle navigation errors
                    android.util.Log.e("RecentItemsAdapter", "Navigation failed for action: ${item.navigationAction}", e)
                }
            }
        }
    }

    class RecentItemDiffCallback : DiffUtil.ItemCallback<RecentItem>() {
        override fun areItemsTheSame(oldItem: RecentItem, newItem: RecentItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecentItem, newItem: RecentItem): Boolean {
            // Compare timestamp as well for potential updates to the same item
            return oldItem == newItem
        }
    }
}

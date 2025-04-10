package com.example.electricianapp.presentation.adapter.materials

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R
import com.example.electricianapp.databinding.ItemMaterialInventoryBinding
import com.example.electricianapp.domain.model.materials.MaterialInventory
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying a list of material inventory items in a RecyclerView
 */
class MaterialInventoryAdapter(
    private val onItemClick: (MaterialInventory) -> Unit,
    private val onAdjustClick: (MaterialInventory) -> Unit
) : ListAdapter<MaterialInventory, MaterialInventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemMaterialInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventory = getItem(position)
        holder.bind(inventory)
    }
    
    inner class InventoryViewHolder(private val binding: ItemMaterialInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            
            binding.buttonAdjust.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAdjustClick(getItem(position))
                }
            }
        }
        
        fun bind(inventory: MaterialInventory) {
            binding.apply {
                // Material info
                textViewMaterialName.text = inventory.material?.name ?: "Unknown Material"
                textViewLocation.text = if (inventory.location.isNotEmpty()) inventory.location else "No location specified"
                textViewLastUpdated.text = "Last updated: ${dateFormat.format(inventory.lastUpdated)}"
                
                // Quantity info
                textViewQuantity.text = inventory.quantity.toString()
                textViewUnitOfMeasure.text = inventory.material?.unitOfMeasure?.name ?: ""
                
                // Low stock indicator
                val isLowStock = inventory.quantity <= inventory.minimumQuantity
                if (isLowStock) {
                    textViewQuantity.setTextColor(ContextCompat.getColor(root.context, R.color.design_default_color_error))
                    textViewLowStock.visibility = android.view.View.VISIBLE
                } else {
                    textViewQuantity.setTextColor(ContextCompat.getColor(root.context, R.color.design_default_color_on_surface))
                    textViewLowStock.visibility = android.view.View.GONE
                }
            }
        }
    }
    
    class InventoryDiffCallback : DiffUtil.ItemCallback<MaterialInventory>() {
        override fun areItemsTheSame(oldItem: MaterialInventory, newItem: MaterialInventory): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: MaterialInventory, newItem: MaterialInventory): Boolean {
            return oldItem == newItem
        }
    }
}

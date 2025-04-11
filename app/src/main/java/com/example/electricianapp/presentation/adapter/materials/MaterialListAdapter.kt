package com.example.electricianapp.presentation.adapter.materials

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemMaterialBinding
import com.example.electricianapp.domain.model.materials.Material

/**
 * Adapter for displaying a list of materials in a RecyclerView
 */
class MaterialListAdapter(
    private val onItemClick: (Material) -> Unit
) : ListAdapter<Material, MaterialListAdapter.MaterialViewHolder>(MaterialDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MaterialViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = getItem(position)
        holder.bind(material)
    }
    
    inner class MaterialViewHolder(
        private val binding: ItemMaterialBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition // Use adapterPosition as workaround
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }
        
        fun bind(material: Material) {
            binding.apply {
                textViewMaterialName.text = material.name
                textViewMaterialDescription.text = material.description
                textViewMaterialCategory.text = material.category.name.replace("_", " ")
                textViewMaterialPrice.text = "$${material.unitPrice}"
                
                // Load image if available
                material.imageUrl?.let { url ->
                    // Use an image loading library like Glide or Coil
                    // For now, we'll just set a placeholder
                    // imageViewMaterial.setImageResource(R.drawable.placeholder_material)
                }
            }
        }
    }
    
    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    class MaterialDiffCallback : DiffUtil.ItemCallback<Material>() {
        override fun areItemsTheSame(oldItem: Material, newItem: Material): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Material, newItem: Material): Boolean {
            return oldItem == newItem
        }
    }
}

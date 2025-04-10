package com.example.electricianapp.presentation.adapter.photodoc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electricianapp.databinding.ItemPhotoDocumentBinding
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying photo documents in a RecyclerView
 */
class PhotoDocAdapter(
    private val onItemClick: (PhotoDocument) -> Unit
) : ListAdapter<PhotoDocument, PhotoDocAdapter.PhotoDocViewHolder>(PhotoDocDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoDocViewHolder {
        val binding = ItemPhotoDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoDocViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PhotoDocViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for photo document items
     */
    class PhotoDocViewHolder(
        private val binding: ItemPhotoDocumentBinding,
        private val onItemClick: (PhotoDocument) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(photo: PhotoDocument) {
            binding.textViewTitle.text = photo.title
            binding.textViewDate.text = dateFormat.format(photo.dateCreated)

            // Load thumbnail or full image if thumbnail is not available
            val imageUri = photo.thumbnailUri ?: photo.photoUri
            Glide.with(binding.root.context)
                .load(imageUri)
                .centerCrop()
                .into(binding.imageViewPhoto)

            // Set tags
            if (photo.tags.isNotEmpty()) {
                binding.textViewTags.text = photo.tags.joinToString(", ")
            } else {
                binding.textViewTags.text = "No tags"
            }

            // Set click listener
            binding.root.setOnClickListener {
                onItemClick(photo)
            }
        }
    }

    /**
     * DiffUtil callback for photo documents
     */
    class PhotoDocDiffCallback : DiffUtil.ItemCallback<PhotoDocument>() {
        override fun areItemsTheSame(oldItem: PhotoDocument, newItem: PhotoDocument): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoDocument, newItem: PhotoDocument): Boolean {
            return oldItem == newItem
        }
    }
}

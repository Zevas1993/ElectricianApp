package com.example.electricianapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.databinding.ItemNecArticleBinding
import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.google.android.material.chip.Chip

/**
 * Adapter for displaying NEC articles in a RecyclerView
 */
class NecArticleAdapter(
    private val onItemClick: (NecArticle) -> Unit
) : ListAdapter<NecArticle, NecArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemNecArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(
        private val binding: ItemNecArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(article: NecArticle) {
            binding.apply {
                textViewArticleNumber.text = article.articleNumber
                textViewTitle.text = article.title
                textViewSummary.text = article.summary
                textViewYear.text = "NEC ${article.year}"
                textViewCategory.text = "Category: ${formatCategory(article.category.name)}"

                // Set up tags
                chipGroupTags.removeAllViews()
                article.tags.take(3).forEach { tag ->
                    val chip = Chip(root.context)
                    chip.text = tag
                    chipGroupTags.addView(chip)
                }
            }
        }

        private fun formatCategory(category: String): String {
            return category.replace("_", " ").split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
        }
    }

    private class ArticleDiffCallback : DiffUtil.ItemCallback<NecArticle>() {
        override fun areItemsTheSame(oldItem: NecArticle, newItem: NecArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NecArticle, newItem: NecArticle): Boolean {
            return oldItem == newItem
        }
    }
}

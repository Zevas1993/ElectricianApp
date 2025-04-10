package com.example.electricianapp.presentation.adapter.materials

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R
import com.example.electricianapp.databinding.ItemTransactionHistoryBinding
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.model.materials.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying a list of material transactions in a RecyclerView
 */
class TransactionHistoryAdapter : ListAdapter<MaterialTransaction, TransactionHistoryAdapter.TransactionViewHolder>(TransactionDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }
    
    inner class TransactionViewHolder(private val binding: ItemTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        
        fun bind(transaction: MaterialTransaction) {
            binding.apply {
                // Transaction type and icon
                val (typeText, isAddition) = when (transaction.transactionType) {
                    TransactionType.PURCHASE -> "Purchase" to true
                    TransactionType.USE -> "Used" to false
                    TransactionType.RETURN_TO_INVENTORY -> "Return to Inventory" to true
                    TransactionType.RETURN_TO_SUPPLIER -> "Return to Supplier" to false
                    TransactionType.ADJUSTMENT -> "Adjustment" to (transaction.quantity > 0)
                    TransactionType.TRANSFER -> "Transfer" to false
                }
                
                textViewTransactionType.text = typeText
                
                // Quantity with sign
                val quantityPrefix = if (isAddition) "+" else "-"
                val absQuantity = Math.abs(transaction.quantity)
                textViewQuantity.text = "$quantityPrefix$absQuantity"
                
                // Set color based on addition or subtraction
                val textColor = if (isAddition) {
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                } else {
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                }
                textViewQuantity.setTextColor(textColor)
                
                // Date and notes
                textViewDate.text = dateFormat.format(transaction.date)
                textViewNotes.text = transaction.notes
                
                // Show/hide notes
                if (transaction.notes.isBlank()) {
                    textViewNotes.visibility = android.view.View.GONE
                } else {
                    textViewNotes.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
    
    class TransactionDiffCallback : DiffUtil.ItemCallback<MaterialTransaction>() {
        override fun areItemsTheSame(oldItem: MaterialTransaction, newItem: MaterialTransaction): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: MaterialTransaction, newItem: MaterialTransaction): Boolean {
            return oldItem == newItem
        }
    }
}

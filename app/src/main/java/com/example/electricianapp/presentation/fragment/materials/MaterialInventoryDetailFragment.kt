package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.databinding.FragmentMaterialInventoryDetailBinding
import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.presentation.adapter.materials.TransactionHistoryAdapter
import com.example.electricianapp.presentation.viewmodel.materials.MaterialInventoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for displaying detailed information about a material inventory item
 */
@AndroidEntryPoint
class MaterialInventoryDetailFragment : Fragment() {
    
    private var _binding: FragmentMaterialInventoryDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialInventoryViewModel by viewModels()
    private val args: MaterialInventoryDetailFragmentArgs by navArgs()
    
    private lateinit var transactionAdapter: TransactionHistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialInventoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Load inventory item details
        viewModel.loadInventoryItem(args.inventoryId)
        
        // Load transaction history
        viewModel.loadTransactionHistory(args.inventoryId)
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter()
        
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }
    
    private fun setupObservers() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        viewModel.selectedInventory.observe(viewLifecycleOwner) { inventory ->
            inventory?.let {
                // Material info
                binding.textViewMaterialName.text = it.material?.name ?: "Unknown Material"
                binding.textViewMaterialDescription.text = it.material?.description ?: ""
                binding.textViewMaterialCode.text = "Code: ${it.material?.code ?: "N/A"}"
                
                // Quantity info
                binding.textViewQuantity.text = "${it.quantity} ${it.material?.unitOfMeasure?.name ?: ""}"
                binding.textViewMinimumQuantity.text = "Minimum: ${it.minimumQuantity} ${it.material?.unitOfMeasure?.name ?: ""}"
                
                // Location info
                binding.textViewLocation.text = if (it.location.isNotEmpty()) it.location else "No location specified"
                
                // Last updated
                binding.textViewLastUpdated.text = "Last updated: ${dateFormat.format(it.lastUpdated)}"
                
                // Low stock indicator
                val isLowStock = it.quantity <= it.minimumQuantity
                if (isLowStock) {
                    binding.textViewLowStock.visibility = View.VISIBLE
                } else {
                    binding.textViewLowStock.visibility = View.GONE
                }
            }
        }
        
        viewModel.transactionHistory.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            
            // Show empty state if no transactions
            binding.emptyStateLayout.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewTransactions.visibility = if (transactions.isEmpty()) View.GONE else View.VISIBLE
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.textViewError.text = errorMessage
                binding.textViewError.visibility = View.VISIBLE
            } else {
                binding.textViewError.visibility = View.GONE
            }
        }
    }
    
    private fun setupListeners() {
        binding.fabAdjust.setOnClickListener {
            // Show adjustment dialog
            val dialog = MaterialInventoryAdjustmentDialogFragment.newInstance(args.inventoryId)
            dialog.show(childFragmentManager, "AdjustmentDialog")
        }
        
        binding.buttonEdit.setOnClickListener {
            // Navigate to edit screen
            val action = MaterialInventoryDetailFragmentDirections.actionMaterialInventoryDetailFragmentToAddEditInventoryFragment(
                args.inventoryId,
                "Edit Inventory Item"
            )
            findNavController().navigate(action)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

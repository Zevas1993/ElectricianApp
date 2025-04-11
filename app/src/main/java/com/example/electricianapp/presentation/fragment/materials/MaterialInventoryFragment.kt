package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentMaterialInventoryBinding
import com.example.electricianapp.domain.model.materials.MaterialInventory // Added correct import
import com.example.electricianapp.presentation.adapter.materials.MaterialInventoryAdapter
import com.example.electricianapp.presentation.viewmodel.materials.MaterialInventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying and managing material inventory
 */
@AndroidEntryPoint
class MaterialInventoryFragment : Fragment() {
    
    private var _binding: FragmentMaterialInventoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialInventoryViewModel by viewModels()
    private lateinit var adapter: MaterialInventoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Load inventory when the fragment is created
        viewModel.loadInventory()
    }
    
    private fun setupRecyclerView() {
        adapter = MaterialInventoryAdapter(
            onItemClick = { inventory ->
                // Navigate to inventory detail screen
                val action = MaterialInventoryFragmentDirections.actionMaterialInventoryFragmentToMaterialInventoryDetailFragment(inventory.id)
                findNavController().navigate(action)
            },
            onAdjustClick = { inventory ->
                // Show adjustment dialog
                showAdjustmentDialog(inventory)
            }
        )
        
        binding.recyclerViewInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MaterialInventoryFragment.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.inventory.observe(viewLifecycleOwner) { inventoryItems ->
            adapter.submitList(inventoryItems)
            
            // Show empty state if no inventory items
            binding.emptyStateLayout.visibility = if (inventoryItems.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewInventory.visibility = if (inventoryItems.isEmpty()) View.GONE else View.VISIBLE
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                // Show error message
                binding.errorTextView.text = errorMessage
                binding.errorTextView.visibility = View.VISIBLE
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }
        
        viewModel.lowStockItems.observe(viewLifecycleOwner) { lowStockItems ->
            if (lowStockItems.isNotEmpty()) {
                binding.lowStockBanner.visibility = View.VISIBLE
                binding.textViewLowStockCount.text = lowStockItems.size.toString()
            } else {
                binding.lowStockBanner.visibility = View.GONE
            }
        }
    }
    
    private fun setupListeners() {
        binding.fabAddInventory.setOnClickListener {
            // Navigate to add inventory screen
            val action = MaterialInventoryFragmentDirections.actionMaterialInventoryFragmentToAddEditInventoryFragment(null)
            findNavController().navigate(action)
        }
        
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchInventory(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.loadInventory()
                } else if (newText.length >= 3) {
                    viewModel.searchInventory(newText)
                }
                return true
            }
        })
        
        binding.filterButton.setOnClickListener {
            // Show filter dialog
            MaterialInventoryFilterDialogFragment().show(childFragmentManager, "MaterialInventoryFilterDialog")
        }
        
        binding.lowStockBanner.setOnClickListener {
            // Show only low stock items
            viewModel.showLowStockItems()
        }
    }
    
    private fun showAdjustmentDialog(inventory: MaterialInventory) {
        // Create and show adjustment dialog
        val dialog = MaterialInventoryAdjustmentDialogFragment.newInstance(inventory.id)
        dialog.show(childFragmentManager, "AdjustmentDialog")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

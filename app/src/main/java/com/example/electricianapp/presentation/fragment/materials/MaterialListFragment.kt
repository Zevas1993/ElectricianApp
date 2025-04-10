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
import com.example.electricianapp.databinding.FragmentMaterialListBinding
import com.example.electricianapp.presentation.adapter.materials.MaterialListAdapter
import com.example.electricianapp.presentation.viewmodel.materials.MaterialListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying a list of materials
 */
@AndroidEntryPoint
class MaterialListFragment : Fragment() {
    
    private var _binding: FragmentMaterialListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialListViewModel by viewModels()
    private lateinit var adapter: MaterialListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Load materials when the fragment is created
        viewModel.loadMaterials()
    }
    
    private fun setupRecyclerView() {
        adapter = MaterialListAdapter { material ->
            // Navigate to material detail screen
            val action = MaterialListFragmentDirections.actionMaterialListFragmentToMaterialDetailFragment(material.id)
            findNavController().navigate(action)
        }
        
        binding.recyclerViewMaterials.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MaterialListFragment.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.materials.observe(viewLifecycleOwner) { materials ->
            adapter.submitList(materials)
            
            // Show empty state if no materials
            binding.emptyStateLayout.visibility = if (materials.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewMaterials.visibility = if (materials.isEmpty()) View.GONE else View.VISIBLE
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
    }
    
    private fun setupListeners() {
        binding.fabAddMaterial.setOnClickListener {
            // Navigate to add material screen
            val action = MaterialListFragmentDirections.actionMaterialListFragmentToAddEditMaterialFragment(null)
            findNavController().navigate(action)
        }
        
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMaterials(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.loadMaterials()
                } else if (newText.length >= 3) {
                    viewModel.searchMaterials(newText)
                }
                return true
            }
        })
        
        binding.filterButton.setOnClickListener {
            // Show filter dialog
            MaterialFilterDialogFragment().show(childFragmentManager, "MaterialFilterDialog")
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

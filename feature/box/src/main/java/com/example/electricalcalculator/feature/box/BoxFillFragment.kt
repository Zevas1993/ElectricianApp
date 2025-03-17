package com.example.electricalcalculator.feature.box

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricalcalculator.domain.model.Wire
import com.example.electricalcalculator.feature.box.adapter.WireAdapter
import com.example.electricalcalculator.feature.box.databinding.FragmentBoxFillBinding
import com.example.electricalcalculator.feature.box.viewmodel.BoxFillViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for Box Fill calculations.
 * Allows users to add wires and other components to calculate box fill requirements.
 */
@AndroidEntryPoint
class BoxFillFragment : Fragment() {

    private var _binding: FragmentBoxFillBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BoxFillViewModel by viewModels()
    private lateinit var wireAdapter: WireAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoxFillBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupListeners()
        observeViewState()
    }
    
    private fun setupRecyclerView() {
        wireAdapter = WireAdapter(
            onDeleteClick = { wire -> viewModel.removeWire(wire) }
        )
        
        binding.wireRecyclerView.apply {
            adapter = wireAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupListeners() {
        binding.addWireButton.setOnClickListener {
            showAddWireDialog()
        }
        
        binding.calculateButton.setOnClickListener {
            viewModel.calculateBoxFill()
        }
        
        binding.boxTypeSpinner.apply {
            // Set up spinner with box types
        }
    }
    
    private fun observeViewState() {
        viewModel.wires.observe(viewLifecycleOwner) { wires ->
            wireAdapter.submitList(wires)
            updateUiBasedOnWires(wires)
        }
        
        viewModel.boxFillResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                navigateToResults(result.id)
            }
        }
    }
    
    private fun updateUiBasedOnWires(wires: List<Wire>) {
        binding.emptyStateLayout.visibility = if (wires.isEmpty()) View.VISIBLE else View.GONE
        binding.wireRecyclerView.visibility = if (wires.isEmpty()) View.GONE else View.VISIBLE
        binding.calculateButton.isEnabled = wires.isNotEmpty()
    }
    
    private fun showAddWireDialog() {
        // Show dialog to add a wire
    }
    
    private fun navigateToResults(resultId: Long) {
        // Navigate to results fragment
        val action = BoxFillFragmentDirections.actionNavigationBoxFillToBoxFillResults(resultId)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
